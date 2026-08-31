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
