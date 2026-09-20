package com.kith.feature.community.impl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.app.features.joincommunity.JoinCommunityEvent
import com.kith.app.features.joincommunity.JoinCommunityViewModel
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun JoinCommunityScreen(
    modifier: Modifier = Modifier,
    viewModel: JoinCommunityViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onCreateCommunity: () -> Unit,
    onFindCommunity: () -> Unit,
    onJoined: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    JoinCommunityScreen(
        uiState = uiState,
        onCommunityNameChanged = viewModel::onCommunityNameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onBackClicked = {},
        onJoinClicked = viewModel::onJoinClicked,
        onCantFindClicked = {},
        onCreateClicked = {},
    )
}

@Composable
internal fun JoinCommunityScreen(
    uiState: JoinCommunityUiState,
    onCommunityNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onBackClicked: () -> Unit = {},
    onJoinClicked: () -> Unit = {},
    onCantFindClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
) {
    MeshGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            InteractiveIconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = KithIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join a\nCommunity",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Soft rounded "avatar" shape holding the placeholder shapes,
            // matching the pill-shaped icon container in the design.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(250.dp, 200.dp)
                        .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(60.dp))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            FieldLabel(text = "Community Name")
            MeshTextField(
                value = uiState.communityName,
                onValueChange = onCommunityNameChanged,
                placeholder = "Enter Community Name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel(text = "Password")
            MeshTextField(
                value = uiState.communityPassword,
                onValueChange = onPasswordChanged,
                placeholder = "Enter Community Password",
                isPassword = true
            )

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                ShakingErrorText(message = message)
            }

            Spacer(modifier = Modifier.height(28.dp))

            InteractiveButton(
                onClick = onJoinClicked,
                enabled = uiState.isJoinEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isJoining) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF1B3ADB),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Join", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InteractiveTextButton(onClick = onCantFindClicked) {
                    Text(
                        text = "Can't find a community?",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
                InteractiveTextButton(onClick = onCreateClicked) {
                    Text(
                        text = "Create One",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.75f),
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

/**
 * Main CTA button with a press-scale "squish" — shrinks slightly on press and
 * springs back on release, on top of the standard ripple, so it feels tactile
 * rather than just a flat click target.
 */
@Composable
private fun InteractiveButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsStateCompat()
    val scale by animateDpAsState(
        targetValue = if (isPressed) (-2).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonPressScale"
    )
    val scaleFactor = 1f + (scale.value / 100f)

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier.graphicsLayer {
            scaleX = scaleFactor
            scaleY = scaleFactor
        },
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1B3ADB),
            disabledContainerColor = Color.White.copy(alpha = 0.6f)
        ),
        content = content
    )
}

/**
 * Text buttons ("Can't find a community?" / "Create One") get the same
 * subtle press-scale so the whole screen feels consistently tactile.
 */
@Composable
private fun InteractiveTextButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsStateCompat()
    val scale by animateDpAsState(
        targetValue = if (isPressed) (-3).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "textButtonPressScale"
    )
    val scaleFactor = 1f + (scale.value / 100f)

    TextButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier.graphicsLayer {
            scaleX = scaleFactor
            scaleY = scaleFactor
        },
        content = content
    )
}

@Composable
private fun InteractiveIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsStateCompat()
    val scale by animateDpAsState(
        targetValue = if (isPressed) (-3).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconButtonPressScale"
    )
    val scaleFactor = 1f + (scale.value / 100f)

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier.graphicsLayer {
            scaleX = scaleFactor
            scaleY = scaleFactor
        }
    ) {
        content()
    }
}

/**
 * Small compat helper — collects "is pressed" from a MutableInteractionSource
 * without pulling in an extra foundation import name clash.
 */
@Composable
private fun MutableInteractionSource.collectIsPressedAsStateCompat() =
    produceState(initialValue = false, this) {
        this@collectIsPressedAsStateCompat.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> value = true
                is PressInteraction.Release, is PressInteraction.Cancel -> value = false
            }
        }
    }

/**
 * Text field with an animated glow border — the border brightens and thickens
 * smoothly when the field gains focus, instead of just snapping to a new color,
 * so typing into it feels responsive rather than static.
 */
@Composable
private fun MeshTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "fieldBorderWidth"
    )

    // Only relevant for the password field — toggled by the eye icon below.
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.6f)) },
        singleLine = true,
        interactionSource = interactionSource,
        visualTransformation = when {
            !isPassword -> VisualTransformation.None
            isPasswordVisible -> VisualTransformation.None
            else -> PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) KithIcons.VisibilityOff else KithIcons.VisibilityOn,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White.copy(alpha = 0.9f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
            cursorColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.06f)
        )
    )
    // Note: OutlinedTextField doesn't expose border width directly, so if you
    // want the thickening effect too (not just the color animation Material3
    // already gives you for free), wrap this in a Box with Modifier.border(
    // borderWidth, ...) and drop the built-in outline via a transparent shape.
}

/**
 * Error text that shakes side to side once when it first appears, so a failed
 * join attempt actually catches your eye instead of quietly fading in.
 */
@Composable
private fun ShakingErrorText(message: String) {
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(message) {
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            )
        )
        val shakeSequence = listOf(-10f, 10f, -8f, 8f, -4f, 4f, 0f)
        for (target in shakeSequence) {
            offsetX.animateTo(
                targetValue = target,
                animationSpec = spring(stiffness = Spring.StiffnessHigh)
            )
        }
    }

    Text(
        text = message,
        color = Color(0xFFFFD2D2),
        fontSize = 13.sp,
        modifier = Modifier.graphicsLayer {
            translationX = offsetX.value
        }
    )
}

/**
 * The mesh gradient background, tuned to match the reference screenshot:
 * a soft, bright pool of light sitting in the upper-left/center that blends
 * smoothly down into a deep, saturated blue at the bottom of the screen.
 * We layer a base diagonal gradient with a few large, heavily-blurred radial
 * "blobs" so the transitions are soft rather than banded, like a real mesh gradient.
 */
@Composable
fun MeshGradientBackground(content: @Composable BoxScope.() -> Unit) {
    val meshGradientPainter = remember {
        MeshGradientPainter(4, 4) {
            setVertex(0, 0, Offset(0f, 0f), Color(0xFF5B8DF9))
            setVertex(0, 1, Offset(0.33f, 0f), Color(0xFF5B8DF9))
            setVertex(0, 2, Offset(0.66f, 0f), Color(0xFF0841BE))
            setVertex(0, 3, Offset(1f, 0f), Color(0xFF002A88))

            setVertex(1, 0, Offset(0f, 0.33f), Color(0xFF003ABA))
            setVertex(1, 1, Offset(0.33f, 0.33f), Color(0xFF5B8DF9))
            setVertex(1, 2, Offset(0.66f, 0.33f), Color(0xFF5B8DF9))
            setVertex(1, 3, Offset(1f, 0.33f), Color(0xFF5B8DF9))

            setVertex(2, 0, Offset(0f, 0.66f), Color(0xFF2563EB))
            setVertex(2, 1, Offset(0.33f, 0.66f), Color(0xFF608DEF))
            setVertex(2, 2, Offset(0.66f, 0.66f), Color(0xFF2E60CC))
            setVertex(2, 3, Offset(1f, 0.66f), Color(0xFF608DEF))

            setVertex(3, 0, Offset(0f, 1f), Color(0xFF5B8DF9))
            setVertex(3, 1, Offset(0.33f, 1f), Color(0xFF4D75CC))
            setVertex(3, 2, Offset(0.66f, 1f), Color(0xFF1243AE))
            setVertex(3, 3, Offset(1f, 1f), Color(0xFF0036AD))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(painter = meshGradientPainter),
    ) {
        content()
    }
}

@Preview
@Composable
private fun JoinCommunityPreview() {
    // Interactive Mode has no real ViewModel behind it, so we hold local
    // state here and feed typed input straight back into uiState — otherwise
    // onCommunityNameChanged/onPasswordChanged do nothing and typing appears dead.
    var previewState by remember { mutableStateOf(JoinCommunityUiState()) }

    KithTheme {
        JoinCommunityScreen(
            uiState = previewState,
            onCommunityNameChanged = { previewState = previewState.copy(communityName = it) },
            onPasswordChanged = { previewState = previewState.copy(communityPassword = it) },
            onBackClicked = {},
            onJoinClicked = {},
            onCantFindClicked = {},
            onCreateClicked = {},
        )
    }
}