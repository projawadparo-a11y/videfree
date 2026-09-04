package com.example.config

import com.example.BuildConfig

/**
 * API Key configuration for VideFree AI models.
 * Users can easily plug real API keys via AI Studio Secrets (.env file)
 * or provide custom runtime keys.
 */
object ApiKeysConfig {

    // Google Veo 3 API Key
    val googleVeoApiKey: String
        get() = try {
            BuildConfig.GOOGLE_VEO_API_KEY.ifEmpty { "demo_veo_live_key" }
        } catch (e: Throwable) {
            "demo_veo_live_key"
        }

    // OpenAI Sora API Key
    val openAiSoraApiKey: String
        get() = try {
            BuildConfig.OPENAI_SORA_API_KEY.ifEmpty { "demo_sora_live_key" }
        } catch (e: Throwable) {
            "demo_sora_live_key"
        }

    // ByteDance Seedance 2.3 API Key
    val byteDanceSeedanceApiKey: String
        get() = try {
            BuildConfig.BYTEDANCE_SEEDANCE_API_KEY.ifEmpty { "demo_seedance_live_key" }
        } catch (e: Throwable) {
            "demo_seedance_live_key"
        }

    // Nano Banana (Google Gemini 2.5 Flash Image) API Key
    val nanoBananaApiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY.ifEmpty { "demo_nano_banana_key" }
        } catch (e: Throwable) {
            "demo_nano_banana_key"
        }

    // Whether to use simulated mock mode or live endpoints
    var isLiveApiEnabled: Boolean = false

    fun getActiveKeyForModel(modelName: String): String {
        return when {
            modelName.contains("Veo", ignoreCase = true) -> googleVeoApiKey
            modelName.contains("Sora", ignoreCase = true) -> openAiSoraApiKey
            modelName.contains("Seed", ignoreCase = true) -> byteDanceSeedanceApiKey
            else -> nanoBananaApiKey
        }
    }
}
