package com.kith.feature.onboarding.impl

import android.graphics.Path
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
) {
    OnboardingScreen(
        modifier = modifier,
        onOnboardingCompleted = {
            viewModel.onCompleteOnboarding()
            navigateToHome()
        },
    )
}

@Composable
internal fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onOnboardingCompleted: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 4 })

    val fraction = remember { derivedStateOf { pagerState.currentPageOffsetFraction } }

    val coroutineScope = rememberCoroutineScope()

    val xpFillProgress = remember { Animatable(0f) }
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 2) {
            xpFillProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
        } else {
            xpFillProgress.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF5B8DF9))
    ) {
        AnimatedBackground(
            pagerState.currentPage,
            fraction.value,
            xpFillProgress = xpFillProgress.value,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .align(Alignment.Center)
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->

                when (page) {
                    0 -> OnboardingPageOne(
                        page = page,
                        pagerState = pagerState,
                    )

                    1 -> OnboardingPageTwo(
                        page = page,
                        pagerState = pagerState,
                    )

                    2 -> OnboardingPageThree(
                        page = page,
                        pagerState = pagerState,
                    )

                    3 -> OnboardingPageFour(
                        page = page,
                        pagerState = pagerState,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 72.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 72.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color by animateColorAsState(
                            targetValue = if (pagerState.currentPage == iteration)
                                Color(0xFF0050FF)
                            else
                                Color.White,
                            animationSpec = tween(easing = EaseInOut)
                        )
                        val width by animateDpAsState(
                            targetValue = if (pagerState.currentPage == iteration)
                                52.dp
                            else
                                16.dp,
                            animationSpec = tween(easing = EaseInOut)
                        )
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(height = 16.dp, width = width)
                                .clickable(onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(iteration)
                                    }
                                })
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        if (pagerState.settledPage != pagerState.pageCount - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.settledPage + 1)
                            }
                        } else {
                            onOnboardingCompleted()
                        }
                    },
                    containerColor = Color(0xFF00236E)
                ) {
                    Icon(
                        imageVector = KithIcons.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val backgroundTracks = listOf(
    ShapeTrack(
        keyframes = listOf(
            ShapeKeyframe(
                shape = MaterialShapes.Arrow,
                xFraction = 0.35f,
                yFraction = 0.64f,
                rotation = -31.5f,
                scaleMultiplier = 1.0f,
            ),
            ShapeKeyframe(
                shape = MaterialShapes.Arrow,
                xFraction = -0.13f,
                yFraction = 0.64f,
                rotation = 31.5f,
                scaleMultiplier = 1.0f,
            ),
            ShapeKeyframe(
                shape = MaterialShapes.Burst,
                xFraction = 0.32f,
                yFraction = 0.65f,
                rotation = 0f,
                scaleMultiplier = 1.0f,
            ),
            ShapeKeyframe(
                shape = MaterialShapes.Bun,
                xFraction = 0.4f,
                yFraction = 0.73f,
                rotation = -31.5f,
                scaleMultiplier = 0.85f,
            ),
        )
    ),
    ShapeTrack(
        keyframes = listOf(
            ShapeKeyframe(
                shape = MaterialShapes.Cookie4Sided,
                xFraction = -0.09f,
                yFraction = 0.08f,
                rotation = -21.6f,
                scaleMultiplier = 0.85f,
            ),
            ShapeKeyframe(
                shape = MaterialShapes.Pill,
                xFraction = 0.6f,
                yFraction = -0.1f,
                rotation = 0f,
                scaleMultiplier = 0.85f,
            ),
            ShapeKeyframe(
                shape = MaterialShapes.Cookie6Sided,
                xFraction = 0.145f,
                yFraction = 0.07f,
                rotation = 0f,
                scaleMultiplier = 0.85f,
            ),
            ShapeKeyframe(
                shape = MaterialShapes.Diamond,
                xFraction = -0.1f,
                yFraction = 0.1f,
                rotation = -25.6f,
                scaleMultiplier = 0.85f,
            ),
        )
    )
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedBackground(
    currentPage: Int,
    offsetFraction: Float,
    xpFillProgress: Float,
    modifier: Modifier = Modifier,
    tracks: List<ShapeTrack> = backgroundTracks,
) {
    val androidPath = remember { Path() }
    val textMeasurer = rememberTextMeasurer()
    val androidPathMeasure = remember { android.graphics.PathMeasure() }
    val animatedOutlineAndroidPath = remember { android.graphics.Path() }

    val trackMorphs = remember(tracks) {
        tracks.map { track ->
            List(track.keyframes.size - 1) { i ->
                Morph(track.keyframes[i].shape, track.keyframes[i + 1].shape)
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val absolutePosition = (currentPage + offsetFraction).coerceIn(0f, 3f)

        val maxIndex = (tracks.firstOrNull()?.keyframes?.size ?: 1) - 2
        val fromIndex = absolutePosition.toInt().coerceIn(0, maxIndex.coerceAtLeast(0))
        val progress = absolutePosition - fromIndex

        val page2Alpha = (1f - abs(absolutePosition - 2f) * 2f).coerceIn(0f, 1f)

        tracks.forEachIndexed { trackIndex, track ->
            val startState = track.keyframes[fromIndex]
            val endState = track.keyframes[fromIndex + 1]
            val activeMorph = trackMorphs[trackIndex][fromIndex]

            val currentX = lerp(width * startState.xFraction, width * endState.xFraction, progress)
            val currentY =
                lerp(height * startState.yFraction, height * endState.yFraction, progress)
            val currentRotation = lerp(startState.rotation, endState.rotation, progress)
            val currentScale = lerp(startState.scaleMultiplier, endState.scaleMultiplier, progress)

            androidPath.rewind()
            activeMorph.toPath(progress = progress, path = androidPath)

            val composePath = androidPath.asComposePath()
            val finalScale = (minOf(width, height) * 0.83f) * currentScale
            val scaleMatrix = Matrix().apply {
                scale(x = finalScale, y = finalScale)
            }
            composePath.transform(scaleMatrix)

            val pathBounds = composePath.getBounds()

            val trueCenter = pathBounds.center

            val dynamicRadius = maxOf(pathBounds.width, pathBounds.height) / 2f

            val gradientBrush = Brush.radialGradient(
                0.0f to Color(0xFF608DEF),
                0.2f to Color(0xFF608DEF),
                1.0f to Color(0xFF2A67EC),
                center = trueCenter,
                radius = dynamicRadius.coerceAtLeast(1f)
            )

            translate(left = currentX, top = currentY) {
                rotate(degrees = currentRotation, pivot = trueCenter) {
                    drawPath(
                        path = composePath,
                        brush = gradientBrush,
                    )

                    if (trackIndex == 1 && page2Alpha > 0f) {
                        val outlineMatrix = Matrix().apply {
                            translate(trueCenter.x, trueCenter.y)
                            scale(1.15f, 1.15f)
                            translate(-trueCenter.x, -trueCenter.y)
                        }

                        val scaledOutline = androidx.compose.ui.graphics.Path().apply {
                            addPath(composePath)
                            transform(outlineMatrix)
                        }

                        androidPathMeasure.setPath(scaledOutline.asAndroidPath(), false)
                        animatedOutlineAndroidPath.rewind()
                        androidPathMeasure.getSegment(
                            0f,
                            androidPathMeasure.length * xpFillProgress,
                            animatedOutlineAndroidPath,
                            true
                        )

                        drawPath(
                            path = animatedOutlineAndroidPath.asComposePath(),
                            color = Color.White.copy(alpha = 0.3f * page2Alpha),
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                if (trackIndex == 1 && page2Alpha > 0f) {
                    val circleRadius = dynamicRadius * 0.55f

                    drawArc(
                        color = Color.White.copy(alpha = 0.15f * page2Alpha),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(trueCenter.x - circleRadius, trueCenter.y - circleRadius),
                        size = Size(circleRadius * 2, circleRadius * 2)
                    )

                    drawArc(
                        color = Color(0xFF1A1A1A).copy(alpha = page2Alpha),
                        startAngle = 270f,
                        sweepAngle = 90f * xpFillProgress,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(trueCenter.x - circleRadius, trueCenter.y - circleRadius),
                        size = Size(circleRadius * 2, circleRadius * 2)
                    )

                    val textToDraw = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White.copy(alpha = page2Alpha),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        ) { append("1,450\n") }
                        withStyle(
                            SpanStyle(
                                color = Color.White.copy(alpha = 0.7f * page2Alpha),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        ) { append("LEVEL 3") }
                    }
                    val textLayoutResult = textMeasurer.measure(
                        text = textToDraw, style = TextStyle(
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    )

                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = trueCenter.x - textLayoutResult.size.width / 2f,
                            y = trueCenter.y - textLayoutResult.size.height / 2f
                        )
                    )
                }
            }
        }
    }
}

private data class ShapeKeyframe(
    val shape: RoundedPolygon,
    val xFraction: Float,
    val yFraction: Float,
    val rotation: Float,
    val scaleMultiplier: Float = 1.0f,
)

private data class ShapeTrack(
    val keyframes: List<ShapeKeyframe>,
)

@Preview(showBackground = true)
@Composable
fun BackgroundPreview() {
    var sliderValue by remember { mutableFloatStateOf(0f) }

    val simulatedPage = sliderValue.toInt().coerceAtMost(3)
    val simulatedOffset = sliderValue - simulatedPage

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(
            currentPage = simulatedPage,
            offsetFraction = simulatedOffset,
            xpFillProgress = 0f,
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Timeline Position: ${String.format("%.2f", sliderValue)}")
            Slider(
                state = rememberSliderState(
                    value = sliderValue,
                    steps = 0,
                    trackRange = 0f..3f,
                ),
                modifier = Modifier,
                enabled = true,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = null,
                colors = SliderDefaults.colors(),
                interactionSource = remember { MutableInteractionSource() },
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingPreview() {
    KithTheme {
        OnboardingScreen(
            onOnboardingCompleted = {},
        )
    }
}