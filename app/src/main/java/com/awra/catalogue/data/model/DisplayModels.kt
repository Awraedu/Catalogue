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
