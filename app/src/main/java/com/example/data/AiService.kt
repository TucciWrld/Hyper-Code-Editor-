package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") key: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val geminiApi: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}

class AiService {

    private suspend fun callGemini(systemPrompt: String, userPrompt: String): String {
        return try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                return "AI Assistant API Key is missing. Please enter your GEMINI_API_KEY in the AI Studio Secets panel."
            }
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
            )
            val response = RetrofitClient.geminiApi.generateContent(apiKey, request)
            var text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (text != null) {
                // Strip markdown code block surrounds if the AI surrounds responses with it
                if (text.startsWith("```")) {
                    val lines = text.lines()
                    if (lines.size > 2) {
                        text = lines.subList(1, lines.size - 1).joinToString("\n")
                    }
                }
                text.trim()
            } else {
                "Unable to obtain response from Hyper AI."
            }
        } catch (e: Exception) {
            "Hyper AI Consultation failed: ${e.message ?: "Unknown Connection Error"}"
        }
    }

    suspend fun writeCode(instruction: String, currentCode: String, language: String): String {
        val systemPrompt = """You are Hyper AI, an expert code generator built into the Hyper Code IDE. 
Your output must contain ONLY the raw source code of the generated component, file, or HTML page.
DO NOT wrap the code in markdown ``` blocks. DO NOT provide explanations, guides, or warnings. Just output the clean, compilable, runnable code."""
        
        val userPrompt = "Action requested: $instruction\nLanguage context: $language\nExisting context snippet (can be empty):\n$currentCode"
        return callGemini(systemPrompt, userPrompt)
    }

    suspend fun checkErrorsAndFix(code: String, filePath: String, consoleErrors: String): String {
        val systemPrompt = """You are Hyper AI debugger expert. 
Your task is to analyze the file and any console output/runtime errors provided, and produce a FIXED, complete source file.
Output ONLY the clean, fixed code with NO surrounding warnings, explanation, or markdown code blocks."""
        
        val userPrompt = "File: $filePath\nCode:\n$code\n\nRuntime Console Output/Errors:\n$consoleErrors"
        return callGemini(systemPrompt, userPrompt)
    }

    suspend fun explainCode(code: String, filePath: String): String {
        val systemPrompt = "You are Hyper AI expert developer. Explain the following source code in clear, educational, bullet-point highlights. Suggest optimizations if applicable."
        val userPrompt = "File: $filePath\nCode:\n$code"
        return callGemini(systemPrompt, userPrompt)
    }

    suspend fun convertJsToTs(code: String): String {
        val systemPrompt = "You are code conversion specialist. Convert the following JavaScript code to TypeScript. Define clean interfaces and type configurations where applicable. Output ONLY the raw TypeScript code with no surrounds."
        return callGemini(systemPrompt, code)
    }

    suspend fun convertHtmlToReact(code: String): String {
        val systemPrompt = "You are a senior React developer. Convert the following static HTML document to a fully functional React component using hooks (useState/useEffect) and return standard JSX. Output ONLY the raw JSX code with no surrounds or explanations."
        return callGemini(systemPrompt, code)
    }
}
