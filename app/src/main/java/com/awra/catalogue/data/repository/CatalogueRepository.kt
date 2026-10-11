package com.awra.catalogue.data.repository

import android.content.Context
import com.awra.catalogue.data.database.DatabaseSeeder
import com.awra.catalogue.data.dao.CatalogueDao
import com.awra.catalogue.data.model.*
import com.awra.catalogue.data.security.HandoutEncryptionManager
import com.awra.catalogue.data.seed.CatalogueMetadata
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class CatalogueRepository(private val catalogueDao: CatalogueDao) {

    fun getSubjects(stream: Stream): Flow<List<Subject>> = catalogueDao.getSubjectsByStream(stream)

    fun getAllSubjects(): Flow<List<Subject>> = catalogueDao.getAllSubjects()

    suspend fun getSubjectById(subjectId: Long): Subject? = catalogueDao.getSubjectsById(subjectId)
    
    fun getChaptersFromMetadata(subjectName: String): List<Pair<String, List<String>>> = 
        CatalogueMetadata.getUnitsForSubject(subjectName)

    suspend fun getSubjectCount(): Int = catalogueDao.getSubjectCount()

    suspend fun seedDatabase(context: Context) = DatabaseSeeder.seed(context, catalogueDao)

    suspend fun seedSubjects(subjects: List<Subject>) = catalogueDao.insertSubjects(subjects)

    fun getChapters(subjectId: Long): Flow<List<ChapterWithTopics>> = catalogueDao.getChaptersWithHandoutsBySubject(subjectId)

    fun getChaptersWithQuizzes(subjectId: Long): Flow<List<ChapterWithQuizTopics>> = catalogueDao.getChaptersWithQuizzesBySubject(subjectId)

    fun getChaptersWithInfographics(subjectId: Long): Flow<List<ChapterWithInfographicTopics>> = catalogueDao.getChaptersWithInfographicsBySubject(subjectId)

    fun getTopics(chapterId: Long): Flow<List<Topic>> = catalogueDao.getTopicsByChapter(chapterId)

    suspend fun getTopicById(id: Long) = catalogueDao.getTopicById(id)
    
    suspend fun getChapterById(id: Long) = catalogueDao.getChapterById(id)

    fun getHandouts(topicId: Long): Flow<List<Handout>> = catalogueDao.getHandoutsByTopic(topicId)

    suspend fun getHandoutById(id: Long): Handout? = catalogueDao.getHandoutById(id)

    fun isHandoutDownloaded(context: Context, handout: Handout): Boolean {
        return HandoutEncryptionManager.isHandoutDownloaded(context, handout.content)
    }

    fun getEncryptedHandoutContent(context: Context, handout: Handout): String? {
        // Try reading decrypted file from encrypted internal storage first
        val decrypted = HandoutEncryptionManager.decryptAndRead(context, handout.content)
        if (decrypted != null) return decrypted

        // Fallback to assets if available (for backwards compatibility)
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
        try {
            val downloadUrl = GITHUB_HANDOUTS_BASE_URL + handout.content
            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val contentLength = connection.contentLength
                val inputStream = connection.inputStream
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                var totalRead = 0L
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalRead += bytesRead
                    if (contentLength > 0) {
                        val progress = (totalRead.toFloat() / contentLength).coerceIn(0f, 1f)
                        onProgress?.invoke(progress)
                    }
                }

                val content = outputStream.toString("UTF-8")
                val saved = HandoutEncryptionManager.encryptAndSave(context, handout.content, content)
                if (saved) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Failed to encrypt and save handout locally."))
                }
            } else {
                Result.failure(Exception("Server returned HTTP ${connection.responseCode}: ${connection.responseMessage}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteDownloadedHandout(context: Context, handout: Handout): Boolean {
        return HandoutEncryptionManager.deleteHandout(context, handout.content)
    }

    companion object {
        var GITHUB_HANDOUTS_BASE_URL: String = "https://raw.githubusercontent.com/Awraedu/Catalogue/updated/"
    }

    suspend fun getInfographicById(id: Long): Infographic? = catalogueDao.getInfographicById(id)

    fun getFlashcards(topicId: Long): Flow<List<Flashcard>> = catalogueDao.getFlashcardsByTopic(topicId)

    fun getCustomFlashcards(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<Flashcard>> =
        catalogueDao.getCustomFlashcards(chapterIds, difficulty, limit)

    fun getQuizQuestions(topicId: Long): Flow<List<QuizQuestion>> = catalogueDao.getQuizQuestionsByTopic(topicId)

    fun getCustomQuizQuestions(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<QuizQuestion>> =
        catalogueDao.getCustomQuizQuestions(chapterIds, difficulty, limit)

    fun getCustomQuizQuestionsFromTopics(topicIds: List<Long>, difficulty: Difficulty, limit: Int) =
        catalogueDao.getCustomQuizQuestionsFromTopics(topicIds, difficulty, limit)

    fun getTerms(topicId: Long): Flow<List<Term>> = catalogueDao.getTermsByTopic(topicId)

    fun getUtilities(subjectId: Long): Flow<List<Utility>> = catalogueDao.getUtilitiesBySubject(subjectId)

    suspend fun getUtilityById(id: Long): Utility? = catalogueDao.getUtilityById(id)

    fun getBookmarkedTerms(): Flow<List<Term>> = catalogueDao.getBookmarkedTerms()

    suspend fun toggleBookmark(term: Term) {
        catalogueDao.updateTerm(term.copy(isBookmarked = !term.isBookmarked))
    }

    fun searchTerms(subjectId: Long, query: String): Flow<List<Term>> = 
        catalogueDao.searchTermsInSubject(subjectId, query)

    fun searchAllTerms(query: String): Flow<List<Term>> = 
        catalogueDao.searchAllTerms(query)

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

    fun getTopicAnalytics(topicId: Long) = catalogueDao.getTopicQuizAnalytics(topicId)

    fun getStudiedTopics() = catalogueDao.getStudiedTopics()

    fun getTotalQuizAttemptsForTopicsToday(topicIds: List<Long>, startOfDay: Long) = catalogueDao.getTotalQuizAttemptsForTopicsToday(topicIds, startOfDay)
    
    fun getTotalCorrectQuizAttemptsForTopicsToday(topicIds: List<Long>, startOfDay: Long) = catalogueDao.getTotalCorrectQuizAttemptsForTopicsToday(topicIds, startOfDay)

    fun getGlobalQuizAnalytics() = catalogueDao.getGlobalQuizAnalytics()

    fun getGlobalFlashcardCount() = catalogueDao.getGlobalFlashcardCount()

    fun getUniqueStudyDaysCount() = catalogueDao.getUniqueStudyDaysCount()

    suspend fun getStudySessionsCountToday(startOfDay: Long) = catalogueDao.getStudySessionsCountToday(startOfDay)

    fun getGlobalHandoutProgress() = catalogueDao.getGlobalHandoutProgress()

    fun getSubjectMastery() = catalogueDao.getSubjectMastery()

    fun getAllChaptersPerformance() = catalogueDao.getAllChaptersPerformance()

    fun getAllTopicsPerformance() = catalogueDao.getAllTopicsPerformance()

    fun getTimePerSubject() = catalogueDao.getTimePerSubject()

    fun getDailyStudyTime(since: Long) = catalogueDao.getDailyStudyTime(since)

    fun getPinnedItems(): Flow<List<RecentlyOpenedDisplayItem>> = catalogueDao.getPinnedItems().transform { pins ->
        val resolved = pins.mapNotNull { pin ->
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
                else -> null
            }
        }
        emit(resolved)
    }

    suspend fun togglePin(targetId: Long, type: MaterialType) {
        val isPinned = catalogueDao.isItemPinned(targetId, type).first()
        if (isPinned) {
            catalogueDao.removePinnedItem(targetId, type)
        } else {
            catalogueDao.insertPinnedItem(PinnedItem(targetId = targetId, type = type))
        }
    }

    fun isItemPinned(targetId: Long, type: MaterialType) = catalogueDao.isItemPinned(targetId, type)

    // Schedules
    fun getAllSchedules() = catalogueDao.getAllSchedules()
    suspend fun addSchedule(schedule: ClassSchedule) = catalogueDao.insertSchedule(schedule)
    suspend fun removeSchedule(schedule: ClassSchedule) = catalogueDao.deleteSchedule(schedule)
}
