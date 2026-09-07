package com.awra.catalogue.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

enum class Stream {
    NATURAL, SOCIAL, COMMON
}

enum class MaterialType {
    HANDOUT, QUIZ, FLASHCARD, INFOGRAPHIC, UTILITY, TERM, ANALYTICS, SETTINGS, SCHEDULE, PAYWALL
}

enum class Difficulty {
    EASY, MEDIUM, HARD
}

enum class ExpressiveShape {
    CIRCLE, OVAL, PILL, SEMICIRCLE, ARCH, BUN, FAN, HEART,
    COOKIE_4, COOKIE_6, COOKIE_7, COOKIE_9, COOKIE_12,
    CLOVER_4, CLOVER_8, PUFFY, SOFT_BOOM, SOFT_BURST, GHOST
}

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val stream: Stream,
    val shape: ExpressiveShape
)

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val number: Int,
    val title: String
)

@Entity(
    tableName = "topics",
    foreignKeys = [
        ForeignKey(
            entity = Chapter::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("chapterId")]
)
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapterId: Long,
    val title: String
)

@Entity(
    tableName = "handouts",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class Handout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val content: String // Could be text or URI to a file
)

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val front: String,
    val back: String,
    val difficulty: Difficulty = Difficulty.MEDIUM
)

@Entity(
    tableName = "infographics",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class Infographic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val imageUrl: String
)

@Entity(
    tableName = "quiz_questions",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val hint: String = "",
    val explanation: String = ""
)

@Entity(
    tableName = "terms",
    foreignKeys = [
        ForeignKey(
            entity = Topic::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
data class Term(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val word: String,
    val definition: String,
    val isBookmarked: Boolean = false
)

@Entity(
    tableName = "utilities",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class Utility(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val type: String, // e.g., "CALCULATOR", "PERIODIC_TABLE"
    val icon: String // Icon identifier
)

@Entity(tableName = "recent_items")
data class RecentItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetId: Long,
    val type: MaterialType,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "class_schedules")
data class ClassSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long? = null,
    val subjectName: String,
    val dayOfWeek: Int, // 1=Mon, ..., 7=Sun
    val startTime: String, // HH:mm
    val endTime: String? = null, // HH:mm
    val room: String = "",
    val color: Int = 0xFF2196F3.toInt(),
    val isTest: Boolean = false,
    val testType: String? = null, // e.g., "Midterm", "Final"
    val date: Long? = null // For tests
)

