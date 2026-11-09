package com.awra.catalogue.data.repository

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.awra.catalogue.data.dao.CatalogueDao
import com.awra.catalogue.data.database.DatabaseSeeder
import com.awra.catalogue.data.model.*
import com.awra.catalogue.data.security.HandoutEncryptionManager
import com.awra.catalogue.data.seed.CatalogueMetadata
import com.awra.catalogue.data.util.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URI

class CatalogueRepository(
    private val catalogueDao: CatalogueDao,
    private val preferenceManager: PreferenceManager? = null
) {

    fun getSubjects(stream: Stream): Flow<List<Subject>> =
        catalogueDao.getSubjectsByStream(stream).distinctUntilChanged()

    fun getAllSubjects(): Flow<List<Subject>> =
        catalogueDao.getAllSubjects().distinctUntilChanged()

    suspend fun getSubjectById(subjectId: Long): Subject? =
        catalogueDao.getSubjectsById(subjectId)

    fun getChaptersFromMetadata(subjectName: String): List<Pair<String, List<String>>> =
        CatalogueMetadata.getUnitsForSubject(subjectName)

    suspend fun getSubjectCount(): Int = catalogueDao.getSubjectCount()

    suspend fun seedDatabase(context: Context) = DatabaseSeeder.seed(context, catalogueDao)

    suspend fun seedSubjects(subjects: List<Subject>) = catalogueDao.insertSubjects(subjects)

    fun getChapters(subjectId: Long): Flow<List<ChapterWithTopics>> =
        catalogueDao.getChaptersWithHandoutsBySubject(subjectId).distinctUntilChanged()

    fun getChaptersWithQuizzes(subjectId: Long): Flow<List<ChapterWithQuizTopics>> =
        catalogueDao.getChaptersWithQuizzesBySubject(subjectId).distinctUntilChanged()

    fun getChaptersWithInfographics(subjectId: Long): Flow<List<ChapterWithInfographicTopics>> =
        catalogueDao.getChaptersWithInfographicsBySubject(subjectId).distinctUntilChanged()

    fun getTopics(chapterId: Long): Flow<List<Topic>> =
        catalogueDao.getTopicsByChapter(chapterId).distinctUntilChanged()

    suspend fun getTopicById(id: Long) = catalogueDao.getTopicById(id)

    suspend fun getChapterById(id: Long) = catalogueDao.getChapterById(id)

    fun getHandouts(topicId: Long): Flow<List<Handout>> =
        catalogueDao.getHandoutsByTopic(topicId).distinctUntilChanged()

    suspend fun getHandoutById(id: Long): Handout? = catalogueDao.getHandoutById(id)

    fun isHandoutDownloaded(context: Context, handout: Handout): Boolean {
        return HandoutEncryptionManager.isHandoutDownloaded(context, handout.content)
    }

    fun getEncryptedHandoutContent(context: Context, handout: Handout): String? {
        val decrypted = HandoutEncryptionManager.decryptAndRead(context, handout.content)
        if (decrypted != null) return decrypted

        return try {
            context.assets.open(handout.content).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun downloadAndEncryptHandout(
        context: Context,
        handout: Handout,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val cleanPath = handout.content.removePrefix("handouts/").removePrefix("/")
        val rawPath = handout.content.removePrefix("/")

        val baseUrl = GITHUB_HANDOUTS_BASE_URL.removePrefix("https://").removePrefix("http://")
        val domain = baseUrl.substringBefore("/")
        val baseUriPath = "/" + baseUrl.substringAfter("/", "").trimEnd('/')

        val candidatePaths = listOf(
            "$baseUriPath/$cleanPath",
            "$baseUriPath/$rawPath"
        ).distinct()

        var lastError: Exception? = null

        for (path in candidatePaths) {
            var connection: HttpURLConnection? = null
            try {
                val uri = URI("https", domain, path, null)
                val url = uri.toURL()

                connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 15000
                    requestMethod = "GET"
                    instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "Mozilla/5.0")
                    connect()
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val contentLength = connection.contentLength
                    val outputStream = ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    var totalRead = 0L
                    var bytesRead: Int

                    connection.inputStream.use { inputStream ->
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalRead += bytesRead
                            if (contentLength > 0) {
                                val progress = (totalRead.toFloat() / contentLength).coerceIn(0f, 1f)
                                onProgress?.invoke(progress)
                            }
                        }
                    }

                    val content = outputStream.toString("UTF-8")
                    val saved = HandoutEncryptionManager.encryptAndSave(context, handout.content, content)
                    if (saved) {
                        return@withContext Result.success(content)
                    } else {
                        return@withContext Result.failure(Exception("Failed to encrypt and save handout locally."))
                    }
                } else {
                    lastError = Exception("Server returned HTTP $responseCode: ${connection.responseMessage}")
                }
            } catch (e: Exception) {
                lastError = e
            } finally {
                connection?.disconnect()
            }
        }

        Result.failure(lastError ?: Exception("Failed to download handout."))
    }

    fun deleteDownloadedHandout(context: Context, handout: Handout): Boolean {
        return HandoutEncryptionManager.deleteHandout(context, handout.content)
    }

    companion object {
        var GITHUB_HANDOUTS_BASE_URL: String = "https://raw.githubusercontent.com/Awraedu/handouts/main/"
    }

    suspend fun getInfographicById(id: Long): Infographic? = catalogueDao.getInfographicById(id)

    fun getFlashcards(topicId: Long): Flow<List<Flashcard>> =
        catalogueDao.getFlashcardsByTopic(topicId).distinctUntilChanged()

    fun getCustomFlashcards(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<Flashcard>> =
        catalogueDao.getCustomFlashcards(chapterIds, difficulty, limit)

    fun getQuizQuestions(topicId: Long): Flow<List<QuizQuestion>> =
        catalogueDao.getQuizQuestionsByTopic(topicId).distinctUntilChanged()

    fun getCustomQuizQuestions(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<QuizQuestion>> =
        catalogueDao.getCustomQuizQuestions(chapterIds, difficulty, limit)

    fun getCustomQuizQuestionsFromTopics(topicIds: List<Long>, difficulty: Difficulty, limit: Int) =
        catalogueDao.getCustomQuizQuestionsFromTopics(topicIds, difficulty, limit)

    fun getTerms(topicId: Long): Flow<List<Term>> =
        catalogueDao.getTermsByTopic(topicId).distinctUntilChanged()

    fun getUtilities(subjectId: Long): Flow<List<Utility>> =
        catalogueDao.getUtilitiesBySubject(subjectId).distinctUntilChanged()

    suspend fun getUtilityById(id: Long): Utility? = catalogueDao.getUtilityById(id)

    fun getBookmarkedTerms(): Flow<List<Term>> =
        catalogueDao.getBookmarkedTerms().distinctUntilChanged()

    suspend fun toggleBookmark(term: Term) {
        catalogueDao.updateTerm(term.copy(isBookmarked = !term.isBookmarked))
    }

    fun searchTerms(subjectId: Long, query: String): Flow<List<Term>> =
        catalogueDao.searchTermsInSubject(subjectId, query).distinctUntilChanged()

    fun searchAllTerms(query: String): Flow<List<Term>> =
        catalogueDao.searchAllTerms(query).distinctUntilChanged()

    fun getFeaturedTerms(limit: Int = 50): Flow<List<Term>> =
        catalogueDao.getFeaturedTerms(limit).distinctUntilChanged()

    suspend fun recordQuizAttempt(quizQuestionId: Long, isCorrect: Boolean, timeTakenMs: Long) {
        catalogueDao.insertQuizAttempt(
            QuizAttempt(
                quizQuestionId = quizQuestionId,
                isCorrect = isCorrect,
                timeTakenMs = timeTakenMs
            )
        )
    }

    suspend fun recordFlashcardAttempt(flashcardId: Long, isCorrect: Boolean) {
        catalogueDao.insertFlashcardAttempt(
            FlashcardAttempt(
                flashcardId = flashcardId,
                isCorrect = isCorrect
            )
        )
    }

    suspend fun recordStudySession(topicId: Long, materialType: MaterialType, timeSpentMs: Long) {
        catalogueDao.insertStudySession(
            StudySession(
                topicId = topicId,
                materialType = materialType,
                timeSpentMs = timeSpentMs
            )
        )
    }

    fun getTopicAnalytics(topicId: Long) = catalogueDao.getTopicQuizAnalytics(topicId).distinctUntilChanged()

    fun getStudiedTopics() = catalogueDao.getStudiedTopics().distinctUntilChanged()

    fun getTotalQuizAttemptsForTopicsToday(topicIds: List<Long>, startOfDay: Long) =
        catalogueDao.getTotalQuizAttemptsForTopicsToday(topicIds, startOfDay).distinctUntilChanged()

    fun getTotalCorrectQuizAttemptsForTopicsToday(topicIds: List<Long>, startOfDay: Long) =
        catalogueDao.getTotalCorrectQuizAttemptsForTopicsToday(topicIds, startOfDay).distinctUntilChanged()

    fun getGlobalQuizAnalytics() = catalogueDao.getGlobalQuizAnalytics().distinctUntilChanged()

    fun getGlobalFlashcardCount() = catalogueDao.getGlobalFlashcardCount().distinctUntilChanged()

    fun getUniqueStudyDaysCount() = catalogueDao.getUniqueStudyDaysCount().distinctUntilChanged()

    suspend fun getStudySessionsCountToday(startOfDay: Long) =
        catalogueDao.getStudySessionsCountToday(startOfDay)

    fun getGlobalHandoutProgress() = catalogueDao.getGlobalHandoutProgress().distinctUntilChanged()

    fun getSubjectMastery() = catalogueDao.getSubjectMastery().distinctUntilChanged()

    fun getAllChaptersPerformance() = catalogueDao.getAllChaptersPerformance().distinctUntilChanged()

    fun getAllTopicsPerformance() = catalogueDao.getAllTopicsPerformance().distinctUntilChanged()

    fun getTimePerSubject() = catalogueDao.getTimePerSubject().distinctUntilChanged()

    fun getDailyStudyTime(since: Long) = catalogueDao.getDailyStudyTime(since).distinctUntilChanged()

    fun getPinnedItems(): Flow<List<RecentlyOpenedDisplayItem>> =
        catalogueDao.getPinnedItems()
            .distinctUntilChanged()
            .map { pins ->
                pins.mapNotNull { pin ->
                    when (pin.type) {
                        MaterialType.HANDOUT -> {
                            val handout = catalogueDao.getHandoutById(pin.targetId)
                            val topic = handout?.let { catalogueDao.getTopicById(it.topicId) }
                            val chapter = topic?.let { catalogueDao.getChapterById(it.chapterId) }
                            topic?.let {
                                RecentlyOpenedDisplayItem(
                                    id = pin.id,
                                    targetId = pin.targetId,
                                    subjectId = chapter?.subjectId ?: 0,
                                    name = it.title,
                                    type = MaterialType.HANDOUT,
                                    icon = "MenuBook",
                                    color = Color(0xFF795548),
                                    timestamp = pin.timestamp
                                )
                            }
                        }
                        MaterialType.INFOGRAPHIC -> {
                            val infographic = catalogueDao.getInfographicById(pin.targetId)
                            val topic = infographic?.let { catalogueDao.getTopicById(it.topicId) }
                            val chapter = topic?.let { catalogueDao.getChapterById(it.chapterId) }
                            topic?.let {
                                RecentlyOpenedDisplayItem(
                                    id = pin.id,
                                    targetId = pin.targetId,
                                    subjectId = chapter?.subjectId ?: 0,
                                    name = it.title,
                                    type = MaterialType.INFOGRAPHIC,
                                    icon = "BarChart",
                                    color = Color(0xFF3F51B5),
                                    timestamp = pin.timestamp
                                )
                            }
                        }
                        MaterialType.UTILITY -> {
                            val utility = catalogueDao.getUtilityById(pin.targetId)
                            utility?.let {
                                RecentlyOpenedDisplayItem(
                                    id = pin.id,
                                    targetId = pin.targetId,
                                    subjectId = it.subjectId,
                                    name = it.name,
                                    type = MaterialType.UTILITY,
                                    icon = it.icon,
                                    color = Color(0xFF009688),
                                    timestamp = pin.timestamp
                                )
                            }
                        }
                        MaterialType.PDF -> {
                            val customPdf = preferenceManager?.getAllCustomPdfs()?.find { it.id.hashCode().toLong() == pin.targetId }
                            RecentlyOpenedDisplayItem(
                                id = pin.id,
                                targetId = pin.targetId,
                                subjectId = 1L,
                                name = customPdf?.title ?: "Course PDF Document",
                                type = MaterialType.PDF,
                                icon = "pdfs",
                                color = Color(0xFF1976D2),
                                timestamp = pin.timestamp
                            )
                        }
                        else -> null
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .distinctUntilChanged()

    suspend fun togglePin(targetId: Long, type: MaterialType) {
        val isPinned = catalogueDao.isItemPinned(targetId, type).first()
        if (isPinned) {
            catalogueDao.removePinnedItem(targetId, type)
        } else {
            catalogueDao.insertPinnedItem(PinnedItem(targetId = targetId, type = type))
        }
    }

    fun isItemPinned(targetId: Long, type: MaterialType) =
        catalogueDao.isItemPinned(targetId, type).distinctUntilChanged()

    // Schedules
    fun getAllSchedules() = catalogueDao.getAllSchedules().distinctUntilChanged()
    suspend fun addSchedule(schedule: ClassSchedule) = catalogueDao.insertSchedule(schedule)
    suspend fun removeSchedule(schedule: ClassSchedule) = catalogueDao.deleteSchedule(schedule)
}