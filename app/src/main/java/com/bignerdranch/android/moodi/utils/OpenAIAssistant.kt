package com.bignerdranch.android.moodi.utils

import android.util.Log
import com.bignerdranch.android.moodi.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import org.json.JSONArray
import java.io.IOException


class OpenAIAssistant {
    companion object {
        private val API_KEY = BuildConfig.OPENAI_API_KEY
        private const val API_URL = "https://api.openai.com/v1/chat/completions"

        suspend fun generateMoodInsight(mood: String, note: String? = null): String = withContext(Dispatchers.IO) {
            val client = OkHttpClient()

            try {
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
                    .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string() ?: ""

                    Log.d("OpenAI", "Response code: ${response.code}")
                    Log.d("OpenAI", "Response body: $responseBody")

                    if (response.isSuccessful) {
                        return@withContext parseResponse(responseBody)
                    } else {
                        Log.e("OpenAI", "Error response: $responseBody")
                        return@withContext "Error ${response.code}: ${response.message}"
                    }
                } catch (e: IOException) {
                    Log.e("OpenAI", "Network error: ${e.message}", e)
                    return@withContext "Network error: ${e.message}"
                }
            } catch (e: Exception) {
                Log.e("OpenAI", "General error: ${e.message}", e)
                return@withContext "Error: ${e.message}"
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
