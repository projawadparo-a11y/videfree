package com.example.data.repository

import com.example.data.db.CreationDao
import com.example.data.model.CreationItem
import com.example.data.model.MediaType
import kotlinx.coroutines.flow.Flow

class CreationRepository(private val creationDao: CreationDao) {

    val allCreations: Flow<List<CreationItem>> = creationDao.getAllCreations()

    fun getCreationsByType(type: MediaType): Flow<List<CreationItem>> {
        return creationDao.getCreationsByType(type)
    }

    suspend fun insertCreation(creation: CreationItem): Long {
        return creationDao.insertCreation(creation)
    }

    suspend fun deleteCreation(id: Long) {
        creationDao.deleteCreationById(id)
    }

    suspend fun prepopulateIfEmpty() {
        val count = creationDao.getCreationCount()
        if (count == 0) {
            val initialList = listOf(
                CreationItem(
                    title = "Cyberpunk Neo-Tokyo Rain",
                    prompt = "Cinematic wide shot of a futuristic cyberpunk metropolis with purple and cyan neon light reflections in rainy streets, flying vehicle motion blur, photorealistic movie still",
                    negativePrompt = "blurry, low quality, cartoon, watermark",
                    mediaType = MediaType.VIDEO,
                    modelName = "Google Veo 3",
                    style = "Cyberpunk",
                    aspectRatio = "16:9",
                    duration = "10s",
                    resolution = "4K",
                    mediaUrl = "android.resource://com.aistudio.videfree.stdapp/drawable/demo_cyberpunk_neon",
                    thumbnailUrl = "android.resource://com.aistudio.videfree.stdapp/drawable/demo_cyberpunk_neon",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 35
                ),
                CreationItem(
                    title = "Bioluminescent Valley",
                    prompt = "Cinematic drone shot of an enchanted bioluminescent glowing river and crystalline waterfalls surrounded by ethereal glowing flora in an alien twilight forest, 8k movie still",
                    negativePrompt = "noisy, pixelated, washed out",
                    mediaType = MediaType.VIDEO,
                    modelName = "OpenAI Sora",
                    style = "Fantasy",
                    aspectRatio = "16:9",
                    duration = "5s",
                    resolution = "1080p",
                    mediaUrl = "android.resource://com.aistudio.videfree.stdapp/drawable/demo_fantasy_forest",
                    thumbnailUrl = "android.resource://com.aistudio.videfree.stdapp/drawable/demo_fantasy_forest",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120
                ),
                CreationItem(
                    title = "Golden Hour Studio Portrait",
                    prompt = "Hyper-detailed studio portrait of a woman with delicate golden lighting, hyper-realistic skin texture, photorealistic 85mm lens photograph, cinematic quality",
                    negativePrompt = "deformed, extra fingers, cartoonish",
                    mediaType = MediaType.IMAGE,
                    modelName = "Nano Banana (Gemini 2.5 Flash)",
                    style = "Photorealistic",
                    aspectRatio = "1:1",
                    duration = "",
                    resolution = "4K",
                    mediaUrl = "android.resource://com.aistudio.videfree.stdapp/drawable/demo_banana_portrait",
                    thumbnailUrl = "android.resource://com.aistudio.videfree.stdapp/drawable/demo_banana_portrait",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 240
                )
            )
            creationDao.insertAll(initialList)
        }
    }
}
