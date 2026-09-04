package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.VideFreeViewModel
import com.example.ui.components.FooterSection
import com.example.ui.components.NavTab
import com.example.ui.components.TopNavigationBar
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.ImageGeneratorScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.VideoGeneratorScreen
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.VideFreeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideFreeTheme {
                VideFreeApp()
            }
        }
    }
}

@Composable
fun VideFreeApp(
    viewModel: VideFreeViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val creditsRemaining by viewModel.creditsRemaining.collectAsStateWithLifecycle()
    val allCreations by viewModel.allCreations.collectAsStateWithLifecycle()

    // Video State
    val isGeneratingVideo by viewModel.isGeneratingVideo.collectAsStateWithLifecycle()
    val videoProgress by viewModel.videoProgress.collectAsStateWithLifecycle()
    val videoStatusText by viewModel.videoStatusText.collectAsStateWithLifecycle()
    val activeVideoCreation by viewModel.activeVideoCreation.collectAsStateWithLifecycle()

    // Image State
    val isGeneratingImage by viewModel.isGeneratingImage.collectAsStateWithLifecycle()
    val imageProgress by viewModel.imageProgress.collectAsStateWithLifecycle()
    val imageStatusText by viewModel.imageStatusText.collectAsStateWithLifecycle()
    val generatedImages by viewModel.generatedImages.collectAsStateWithLifecycle()
    val crossTabPrompt by viewModel.crossTabPrompt.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = DarkCanvas,
        topBar = {
            TopNavigationBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) },
                creditsRemaining = creditsRemaining,
                onCreditsClick = { viewModel.selectTab(NavTab.PRICING) }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("app_snackbar_host")
            ) { data ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1B2E))
                        .border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.VIDEO -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        VideoGeneratorScreen(
                            isGenerating = isGeneratingVideo,
                            progress = videoProgress,
                            statusText = videoStatusText,
                            activeCreation = activeVideoCreation ?: allCreations.firstOrNull { it.mediaType == com.example.data.model.MediaType.VIDEO },
                            onGenerate = { params -> viewModel.generateVideo(params) },
                            onDownload = { item -> viewModel.triggerDownload(item) },
                            onDelete = { item -> viewModel.deleteCreation(item) },
                            onUseAsImageSource = { item -> viewModel.useVideoAsImageSource(item) },
                            modifier = Modifier.weight(1f)
                        )
                        FooterSection()
                    }
                }

                NavTab.IMAGE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ImageGeneratorScreen(
                            isGenerating = isGeneratingImage,
                            progress = imageProgress,
                            statusText = imageStatusText,
                            generatedImages = if (generatedImages.isNotEmpty()) generatedImages
                            else allCreations.filter { it.mediaType == com.example.data.model.MediaType.IMAGE },
                            onGenerate = { params -> viewModel.generateImages(params) },
                            onDownload = { item -> viewModel.triggerDownload(item) },
                            initialPrompt = crossTabPrompt,
                            modifier = Modifier.weight(1f)
                        )
                        FooterSection()
                    }
                }

                NavTab.GALLERY -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        GalleryScreen(
                            creations = allCreations,
                            onDownload = { item -> viewModel.triggerDownload(item) },
                            onDelete = { item -> viewModel.deleteCreation(item) },
                            onRemixVideo = { item ->
                                viewModel.selectTab(NavTab.VIDEO)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FooterSection()
                    }
                }

                NavTab.PRICING -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PricingScreen(
                            currentCredits = creditsRemaining,
                            onPlanSelected = { plan -> viewModel.upgradePlan(plan) },
                            modifier = Modifier.weight(1f)
                        )
                        FooterSection()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VideFreeTheme { Greeting("VideFree") }
}
