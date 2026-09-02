package com.awra.catalogue.data.repository

import android.content.Context
import com.awra.catalogue.data.database.DatabaseSeeder
import com.awra.catalogue.data.dao.CatalogueDao
import com.awra.catalogue.data.model.*
import com.awra.catalogue.data.seed.CatalogueMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CatalogueRepository(private val catalogueDao: CatalogueDao) {

    fun getSubjects(stream: Stream): Flow<List<Subject>> = catalogueDao.getSubjectsByStream(stream)

    suspend fun getSubjectById(subjectId: Long): Subject? = catalogueDao.getSubjectsById(subjectId)
    
    fun getChaptersFromMetadata(subjectName: String): List<Pair<String, List<String>>> = 
        CatalogueMetadata.getUnitsForSubject(subjectName)

    suspend fun getSubjectCount(): Int = catalogueDao.getSubjectCount()

    suspend fun seedDatabase(context: Context) = DatabaseSeeder.seed(context, catalogueDao)

    suspend fun seedSubjects(subjects: List<Subject>) = catalogueDao.insertSubjects(subjects)

    fun getChapters(subjectId: Long): Flow<List<ChapterWithTopics>> = catalogueDao.getChaptersWithHandoutsBySubject(subjectId)

    fun getChaptersWithQuizzes(subjectId: Long): Flow<List<ChapterWithQuizTopics>> = catalogueDao.getChaptersWithQuizzesBySubject(subjectId)

    fun getTopics(chapterId: Long): Flow<List<Topic>> = catalogueDao.getTopicsByChapter(chapterId)

    fun getHandouts(topicId: Long): Flow<List<Handout>> = catalogueDao.getHandoutsByTopic(topicId)

    fun getFlashcards(topicId: Long): Flow<List<Flashcard>> = catalogueDao.getFlashcardsByTopic(topicId)

    fun getCustomFlashcards(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<Flashcard>> =
        catalogueDao.getCustomFlashcards(chapterIds, difficulty, limit)

    fun getQuizQuestions(topicId: Long): Flow<List<QuizQuestion>> = catalogueDao.getQuizQuestionsByTopic(topicId)

    fun getCustomQuizQuestions(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<QuizQuestion>> =
        catalogueDao.getCustomQuizQuestions(chapterIds, difficulty, limit)

    fun getTerms(topicId: Long): Flow<List<Term>> = catalogueDao.getTermsByTopic(topicId)

    fun getUtilities(subjectId: Long): Flow<List<Utility>> = catalogueDao.getUtilitiesBySubject(subjectId)

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

    fun getGlobalQuizAnalytics() = catalogueDao.getGlobalQuizAnalytics()

    fun getSubjectMastery() = catalogueDao.getSubjectMastery()

    fun getAllChaptersPerformance() = catalogueDao.getAllChaptersPerformance()

    fun getAllTopicsPerformance() = catalogueDao.getAllTopicsPerformance()

    fun getTimePerSubject() = catalogueDao.getTimePerSubject()

    fun getDailyStudyTime(since: Long) = catalogueDao.getDailyStudyTime(since)
}
