package com.bignerdranch.android.moodi.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.bignerdranch.android.moodi.BuildConfig

class OpenAIAssistant {
    companion object {
        private val API_KEY = BuildConfig.OPENAI_API_KEY
        private const val API_URL = "https://api.openai.com/v1/chat/completions"

        suspend fun generateMoodInsight(mood: String, note: String? = null): String = withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            
            val prompt = buildPrompt(mood, note)
            
            val requestBody = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("max_tokens", 100)
                put("temperature", 0.7)
            }

            val request = Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer $API_KEY")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.get("application/json"), requestBody.toString()))
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body()
                
                if (response.isSuccessful && responseBody != null) {
                    parseResponse(responseBody.string())
                } else {
                    "Unable to generate insight at this time."
                }
            } catch (e: IOException) {
                "Error connecting to AI service: ${e.message}"
            }
        }

        private fun buildPrompt(mood: String, note: String?): String {
            return """
                Provide a supportive and insightful 2-3 sentence analysis of someone feeling $mood.
                ${note?.let { "Additional context: $it" } ?: ""}
                Focus on emotional understanding and gentle encouragement.
            """.trimIndent()
        }

        private fun parseResponse(responseBody: String): String {
            return try {
                val jsonResponse = JSONObject(responseBody)
                jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
            } catch (e: Exception) {
                "Unable to process AI response."
            }
        }
    }
}
