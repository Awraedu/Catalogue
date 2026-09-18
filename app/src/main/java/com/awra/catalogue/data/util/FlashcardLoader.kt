package com.awra.catalogue.data.util

import android.content.Context
import com.awra.catalogue.data.model.Difficulty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
data class JsonFlashcard(
    val front: String,
    val back: String,
    val chapter: String,
    val topic: String
)

object FlashcardLoader {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadFlashcards(context: Context, subjectFolderName: String, difficulty: Difficulty): List<JsonFlashcard> {
        val fileName = when (difficulty) {
            Difficulty.EASY -> "easy.json"
            Difficulty.HARD -> "hard.json"
            else -> "easy.json" // Default for MIXED
        }
        
        val path = "flashcards/$subjectFolderName/$fileName"
        
        return try {
            val jsonString = context.assets.open(path).bufferedReader().use { it.readText() }
            json.decodeFromString<List<JsonFlashcard>>(jsonString)
        } catch (e: IOException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
