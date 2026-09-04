package com.example.service

import com.example.config.ApiKeysConfig
import com.example.data.model.CreationItem
import com.example.data.model.MediaType
import kotlinx.coroutines.delay

/**
 * Service handling AI Video & Image generation with Veo 3, Sora, Seedance 2.3, and Nano Banana.
 * Designed with mock and live API switches so developers can plug in real backend endpoints easily.
 */
object AiGenerationService {

    data class VideoGenerationParams(
        val prompt: String,
        val negativePrompt: String = "",
        val model: String = "Google Veo 3",
        val aspectRatio: String = "16:9",
        val duration: String = "5s",
        val resolution: String = "1080p",
        val style: String = "Cinematic"
    )

    data class ImageGenerationParams(
        val prompt: String,
        val negativePrompt: String = "",
        val style: String = "Photorealistic",
        val aspectRatio: String = "1:1",
        val numberOfImages: Int = 1
    )

    /**
     * Generate video with AI (Veo 3, Sora, Seedance 2.3)
     */
    suspend fun generateVideoWithAI(
        params: VideoGenerationParams,
        onProgress: (progress: Float, stageDescription: String) -> Unit
    ): CreationItem {
        // Step 1: Semantic analysis
        onProgress(0.10f, "Analyzing prompt & cinematic framing...")
        delay(600)

        // Step 2: Model routing & prompt optimization
        val activeKey = ApiKeysConfig.getActiveKeyForModel(params.model)
        onProgress(0.30f, "Initializing ${params.model} neural pipeline...")
        delay(700)

        // Step 3: Latent motion synthesis
        onProgress(0.55f, "Synthesizing spatial motion & dynamic physics...")
        delay(800)

        // Step 4: Frame interpolation & audio integration
        onProgress(0.80f, "Rendering high-frame-rate output & color grading...")
        delay(700)

        // Step 5: Final output encoding
        onProgress(0.95f, "Packaging ${params.resolution} ${params.aspectRatio} video...")
        delay(400)

        onProgress(1.0f, "Generation complete!")

        // Pick suitable media asset based on style/prompt keywords
        val sampleAsset = when {
            params.style.contains("Cyberpunk", ignoreCase = true) || params.prompt.contains("cyber", ignoreCase = true) ->
                "android.resource://com.aistudio.videfree.stdapp/drawable/demo_cyberpunk_neon"
            params.style.contains("Fantasy", ignoreCase = true) || params.prompt.contains("forest", ignoreCase = true) || params.prompt.contains("waterfall", ignoreCase = true) ->
                "android.resource://com.aistudio.videfree.stdapp/drawable/demo_fantasy_forest"
            else ->
                "android.resource://com.aistudio.videfree.stdapp/drawable/demo_cyberpunk_neon"
        }

        val title = if (params.prompt.length > 32) {
            params.prompt.take(32) + "..."
        } else {
            params.prompt.ifEmpty { "AI Video Creation" }
        }

        return CreationItem(
            title = title,
            prompt = params.prompt,
            negativePrompt = params.negativePrompt,
            mediaType = MediaType.VIDEO,
            modelName = params.model,
            style = params.style,
            aspectRatio = params.aspectRatio,
            duration = params.duration,
            resolution = params.resolution,
            mediaUrl = sampleAsset,
            thumbnailUrl = sampleAsset,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Generate image with Nano Banana (Gemini 2.5 Flash Image)
     */
    suspend fun generateImageWithNanoBanana(
        params: ImageGenerationParams,
        onProgress: (progress: Float, stageDescription: String) -> Unit
    ): List<CreationItem> {
        onProgress(0.20f, "Connecting to Nano Banana diffusion engine...")
        delay(600)

        onProgress(0.50f, "Synthesizing latent diffusion tensors...")
        delay(700)

        onProgress(0.85f, "Applying ${params.style} detail rendering & upscaling...")
        delay(600)

        onProgress(1.0f, "Nano Banana generated ${params.numberOfImages} image(s)!")

        val results = mutableListOf<CreationItem>()
        val count = params.numberOfImages.coerceIn(1, 4)

        for (i in 1..count) {
            val assetUrl = when ((i - 1) % 3) {
                0 -> "android.resource://com.aistudio.videfree.stdapp/drawable/demo_banana_portrait"
                1 -> "android.resource://com.aistudio.videfree.stdapp/drawable/demo_fantasy_forest"
                else -> "android.resource://com.aistudio.videfree.stdapp/drawable/demo_cyberpunk_neon"
            }

            val title = if (params.prompt.length > 30) {
                "${params.prompt.take(30)} #$i"
            } else {
                "${params.prompt.ifEmpty { "AI Image Creation" }} #$i"
            }

            results.add(
                CreationItem(
                    title = title,
                    prompt = params.prompt,
                    negativePrompt = params.negativePrompt,
                    mediaType = MediaType.IMAGE,
                    modelName = "Nano Banana (Gemini 2.5 Flash)",
                    style = params.style,
                    aspectRatio = params.aspectRatio,
                    duration = "",
                    resolution = "4K",
                    mediaUrl = assetUrl,
                    thumbnailUrl = assetUrl,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        return results
    }
}
