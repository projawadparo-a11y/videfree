package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Crop169
import androidx.compose.material.icons.filled.Crop32
import androidx.compose.material.icons.filled.Crop54
import androidx.compose.material.icons.filled.Crop75
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CreationItem
import com.example.service.AiGenerationService
import com.example.ui.components.HeroSection
import com.example.ui.components.SparklePlayIcon
import com.example.ui.components.VideoPlayerPreview
import com.example.ui.theme.CardGlassBackground
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleBlueGradient
import com.example.ui.theme.SeedanceColor
import com.example.ui.theme.SoraColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Veo3Color

data class AiModelInfo(
    val id: String,
    val name: String,
    val tag: String,
    val description: String,
    val brandColor: Color,
    val badge: String? = null
)

data class AspectRatioOption(
    val id: String,
    val label: String,
    val subtitle: String,
    val icon: ImageVector
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoGeneratorScreen(
    isGenerating: Boolean,
    progress: Float,
    statusText: String,
    activeCreation: CreationItem?,
    onGenerate: (AiGenerationService.VideoGenerationParams) -> Unit,
    onDownload: (CreationItem) -> Unit,
    onDelete: (CreationItem) -> Unit,
    onUseAsImageSource: (CreationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var prompt by remember {
        mutableStateOf("Cinematic aerial shot of futuristic Tokyo floating islands with violet holographic auroras in rain, 4K")
    }
    var negativePrompt by remember { mutableStateOf("") }
    var showNegativePrompt by remember { mutableStateOf(false) }

    val models = listOf(
        AiModelInfo(
            id = "veo3",
            name = "Google Veo 3",
            tag = "Best quality, cinematic, audio included",
            description = "Cinematic 4K fidelity with synchronized spatial sound design",
            brandColor = Veo3Color,
            badge = "RECOMMENDED"
        ),
        AiModelInfo(
            id = "sora",
            name = "OpenAI Sora",
            tag = "Photorealistic, complex motion",
            description = "Industry-standard physical simulation & intricate character choreography",
            brandColor = SoraColor,
            badge = "PRO"
        ),
        AiModelInfo(
            id = "seedance",
            name = "ByteDance Seedance 2.3",
            tag = "Fast, viral style",
            description = "Ultra-fast generation optimized for dynamic TikTok & vertical trends",
            brandColor = SeedanceColor,
            badge = "FAST"
        )
    )
    var selectedModel by remember { mutableStateOf(models[0]) }

    val sceneStyles = listOf(
        "Cinematic", "Realistic", "Anime", "3D Render", "Cartoon",
        "Cyberpunk", "Vintage Film", "Documentary", "Fantasy", "Nature"
    )
    var selectedStyle by remember { mutableStateOf("Cinematic") }

    val aspectRatios = listOf(
        AspectRatioOption("16:9", "16:9 Landscape", "YouTube", Icons.Default.Crop169),
        AspectRatioOption("9:16", "9:16 Portrait", "TikTok / Reels", Icons.Default.CropPortrait),
        AspectRatioOption("1:1", "1:1 Square", "Instagram", Icons.Default.CropSquare),
        AspectRatioOption("4:3", "4:3 Classic", "Retro", Icons.Default.Crop32),
        AspectRatioOption("21:9", "21:9 Cinema", "Ultra-Wide", Icons.Default.Crop75)
    )
    var selectedAspectRatio by remember { mutableStateOf(aspectRatios[0]) }

    var selectedDuration by remember { mutableStateOf("5s") }
    val durations = listOf("5 Seconds" to "5s", "10 Seconds" to "10s")

    var selectedResolution by remember { mutableStateOf("1080p") }
    val resolutions = listOf("720p", "1080p", "4K")

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 840.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero section with prompt ideas
            HeroSection(
                onPromptSuggested = { _, fullPrompt ->
                    prompt = fullPrompt
                }
            )

            if (isWide) {
                // Wide layout: Side by Side (Left Panel Controls, Right Panel Output)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Panel (Controls)
                    Box(modifier = Modifier.weight(1.1f)) {
                        CreationControlsColumn(
                            prompt = prompt,
                            onPromptChange = { prompt = it },
                            negativePrompt = negativePrompt,
                            onNegativePromptChange = { negativePrompt = it },
                            showNegativePrompt = showNegativePrompt,
                            onToggleNegativePrompt = { showNegativePrompt = !showNegativePrompt },
                            models = models,
                            selectedModel = selectedModel,
                            onSelectModel = { selectedModel = it },
                            sceneStyles = sceneStyles,
                            selectedStyle = selectedStyle,
                            onSelectStyle = { selectedStyle = it },
                            aspectRatios = aspectRatios,
                            selectedAspectRatio = selectedAspectRatio,
                            onSelectAspectRatio = { selectedAspectRatio = it },
                            durations = durations,
                            selectedDuration = selectedDuration,
                            onSelectDuration = { selectedDuration = it },
                            resolutions = resolutions,
                            selectedResolution = selectedResolution,
                            onSelectResolution = { selectedResolution = it },
                            isGenerating = isGenerating,
                            onGenerateClick = {
                                onGenerate(
                                    AiGenerationService.VideoGenerationParams(
                                        prompt = prompt,
                                        negativePrompt = negativePrompt,
                                        model = selectedModel.name,
                                        aspectRatio = selectedAspectRatio.id,
                                        duration = selectedDuration,
                                        resolution = selectedResolution,
                                        style = selectedStyle
                                    )
                                )
                            }
                        )
                    }

                    // Right Panel (Preview & Output)
                    Box(modifier = Modifier.weight(0.9f)) {
                        VideoPlayerPreview(
                            isGenerating = isGenerating,
                            progress = progress,
                            statusText = statusText,
                            activeCreation = activeCreation,
                            onDownloadClick = onDownload,
                            onDeleteClick = onDelete,
                            onUseAsImageSource = onUseAsImageSource
                        )
                    }
                }
            } else {
                // Mobile layout: Stacked
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    CreationControlsColumn(
                        prompt = prompt,
                        onPromptChange = { prompt = it },
                        negativePrompt = negativePrompt,
                        onNegativePromptChange = { negativePrompt = it },
                        showNegativePrompt = showNegativePrompt,
                        onToggleNegativePrompt = { showNegativePrompt = !showNegativePrompt },
                        models = models,
                        selectedModel = selectedModel,
                        onSelectModel = { selectedModel = it },
                        sceneStyles = sceneStyles,
                        selectedStyle = selectedStyle,
                        onSelectStyle = { selectedStyle = it },
                        aspectRatios = aspectRatios,
                        selectedAspectRatio = selectedAspectRatio,
                        onSelectAspectRatio = { selectedAspectRatio = it },
                        durations = durations,
                        selectedDuration = selectedDuration,
                        onSelectDuration = { selectedDuration = it },
                        resolutions = resolutions,
                        selectedResolution = selectedResolution,
                        onSelectResolution = { selectedResolution = it },
                        isGenerating = isGenerating,
                        onGenerateClick = {
                            onGenerate(
                                AiGenerationService.VideoGenerationParams(
                                    prompt = prompt,
                                    negativePrompt = negativePrompt,
                                    model = selectedModel.name,
                                    aspectRatio = selectedAspectRatio.id,
                                    duration = selectedDuration,
                                    resolution = selectedResolution,
                                    style = selectedStyle
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    VideoPlayerPreview(
                        isGenerating = isGenerating,
                        progress = progress,
                        statusText = statusText,
                        activeCreation = activeCreation,
                        onDownloadClick = onDownload,
                        onDeleteClick = onDelete,
                        onUseAsImageSource = onUseAsImageSource
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreationControlsColumn(
    prompt: String,
    onPromptChange: (String) -> Unit,
    negativePrompt: String,
    onNegativePromptChange: (String) -> Unit,
    showNegativePrompt: Boolean,
    onToggleNegativePrompt: () -> Unit,
    models: List<AiModelInfo>,
    selectedModel: AiModelInfo,
    onSelectModel: (AiModelInfo) -> Unit,
    sceneStyles: List<String>,
    selectedStyle: String,
    onSelectStyle: (String) -> Unit,
    aspectRatios: List<AspectRatioOption>,
    selectedAspectRatio: AspectRatioOption,
    onSelectAspectRatio: (AspectRatioOption) -> Unit,
    durations: List<Pair<String, String>>,
    selectedDuration: String,
    onSelectDuration: (String) -> Unit,
    resolutions: List<String>,
    selectedResolution: String,
    onSelectResolution: (String) -> Unit,
    isGenerating: Boolean,
    onGenerateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardGlassBackground)
            .border(1.dp, Color(0x33A855F7), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Section Header
        Text(
            text = "Video Prompt & Camera Direction",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Be descriptive. Include lighting, camera angles, movement, and atmospheric effects.",
            color = TextMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Large Prompt Input
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChange,
            placeholder = {
                Text(
                    text = "Describe your video idea in detail... a cinematic shot of...",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag("video_prompt_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F0F17),
                unfocusedContainerColor = Color(0xFF0C0C12),
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Negative Prompt Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleNegativePrompt() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Negative Prompt (Optional)",
                color = if (showNegativePrompt) NeonPink else TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = if (showNegativePrompt) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        AnimatedVisibility(visible = showNegativePrompt) {
            OutlinedTextField(
                value = negativePrompt,
                onValueChange = onNegativePromptChange,
                placeholder = {
                    Text(
                        text = "What to avoid: blurry, oversaturated, morphing, low quality...",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(top = 4.dp)
                    .testTag("negative_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0F0F17),
                    unfocusedContainerColor = Color(0xFF0C0C12),
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI MODEL SELECTOR (VERY IMPORTANT)
        Text(
            text = "AI Generation Model",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            models.forEach { model ->
                val isSelected = model == selectedModel
                val cardBorder = if (isSelected) model.brandColor else Color(0x22FFFFFF)
                val cardBg = if (isSelected) Color(0x1F8B5CF6) else Color(0x0CFFFFFF)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBg)
                        .border(1.5.dp, cardBorder, RoundedCornerShape(14.dp))
                        .clickable { onSelectModel(model) }
                        .padding(12.dp)
                        .testTag("model_selector_${model.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Indicator Dot / Glow
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) model.brandColor else Color(0x44FFFFFF))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (model.badge != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(model.brandColor.copy(alpha = 0.25f))
                                        .border(0.5.dp, model.brandColor, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = model.badge,
                                        color = model.brandColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = model.tag,
                            color = if (isSelected) NeonCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = model.description,
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SCENE STYLE SELECTOR
        Text(
            text = "Scene Style",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            sceneStyles.forEach { style ->
                val isSelected = style == selectedStyle
                val chipBg = if (isSelected) NeonPurple else Color(0x16FFFFFF)
                val textColor = if (isSelected) Color.White else TextSecondary
                val borderCol = if (isSelected) NeonPurple else Color(0x22FFFFFF)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(chipBg)
                        .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                        .clickable { onSelectStyle(style) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("style_chip_${style.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ASPECT RATIO SELECTOR
        Text(
            text = "Aspect Ratio",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            aspectRatios.forEach { option ->
                val isSelected = option == selectedAspectRatio
                val bg = if (isSelected) Color(0x253B82F6) else Color(0x14FFFFFF)
                val border = if (isSelected) NeonBlue else Color(0x22FFFFFF)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(12.dp))
                        .clickable { onSelectAspectRatio(option) }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.label,
                        tint = if (isSelected) NeonCyan else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = option.id,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = option.subtitle.take(7),
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DURATION & RESOLUTION SELECTORS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Duration
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Duration",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    durations.forEach { (label, value) ->
                        val isSelected = value == selectedDuration
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonPurple else Color(0x18FFFFFF))
                                .border(
                                    1.dp,
                                    if (isSelected) NeonPurple else Color(0x22FFFFFF),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectDuration(value) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = value,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Resolution
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Resolution",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    resolutions.forEach { res ->
                        val isSelected = res == selectedResolution
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonBlue else Color(0x18FFFFFF))
                                .border(
                                    1.dp,
                                    if (isSelected) NeonBlue else Color(0x22FFFFFF),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectResolution(res) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BIG GRADIENT GENERATE BUTTON WITH CUSTOM SPARKLE PLAY ICON
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PurpleBlueGradient)
                .clickable(enabled = !isGenerating && prompt.isNotBlank()) {
                    onGenerateClick()
                }
                .testTag("generate_video_button"),
            contentAlignment = Alignment.Center
        ) {
            if (isGenerating) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Synthesizing Motion with ${selectedModel.name}...",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SparklePlayIcon(
                        size = 22.dp,
                        sparkleColor = Color(0xFFFACC15),
                        playColor = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Generate Video • ${selectedModel.name}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}
