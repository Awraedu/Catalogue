package com.awra.catalogue.data.model

import androidx.compose.ui.graphics.Color

data class RecentlyOpenedDisplayItem(
    val id: Long, // RecentItem ID
    val targetId: Long,
    val subjectId: Long,
    val name: String,
    val type: MaterialType,
    val icon: String,
    val color: Color,
    val timestamp: Long
)

sealed class FeaturedItem {
    data class DailyQuiz(
        val topicId: Long,
        val topicTitle: String,
        val subjectName: String,
        val questionCount: Int = 5,
        val isCompleted: Boolean = false,
        val lastScore: Int = 0
    ) : FeaturedItem()
    object EmptyDailyQuiz : FeaturedItem()

    data class DailyTerm(
        val termId: Long,
        val word: String,
        val definition: String,
        val subjectName: String,
        val isTried: Boolean = false
    ) : FeaturedItem()
    object EmptyDailyTerm : FeaturedItem()

    data class NextClass(
        val subjectName: String,
        val startTime: String,
        val room: String,
        val timeRemainingMins: Long
    ) : FeaturedItem()
    object EmptyNextClass : FeaturedItem()

    data class DailyInsight(
        val word: String,
        val definition: String,
        val subjectName: String
    ) : FeaturedItem()
}
