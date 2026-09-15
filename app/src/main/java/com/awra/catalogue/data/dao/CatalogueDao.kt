package com.awra.catalogue.data.dao

import androidx.room.*
import com.awra.catalogue.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogueDao {
    // Subjects
    @Query("SELECT * FROM subjects WHERE stream = :stream OR stream = 'COMMON'")
    fun getSubjectsByStream(stream: Stream): Flow<List<Subject>>

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectsById(subjectId: Long): Subject?

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

    @Transaction
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY number ASC")
    fun getChaptersWithQuizzesBySubject(subjectId: Long): Flow<List<ChapterWithQuizTopics>>

    @Transaction
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY number ASC")
    fun getChaptersWithInfographicsBySubject(subjectId: Long): Flow<List<ChapterWithInfographicTopics>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: Long): Chapter?

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

    @Query("SELECT * FROM handouts WHERE id = :id")
    suspend fun getHandoutById(id: Long): Handout?

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: Long): Topic?

    @Query("SELECT * FROM infographics WHERE id = :id")
    suspend fun getInfographicById(id: Long): Infographic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfographic(infographic: Infographic): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfographics(infographics: List<Infographic>): List<Long>

    @Query("SELECT * FROM utilities WHERE id = :id")
    suspend fun getUtilityById(id: Long): Utility?

    // Flashcards
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId")
    fun getFlashcardsByTopic(topicId: Long): Flow<List<Flashcard>>

    @Query("""
        SELECT flashcards.* FROM flashcards 
        INNER JOIN topics ON flashcards.topicId = topics.id
        WHERE topics.chapterId IN (:chapterIds) AND difficulty = :difficulty
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    fun getCustomFlashcards(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<Flashcard>): List<Long>

    // Quiz Questions
    @Query("SELECT * FROM quiz_questions WHERE topicId = :topicId")
    fun getQuizQuestionsByTopic(topicId: Long): Flow<List<QuizQuestion>>

    @Query("""
        SELECT quiz_questions.* FROM quiz_questions 
        INNER JOIN topics ON quiz_questions.topicId = topics.id
        WHERE topics.chapterId IN (:chapterIds) AND difficulty = :difficulty
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    fun getCustomQuizQuestions(chapterIds: List<Long>, difficulty: Difficulty, limit: Int): Flow<List<QuizQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(quizQuestion: QuizQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestions(quizQuestions: List<QuizQuestion>): List<Long>

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerms(terms: List<Term>): List<Long>

    // Search Terms across subject
    @Query("""
        SELECT terms.* FROM terms
        INNER JOIN topics ON terms.topicId = topics.id 
        INNER JOIN chapters ON topics.chapterId = chapters.id 
        WHERE chapters.subjectId = :subjectId AND (word LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%')
    """)
    fun searchTermsInSubject(subjectId: Long, query: String): Flow<List<Term>>

    @Query("""
        SELECT * FROM terms 
        WHERE word LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%'
    """)
    fun searchAllTerms(query: String): Flow<List<Term>>

    // Analytics
    @Insert
    suspend fun insertQuizAttempt(attempt: QuizAttempt)

    @Insert
    suspend fun insertFlashcardAttempt(attempt: FlashcardAttempt)

    @Insert
    suspend fun insertStudySession(session: StudySession)

    @Query("""
        SELECT 
            subjects.id as subjectId,
            subjects.name as subjectName,
            COALESCE(COUNT(DISTINCT quiz_attempts.id), 0) + COALESCE(COUNT(DISTINCT flashcard_attempts.id), 0) as totalAttempts,
            COALESCE(SUM(CASE WHEN quiz_attempts.isCorrect = 1 THEN 1 ELSE 0 END), 0) + 
            COALESCE(SUM(CASE WHEN flashcard_attempts.isCorrect = 1 THEN 1 ELSE 0 END), 0) as correctAttempts,
            AVG(quiz_attempts.timeTakenMs) as avgQuizTimeMs
        FROM subjects
        LEFT JOIN chapters ON subjects.id = chapters.subjectId
        LEFT JOIN topics ON chapters.id = topics.chapterId
        LEFT JOIN quiz_questions ON topics.id = quiz_questions.topicId
        LEFT JOIN quiz_attempts ON quiz_questions.id = quiz_attempts.quizQuestionId
        LEFT JOIN flashcards ON topics.id = flashcards.topicId
        LEFT JOIN flashcard_attempts ON flashcards.id = flashcard_attempts.flashcardId
        GROUP BY subjects.id
    """)
    fun getSubjectMastery(): Flow<List<SubjectMastery>>

    @Query("""
        SELECT 
            chapters.id as chapterId,
            chapters.title as chapterTitle,
            chapters.number as chapterNumber,
            subjects.name as subjectName,
            COALESCE(COUNT(DISTINCT quiz_attempts.id), 0) + COALESCE(COUNT(DISTINCT flashcard_attempts.id), 0) as totalAttempts,
            COALESCE(SUM(CASE WHEN quiz_attempts.isCorrect = 1 THEN 1 ELSE 0 END), 0) + 
            COALESCE(SUM(CASE WHEN flashcard_attempts.isCorrect = 1 THEN 1 ELSE 0 END), 0) as correctAttempts
        FROM chapters
        INNER JOIN subjects ON chapters.subjectId = subjects.id
        LEFT JOIN topics ON chapters.id = topics.chapterId
        LEFT JOIN quiz_questions ON topics.id = quiz_questions.topicId
        LEFT JOIN quiz_attempts ON quiz_questions.id = quiz_attempts.quizQuestionId
        LEFT JOIN flashcards ON topics.id = flashcards.topicId
        LEFT JOIN flashcard_attempts ON flashcards.id = flashcard_attempts.flashcardId
        GROUP BY chapters.id
    """)
    fun getAllChaptersPerformance(): Flow<List<ChapterPerformance>>

    @Query("""
        SELECT 
            topics.id as topicId,
            topics.title as topicTitle,
            topics.chapterId as chapterId,
            subjects.name as subjectName,
            COALESCE(COUNT(DISTINCT quiz_attempts.id), 0) + COALESCE(COUNT(DISTINCT flashcard_attempts.id), 0) as totalAttempts,
            COALESCE(SUM(CASE WHEN quiz_attempts.isCorrect = 1 THEN 1 ELSE 0 END), 0) + 
            COALESCE(SUM(CASE WHEN flashcard_attempts.isCorrect = 1 THEN 1 ELSE 0 END), 0) as correctAttempts
        FROM topics
        INNER JOIN chapters ON topics.chapterId = chapters.id
        INNER JOIN subjects ON chapters.subjectId = subjects.id
        LEFT JOIN quiz_questions ON topics.id = quiz_questions.topicId
        LEFT JOIN quiz_attempts ON quiz_questions.id = quiz_attempts.quizQuestionId
        LEFT JOIN flashcards ON topics.id = flashcards.topicId
        LEFT JOIN flashcard_attempts ON flashcards.id = flashcard_attempts.flashcardId
        GROUP BY topics.id
    """)
    fun getAllTopicsPerformance(): Flow<List<TopicPerformance>>

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

    @Query("""
        SELECT 
            COUNT(*) as totalAttempts,
            SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) as correctAttempts,
            AVG(timeTakenMs) as avgTimeTaken
        FROM quiz_attempts
    """)
    fun getGlobalQuizAnalytics(): Flow<OverallAnalytics>

    @Query("""
        SELECT 
            subjects.name as subjectName,
            SUM(study_sessions.timeSpentMs) as totalTimeMs
        FROM study_sessions
        INNER JOIN topics ON study_sessions.topicId = topics.id
        INNER JOIN chapters ON topics.chapterId = chapters.id
        INNER JOIN subjects ON chapters.subjectId = subjects.id
        GROUP BY subjects.id
    """)
    fun getTimePerSubject(): Flow<List<SubjectTime>>

    @Query("""
        SELECT 
            (timestamp / (24 * 60 * 60 * 1000)) * (24 * 60 * 60 * 1000) as date,
            SUM(timeSpentMs) as totalTimeMs
        FROM study_sessions
        WHERE timestamp > :since
        GROUP BY date
        ORDER BY date ASC
    """)
    fun getDailyStudyTime(since: Long): Flow<List<DailyTime>>

    @Query("SELECT COUNT(*) FROM flashcard_attempts")
    fun getGlobalFlashcardCount(): Flow<Int>

    @Query("""
        SELECT 
            CAST(COUNT(DISTINCT study_sessions.topicId) AS FLOAT) / 
            NULLIF((SELECT COUNT(*) FROM topics), 0)
        FROM study_sessions 
        WHERE materialType = 'HANDOUT'
    """)
    fun getGlobalHandoutProgress(): Flow<Float?>

    // Pinned Items
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPinnedItem(item: PinnedItem)

    @Delete
    suspend fun deletePinnedItem(item: PinnedItem)

    @Query("SELECT * FROM pinned_items ORDER BY timestamp DESC")
    fun getPinnedItems(): Flow<List<PinnedItem>>

    @Query("DELETE FROM pinned_items WHERE targetId = :targetId AND type = :type")
    suspend fun removePinnedItem(targetId: Long, type: MaterialType)

    @Query("SELECT EXISTS(SELECT 1 FROM pinned_items WHERE targetId = :targetId AND type = :type)")
    fun isItemPinned(targetId: Long, type: MaterialType): Flow<Boolean>

    // Schedules
    @Query("SELECT * FROM class_schedules ORDER BY dayOfWeek, startTime")
    fun getAllSchedules(): Flow<List<ClassSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ClassSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: ClassSchedule)
}



data class OverallAnalytics(
    val totalAttempts: Int,
    val correctAttempts: Int,
    val avgTimeTaken: Double
)

data class SubjectTime(
    val subjectName: String,
    val totalTimeMs: Long
)

data class DailyTime(
    val date: Long,
    val totalTimeMs: Long
)

data class TopicAnalytics(
    val totalAttempts: Int,
    val correctAttempts: Int,
    val avgTimeTaken: Double
)

data class SubjectMastery(
    val subjectId: Long,
    val subjectName: String,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val avgQuizTimeMs: Double?
)

data class ChapterPerformance(
    val chapterId: Long,
    val chapterTitle: String,
    val chapterNumber: Int,
    val subjectName: String,
    val totalAttempts: Int,
    val correctAttempts: Int
)

data class TopicPerformance(
    val topicId: Long,
    val topicTitle: String,
    val chapterId: Long,
    val subjectName: String,
    val totalAttempts: Int,
    val correctAttempts: Int
)
