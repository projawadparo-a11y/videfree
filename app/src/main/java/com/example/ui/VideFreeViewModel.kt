package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CreationItem
import com.example.data.model.MediaType
import com.example.data.repository.CreationRepository
import com.example.service.AiGenerationService
import com.example.ui.components.NavTab
import com.example.ui.screens.PricingPlan
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideFreeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CreationRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CreationRepository(database.creationDao())
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // All Creations from Room Database
    val allCreations: StateFlow<List<CreationItem>> = repository.allCreations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(NavTab.VIDEO)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
    }

    // Credits System
    private val _creditsRemaining = MutableStateFlow(150)
    val creditsRemaining: StateFlow<Int> = _creditsRemaining.asStateFlow()

    // Shared feedback message event
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Video Generator State
    private val _isGeneratingVideo = MutableStateFlow(false)
    val isGeneratingVideo: StateFlow<Boolean> = _isGeneratingVideo.asStateFlow()

    private val _videoProgress = MutableStateFlow(0f)
    val videoProgress: StateFlow<Float> = _videoProgress.asStateFlow()

    private val _videoStatusText = MutableStateFlow("")
    val videoStatusText: StateFlow<String> = _videoStatusText.asStateFlow()

    private val _activeVideoCreation = MutableStateFlow<CreationItem?>(null)
    val activeVideoCreation: StateFlow<CreationItem?> = _activeVideoCreation.asStateFlow()

    // Image Generator State
    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _imageProgress = MutableStateFlow(0f)
    val imageProgress: StateFlow<Float> = _imageProgress.asStateFlow()

    private val _imageStatusText = MutableStateFlow("")
    val imageStatusText: StateFlow<String> = _imageStatusText.asStateFlow()

    private val _generatedImages = MutableStateFlow<List<CreationItem>>(emptyList())
    val generatedImages: StateFlow<List<CreationItem>> = _generatedImages.asStateFlow()

    private val _crossTabPrompt = MutableStateFlow("")
    val crossTabPrompt: StateFlow<String> = _crossTabPrompt.asStateFlow()

    fun generateVideo(params: AiGenerationService.VideoGenerationParams) {
        if (_isGeneratingVideo.value) return

        if (_creditsRemaining.value < 10) {
            viewModelScope.launch {
                _toastMessage.emit("Insufficient credits. Please choose a creator tier.")
                _currentTab.value = NavTab.PRICING
            }
            return
        }

        viewModelScope.launch {
            _isGeneratingVideo.value = true
            _videoProgress.value = 0.05f
            _videoStatusText.value = "Submitting prompt to ${params.model}..."

            try {
                val result = AiGenerationService.generateVideoWithAI(params) { progress, stage ->
                    _videoProgress.value = progress
                    _videoStatusText.value = stage
                }

                // Deduct credits
                _creditsRemaining.value = (_creditsRemaining.value - 10).coerceAtLeast(0)

                // Save to Room Database
                val savedId = repository.insertCreation(result)
                val fullItem = result.copy(id = savedId)
                _activeVideoCreation.value = fullItem

                _toastMessage.emit("✨ Video generated successfully with ${params.model}!")
            } catch (e: Exception) {
                _toastMessage.emit("Generation failed: ${e.localizedMessage ?: "Unknown error"}")
            } finally {
                _isGeneratingVideo.value = false
            }
        }
    }

    fun generateImages(params: AiGenerationService.ImageGenerationParams) {
        if (_isGeneratingImage.value) return

        if (_creditsRemaining.value < 5) {
            viewModelScope.launch {
                _toastMessage.emit("Insufficient credits. Upgrade in Pricing tab.")
                _currentTab.value = NavTab.PRICING
            }
            return
        }

        viewModelScope.launch {
            _isGeneratingImage.value = true
            _imageProgress.value = 0.1f
            _imageStatusText.value = "Nano Banana diffusion initialized..."

            try {
                val results = AiGenerationService.generateImageWithNanoBanana(params) { progress, stage ->
                    _imageProgress.value = progress
                    _imageStatusText.value = stage
                }

                // Deduct credits (5 per generation)
                _creditsRemaining.value = (_creditsRemaining.value - 5).coerceAtLeast(0)

                // Save all to Room Database
                val savedList = mutableListOf<CreationItem>()
                results.forEach { item ->
                    val id = repository.insertCreation(item)
                    savedList.add(item.copy(id = id))
                }

                _generatedImages.value = savedList
                _toastMessage.emit("🍌 Nano Banana synthesized ${results.size} image(s)!")
            } catch (e: Exception) {
                _toastMessage.emit("Image generation failed: ${e.localizedMessage ?: "Error"}")
            } finally {
                _isGeneratingImage.value = false
            }
        }
    }

    fun deleteCreation(item: CreationItem) {
        viewModelScope.launch {
            repository.deleteCreation(item.id)
            if (_activeVideoCreation.value?.id == item.id) {
                _activeVideoCreation.value = null
            }
            _generatedImages.value = _generatedImages.value.filter { it.id != item.id }
            _toastMessage.emit("Asset removed from studio.")
        }
    }

    fun triggerDownload(item: CreationItem) {
        viewModelScope.launch {
            _toastMessage.emit("Downloading \"${item.title}\" (${item.resolution}) to device...")
        }
    }

    fun useVideoAsImageSource(item: CreationItem) {
        _crossTabPrompt.value = "Photorealistic high-detail cinematic still of: ${item.prompt}"
        _currentTab.value = NavTab.IMAGE
        viewModelScope.launch {
            _toastMessage.emit("Video prompt routed to Nano Banana Studio!")
        }
    }

    fun upgradePlan(plan: PricingPlan) {
        _creditsRemaining.value = plan.credits
        viewModelScope.launch {
            _toastMessage.emit("🎉 Upgraded to ${plan.name}! ${plan.credits} credits added.")
            _currentTab.value = NavTab.VIDEO
        }
    }
}
