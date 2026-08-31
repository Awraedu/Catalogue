package com.awra.catalogue.data.util

import android.content.Context
import com.awra.catalogue.data.model.Difficulty
import com.awra.catalogue.data.model.QuizQuestion
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
data class JsonQuizQuestion(
    val question: String,
    val choices: List<String>,
    val tip: String = "",
    val explanation: String = "",
    val chapter: String,
    val topic: String,
    val answerIndex: Int = 0
)

object QuizLoader {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadQuizzes(context: Context, subjectFolderName: String, difficulty: Difficulty): List<JsonQuizQuestion> {
        val fileName = when (difficulty) {
            Difficulty.EASY -> "easy.json"
            Difficulty.MEDIUM -> "medium.json"
            Difficulty.HARD -> "hard.json"
        }
        
        val path = "quizes/$subjectFolderName/$fileName"
        
        return try {
            val jsonString = context.assets.open(path).bufferedReader().use { it.readText() }
            json.decodeFromString<List<JsonQuizQuestion>>(jsonString)
        } catch (e: IOException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
