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
import androidx.compose.foundation.shape.CircleShape
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
import com.kith.app.features.createcommunity.CreateCommunityEvent
import com.kith.app.features.createcommunity.CreateCommunityViewModel
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen entry point — wires the ViewModel state/events to the stateless UI below.
 */
@Composable
fun CreateCommunityRoute(
    viewModel: CreateCommunityViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onCreated: () -> Unit,
    onPickIcon: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                CreateCommunityEvent.NavigateBack -> onBack()
                CreateCommunityEvent.NavigateToHome -> onCreated()
                CreateCommunityEvent.OpenIconPicker -> onPickIcon()
            }
        }
    }

    CreateCommunityScreen(
        uiState = uiState,
        onCommunityNameChanged = viewModel::onCommunityNameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onBackClicked = viewModel::onBackClicked,
        onAddIconClicked = viewModel::onAddIconClicked,
        onCreateClicked = viewModel::onCreateClicked
    )
}

/**
 * Pure, stateless UI — easy to preview and test since it only depends on the state passed in.
 */
@Composable
fun CreateCommunityScreen(
    uiState: CreateCommunityUiState,
    onCommunityNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onBackClicked: () -> Unit,
    onAddIconClicked: () -> Unit,
    onCreateClicked: () -> Unit
) {
    MeshGradientBackgroundCreateCommunity {
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
                text = "Create a\nCommunity",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar/icon picker — the pill-shaped placeholder plus a floating
            // "+" button sitting on its bottom-right corner.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(230.dp, 190.dp)
                        .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(60.dp))
                )

                InteractiveAddIconButton(
                    onClick = onAddIconClicked,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 95.dp, y = 80.dp) // Adjusted y from 55.dp to 80.dp to position it lower
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            FieldLabel(text = "Community Name")
            MeshTextField(
                value = uiState.communityName,
                onValueChange = onCommunityNameChanged,
                placeholder = "Enter Your Community Name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel(text = "Password")
            MeshTextField(
                value = uiState.communityPassword,
                onValueChange = onPasswordChanged,
                placeholder = "Enter Community Password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel(text = "Community Description")
            MeshTextField(
                value = uiState.communityDescription,
                onValueChange = onDescriptionChanged,
                placeholder = "Describe your community...",
                singleLine = false,
                minLines = 3
            )

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                ShakingErrorText(message = message)
            }

            Spacer(modifier = Modifier.height(28.dp))

            InteractiveButton(
                onClick = onCreateClicked,
                enabled = uiState.isCreateEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF1B3ADB),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Create", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
 * springs back on release, on top of the standard ripple.
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
 * The small floating "+" circle sitting on the avatar's corner — opens the
 * icon/photo picker. Gets the same press-scale treatment as everything else.
 */
@Composable
private fun InteractiveAddIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsStateCompat()
    val scale by animateDpAsState(
        targetValue = if (isPressed) (-4).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "addIconPressScale"
    )
    val scaleFactor = 1f + (scale.value / 100f)

    Box(
        modifier = modifier
            .padding(2.dp, 4.dp)
            .size(45.dp)
            .graphicsLayer {
                scaleX = scaleFactor
                scaleY = scaleFactor
            }
            .background(Color(0xFF1B3ADB), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, interactionSource = interactionSource) {
            Icon(
                imageVector = KithIcons.Add,
                contentDescription = "Add community icon",
                tint = Color.White
            )
        }
    }
}

/**
 * Small compat helper — collects "is pressed" from a MutableInteractionSource.
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
 * Text field with an animated glow border on focus, an optional eye-toggle
 * for password fields, and optional multiline support for the description box.
 */
@Composable
private fun MeshTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "fieldBorderWidth"
    )

    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.6f)) },
        singleLine = singleLine,
        minLines = minLines,
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
}

/**
 * Error text that shakes side to side once when it first appears.
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
 * Same mesh gradient background as the Join Community screen — kept
 * byte-for-byte identical so both screens feel like one continuous flow.
 */
@Composable
fun MeshGradientBackgroundCreateCommunity(content: @Composable BoxScope.() -> Unit) {
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
private fun CreateCommunityPreview() {
    var previewState by remember { mutableStateOf(CreateCommunityUiState()) }

    KithTheme {
        CreateCommunityScreen(
            uiState = previewState,
            onCommunityNameChanged = { previewState = previewState.copy(communityName = it) },
            onPasswordChanged = { previewState = previewState.copy(communityPassword = it) },
            onDescriptionChanged = { previewState = previewState.copy(communityDescription = it) },
            onBackClicked = {},
            onAddIconClicked = {},
            onCreateClicked = {},
        )
    }
}