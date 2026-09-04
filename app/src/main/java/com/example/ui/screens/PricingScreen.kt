package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardGlassBackground
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleBlueGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class PricingPlan(
    val id: String,
    val name: String,
    val price: String,
    val period: String,
    val description: String,
    val badge: String? = null,
    val isPopular: Boolean = false,
    val credits: Int,
    val features: List<String>
)

@Composable
fun PricingScreen(
    currentCredits: Int,
    onPlanSelected: (PricingPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    val plans = listOf(
        PricingPlan(
            id = "free",
            name = "Starter Free",
            price = "$0",
            period = "forever",
            description = "Explore generative AI with standard speed",
            credits = 150,
            features = listOf(
                "150 Daily AI Credits",
                "Google Veo 3 & Nano Banana",
                "720p & 1080p Resolution",
                "Standard Queue Processing",
                "Local Room Storage"
            )
        ),
        PricingPlan(
            id = "creator",
            name = "Creator Studio",
            price = "$19",
            period = "per month",
            description = "For creators wanting high-speed 4K renders",
            badge = "MOST POPULAR",
            isPopular = true,
            credits = 2500,
            features = listOf(
                "2,500 Monthly AI Credits",
                "Unrestricted Veo 3, Sora & Seedance",
                "Ultra HD 4K & 60 FPS Output",
                "High-Priority GPU Queue",
                "Zero Watermarks & Commercial License",
                "Fast Nano Banana 4-Image Grid"
            )
        ),
        PricingPlan(
            id = "pro_studio",
            name = "Pro Studio Enterprise",
            price = "$49",
            period = "per month",
            description = "Maximum throughput for production teams & studios",
            badge = "MAX POWER",
            credits = 10000,
            features = listOf(
                "10,000 Monthly AI Credits",
                "Dedicated H100 Tensor Cloud Pipelines",
                "Instant Video Synthesis (< 5s)",
                "21:9 Ultra-Wide Cinematic Aspect Ratio",
                "Direct API Key Integration & Webhooks",
                "24/7 Dedicated AI Engineer Support"
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero title
        Text(
            text = "Flexible Studio Plans",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Free Your Imagination Into Motion with Next-Gen Compute",
            color = NeonCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Current credits banner
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x223B82F6))
                .border(1.dp, Color(0x443B82F6), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color(0xFFFACC15),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Active Account: $currentCredits Credits Available",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Cards list
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            plans.forEach { plan ->
                val cardBorder = if (plan.isPopular) NeonPurple else Color(0x22FFFFFF)
                val cardBg = if (plan.isPopular) Color(0xFF161226) else CardGlassBackground

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBg)
                        .border(if (plan.isPopular) 2.dp else 1.dp, cardBorder, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                        .testTag("pricing_card_${plan.id}")
                ) {
                    Column {
                        // Badge
                        if (plan.badge != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (plan.isPopular) NeonPurple else Color(0x3306B6D4)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = plan.badge,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Title & Price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = plan.name,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = plan.description,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = plan.price,
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "/${plan.period.take(2)}",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Features List
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            plan.features.forEach { feature ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (plan.isPopular) NeonPurple.copy(alpha = 0.3f)
                                                else Color(0x22FFFFFF)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (plan.isPopular) NeonPurple else NeonCyan,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = feature,
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Upgrade Button
                        val buttonBrush = if (plan.isPopular) PurpleBlueGradient
                        else Brush.horizontalGradient(listOf(Color(0x22FFFFFF), Color(0x22FFFFFF)))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(buttonBrush)
                                .clickable { onPlanSelected(plan) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (plan.id == "free") "Current Free Plan" else "Select ${plan.name}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
