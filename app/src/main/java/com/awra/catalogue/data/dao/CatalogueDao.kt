package com.awra.catalogue.data.dao

import androidx.room.*
import com.awra.catalogue.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogueDao {
    // Subjects
    @Query("SELECT * FROM subjects WHERE stream = :stream OR stream = 'COMMON'")
    fun getSubjectsByStream(stream: Stream): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>): List<Long>

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun getSubjectCount(): Int

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()

    // Chapters
    @Transaction
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY number ASC")
    fun getChaptersWithHandoutsBySubject(subjectId: Long): Flow<List<ChapterWithTopics>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    // Topics
    @Query("SELECT * FROM topics WHERE chapterId = :chapterId")
    fun getTopicsByChapter(chapterId: Long): Flow<List<Topic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: Topic): Long

    // Handouts
    @Query("SELECT * FROM handouts WHERE topicId = :topicId")
    fun getHandoutsByTopic(topicId: Long): Flow<List<Handout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHandout(handout: Handout): Long

    // Flashcards
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId")
    fun getFlashcardsByTopic(topicId: Long): Flow<List<Flashcard>>

    // Quiz Questions
    @Query("SELECT * FROM quiz_questions WHERE topicId = :topicId")
    fun getQuizQuestionsByTopic(topicId: Long): Flow<List<QuizQuestion>>

    // Terms (Dictionary)
    @Query("SELECT * FROM terms WHERE topicId = :topicId")
    fun getTermsByTopic(topicId: Long): Flow<List<Term>>

    @Query("SELECT * FROM terms WHERE isBookmarked = 1")
    fun getBookmarkedTerms(): Flow<List<Term>>

    @Query("SELECT * FROM utilities WHERE subjectId = :subjectId")
    fun getUtilitiesBySubject(subjectId: Long): Flow<List<Utility>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUtility(utility: Utility): Long

    @Update
    suspend fun updateTerm(term: Term)

    // Search Terms across subject
    @Query("""
        SELECT * FROM terms 
        INNER JOIN topics ON terms.topicId = topics.id 
        INNER JOIN chapters ON topics.chapterId = chapters.id 
        WHERE chapters.subjectId = :subjectId AND (word LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%')
    """)
    fun searchTermsInSubject(subjectId: Long, query: String): Flow<List<Term>>

    // Analytics
    @Insert
    suspend fun insertQuizAttempt(attempt: QuizAttempt)

    @Insert
    suspend fun insertStudySession(session: StudySession)

    @Query("""
        SELECT 
            COUNT(*) as totalAttempts,
            SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) as correctAttempts,
            AVG(timeTakenMs) as avgTimeTaken
        FROM quiz_attempts
        INNER JOIN quiz_questions ON quiz_attempts.quizQuestionId = quiz_questions.id
        WHERE quiz_questions.topicId = :topicId
    """)
    fun getTopicQuizAnalytics(topicId: Long): Flow<TopicAnalytics>
}

data class TopicAnalytics(
    val totalAttempts: Int,
    val correctAttempts: Int,
    val avgTimeTaken: Double
)
