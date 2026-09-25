package com.kith.feature.community.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import kotlinx.coroutines.launch

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
        onBackClicked = onBack,
        onJoinClicked = viewModel::onJoinClicked,
        onCantFindClicked = onFindCommunity,
        onCreateClicked = onCreateCommunity,
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
    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    MeshGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Adjusts scrollable viewport when keyboard opens
                .verticalScroll(scrollState) // Full page scrollable while typing
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

            // Soft rounded avatar placeholder
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

            // Community Name Field
            FieldLabel(text = "Community Name")
            MeshTextField(
                value = uiState.communityName,
                onValueChange = {
                    nameError = null
                    onCommunityNameChanged(it)
                },
                placeholder = "Enter Community Name",
                isError = nameError != null,
                errorMessage = nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            FieldLabel(text = "Password")
            MeshTextField(
                value = uiState.communityPassword,
                onValueChange = {
                    passwordError = null
                    onPasswordChanged(it)
                },
                placeholder = "Enter Community Password",
                isPassword = true,
                isError = passwordError != null,
                errorMessage = passwordError
            )

            uiState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                ShakingErrorText(message = message)
            }

            Spacer(modifier = Modifier.height(28.dp))

            InteractiveButton(
                onClick = {
                    var isValid = true

                    if (uiState.communityName.trim().isEmpty()) {
                        nameError = "Community name is required"
                        isValid = false
                    }
                    if (uiState.communityPassword.trim().isEmpty()) {
                        passwordError = "Password is required"
                        isValid = false
                    }

                    if (isValid) {
                        onJoinClicked()
                    } else {
                        // Automatically scroll UP so the user immediately sees the top missing fields & errors
                        coroutineScope.launch {
                            scrollState.animateScrollTo(0)
                        }
                    }
                },
                enabled = !uiState.isJoining,
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
            Spacer(modifier = Modifier.navigationBarsPadding())
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

@Composable
private fun MeshTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    // rememberSaveable ensures the visibility state isn't lost on keyboard display or recomposition
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.6f)) },
            singleLine = true,
            isError = isError,
            interactionSource = interactionSource,
            visualTransformation = when {
                !isPassword -> VisualTransformation.None
                isPasswordVisible -> VisualTransformation.None
                else -> PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword && !isPasswordVisible) {
                    KeyboardType.Password
                } else {
                    KeyboardType.Text
                }
            ),
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) KithIcons.VisibilityOff else KithIcons.Visibility,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            tint = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = if (isError) Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.9f),
                unfocusedBorderColor = if (isError) Color(0xFFFF6B6B) else Color.White.copy(alpha = 0.4f),
                cursorColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.06f),
                errorBorderColor = Color(0xFFFF6B6B),
                errorContainerColor = Color.White.copy(alpha = 0.08f),
                errorTextColor = Color.White,
                errorCursorColor = Color.White,
                errorTrailingIconColor = Color.White.copy(alpha = 0.85f)
            )
        )

        // Shaking error message directly below the field
        AnimatedVisibility(
            visible = isError && !errorMessage.isNullOrBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(4.dp))
                ShakingErrorText(message = msg)
            }
        }
    }
}

@Composable
private fun ShakingErrorText(message: String) {
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(message) {
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
        fontSize = 12.sp,
        modifier = Modifier
            .padding(start = 6.dp)
            .graphicsLayer {
                translationX = offsetX.value
            }
    )
}

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