package com.awra.catalogue.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TopicWithHandout(
    @Embedded val topic: Topic,
    @Relation(
        parentColumn = "id",
        entityColumn = "topicId"
    )
    val handout: Handout?
)

data class ChapterWithTopics(
    @Embedded val chapter: Chapter,
    @Relation(
        entity = Topic::class,
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val topics: List<TopicWithHandout>
)

data class TopicWithQuiz(
    @Embedded val topic: Topic,
    @Relation(
        parentColumn = "id",
        entityColumn = "topicId"
    )
    val questions: List<QuizQuestion>
)

data class ChapterWithQuizTopics(
    @Embedded val chapter: Chapter,
    @Relation(
        entity = Topic::class,
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val topics: List<TopicWithQuiz>
)

data class TopicWithInfographic(
    @Embedded val topic: Topic,
    @Relation(
        parentColumn = "id",
        entityColumn = "topicId"
    )
    val infographic: Infographic?
)

data class ChapterWithInfographicTopics(
    @Embedded val chapter: Chapter,
    @Relation(
        entity = Topic::class,
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val topics: List<TopicWithInfographic>
)
