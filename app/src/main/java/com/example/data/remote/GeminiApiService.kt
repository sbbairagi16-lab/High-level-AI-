package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini API Request Data Classes ---
@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

// --- Gemini API Response Data Classes ---
@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }
}

data class AiAnalysisResult(
    val sentimentLabel: String,
    val triggers: String,
    val summaryTip: String,
    val recommendedTips: List<Pair<String, String>> // Title to Description
)

class AiEngineService {

    suspend fun analyzeJournalEntry(
        title: String,
        content: String,
        mood: String,
        moodScore: Int,
        tags: String
    ): AiAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are MindPulse AI, a supportive wellbeing and mood analysis assistant.
                    Analyze the following user's daily journal entry:
                    - Title: $title
                    - Entry Text: $content
                    - Self-Reported Mood: $mood (Score: $moodScore/10)
                    - Tags/Context: $tags

                    Provide your analysis strictly formatted with these 4 headings:
                    SENTIMENT: [One of: Positive, Balanced, Reflective, Challenging]
                    TRIGGERS: [Comma-separated main emotional drivers or key topics identified, e.g. Work Deadline, Sleep Quality]
                    SUMMARY_TIP: [1-2 sentences of actionable, compassionate wellness advice tailored to this moment]
                    WELLNESS_RECOMMENDATIONS:
                    1. [Tip Title] | [Short actionable tip description]
                    2. [Tip Title] | [Short actionable tip description]
                    3. [Tip Title] | [Short actionable tip description]
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    )
                )

                val response = RetrofitClient.api.generateContent(apiKey, request)
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!textResponse.isNullOrBlank()) {
                    return@withContext parseGeminiResponse(textResponse, mood, moodScore)
                }
            } catch (e: Exception) {
                // Fall back to local AI engine if network or API key fails
            }
        }

        // Local AI Engine Fallback (Offline Mode)
        return@withContext generateLocalAiAnalysis(title, content, mood, moodScore, tags)
    }

    private fun parseGeminiResponse(text: String, defaultMood: String, moodScore: Int): AiAnalysisResult {
        var sentiment = if (moodScore >= 7) "Positive" else if (moodScore >= 5) "Balanced" else "Challenging"
        var triggers = "Daily Reflection, Mindset"
        var summaryTip = "Take a moment to pause, breathe deeply, and honor your emotional journey today."
        val recommendations = mutableListOf<Pair<String, String>>()

        val lines = text.lines()
        var currentSection = ""

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("SENTIMENT:") -> {
                    sentiment = trimmed.substringAfter("SENTIMENT:").trim()
                }
                trimmed.startsWith("TRIGGERS:") -> {
                    triggers = trimmed.substringAfter("TRIGGERS:").trim()
                }
                trimmed.startsWith("SUMMARY_TIP:") -> {
                    summaryTip = trimmed.substringAfter("SUMMARY_TIP:").trim()
                }
                trimmed.startsWith("WELLNESS_RECOMMENDATIONS:") -> {
                    currentSection = "RECS"
                }
                currentSection == "RECS" && (trimmed.startsWith("1.") || trimmed.startsWith("2.") || trimmed.startsWith("3.") || trimmed.startsWith("-")) -> {
                    val contentPart = trimmed.substringAfter(" ").trim()
                    if (contentPart.contains("|")) {
                        val titlePart = contentPart.substringBefore("|").trim()
                        val descPart = contentPart.substringAfter("|").trim()
                        recommendations.add(Pair(titlePart, descPart))
                    } else if (contentPart.contains(":")) {
                        val titlePart = contentPart.substringBefore(":").trim()
                        val descPart = contentPart.substringAfter(":").trim()
                        recommendations.add(Pair(titlePart, descPart))
                    }
                }
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.addAll(getDefaultTipsForMood(defaultMood))
        }

        return AiAnalysisResult(
            sentimentLabel = sentiment,
            triggers = triggers,
            summaryTip = summaryTip,
            recommendedTips = recommendations
        )
    }

    private fun generateLocalAiAnalysis(
        title: String,
        content: String,
        mood: String,
        moodScore: Int,
        tags: String
    ): AiAnalysisResult {
        val lowerContent = "$title $content $tags".lowercase()

        val sentiment = when {
            moodScore >= 8 || lowerContent.contains("happy") || lowerContent.contains("great") || lowerContent.contains("awesome") -> "Positive"
            moodScore in 5..7 || lowerContent.contains("okay") || lowerContent.contains("calm") || lowerContent.contains("steady") -> "Balanced"
            lowerContent.contains("reflect") || lowerContent.contains("think") || lowerContent.contains("meditate") -> "Reflective"
            else -> "Challenging"
        }

        val detectedTriggers = mutableListOf<String>()
        if (lowerContent.contains("work") || lowerContent.contains("job") || lowerContent.contains("boss")) detectedTriggers.add("Work Environment")
        if (lowerContent.contains("sleep") || lowerContent.contains("tired") || lowerContent.contains("rest")) detectedTriggers.add("Rest & Recovery")
        if (lowerContent.contains("exercise") || lowerContent.contains("run") || lowerContent.contains("gym")) detectedTriggers.add("Physical Activity")
        if (lowerContent.contains("family") || lowerContent.contains("friend") || lowerContent.contains("social")) detectedTriggers.add("Social Connection")
        if (lowerContent.contains("stress") || lowerContent.contains("anxious") || lowerContent.contains("worry")) detectedTriggers.add("Stress Response")
        if (detectedTriggers.isEmpty()) detectedTriggers.add("Personal Reflection")

        val summaryTip = when (mood.uppercase()) {
            "ANXIOUS", "OVERWHELMED" -> "Try 4-7-8 box breathing for 3 minutes to ground your nervous system."
            "EXHAUSTED" -> "Prioritize an early screen-free evening and hydrate with warm herbal tea."
            "JOYFUL", "ENERGETIC" -> "Channel your radiant energy into a creative project or share gratitude with someone."
            "FOCUSED" -> "Maintain your momentum with short 25-minute Pomodoro focus sprints."
            else -> "Take 5 deep conscious breaths and set a gentle intention for the rest of your day."
        }

        val recommendedTips = getDefaultTipsForMood(mood)

        return AiAnalysisResult(
            sentimentLabel = sentiment,
            triggers = detectedTriggers.joinToString(", "),
            summaryTip = summaryTip,
            recommendedTips = recommendedTips
        )
    }

    private fun getDefaultTipsForMood(mood: String): List<Pair<String, String>> {
        return when (mood.uppercase()) {
            "ANXIOUS", "OVERWHELMED" -> listOf(
                Pair("4-7-8 Breathing", "Inhale 4s, hold 7s, exhale slowly 8s to calm cortisol level."),
                Pair("5-4-3-2-1 Sensory Reset", "Name 5 things you see, 4 you touch, 3 you hear, 2 smell, 1 taste."),
                Pair("Thought Unloading", "Write down all worries on paper, then physically categorize what is in your control.")
            )
            "EXHAUSTED" -> listOf(
                Pair("Digital Sunset", "Turn off blue-light screens 60 minutes before bedtime."),
                Pair("Hydration Boost", "Drink a large glass of water with lemon to restore electrolyte balance."),
                Pair("Gentle Stretching", "Perform 5 minutes of cat-cow and child's pose to release muscular tension.")
            )
            "JOYFUL", "ENERGETIC" -> listOf(
                Pair("Gratitude Journaling", "Write down 3 specific things that triggered your joy today."),
                Pair("Pass along Positivity", "Send a warm voice note or compliment to a friend or colleague."),
                Pair("Active Movement", "Go for a brisk 20-minute outdoor walk while soaking in natural daylight.")
            )
            else -> listOf(
                Pair("Mindful Body Scan", "Spend 3 minutes scanning your body from head to toe, releasing shoulder tension."),
                Pair("Hydration Check", "Aim for a glass of room-temperature water every two hours."),
                Pair("Evening Reflection", "Note one win and one lesson learned at the end of your day.")
            )
        }
    }
}
