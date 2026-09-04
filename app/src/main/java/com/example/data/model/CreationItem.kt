package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType {
    VIDEO,
    IMAGE
}

@Entity(tableName = "creations")
data class CreationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val negativePrompt: String = "",
    val mediaType: MediaType,
    val modelName: String,
    val style: String,
    val aspectRatio: String,
    val duration: String = "",
    val resolution: String = "1080p",
    val mediaUrl: String,
    val thumbnailUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
