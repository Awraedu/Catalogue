package com.awra.catalogue.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_attempts",
    foreignKeys = [
        ForeignKey(
            entity = QuizQuestion::class,
            parentColumns = ["id"],
            childColumns = ["quizQuestionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quizQuestionId")]
)
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizQuestionId: Long,
    val isCorrect: Boolean,
    val timeTakenMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "study_sessions",
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
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val materialType: MaterialType,
    val timeSpentMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)
