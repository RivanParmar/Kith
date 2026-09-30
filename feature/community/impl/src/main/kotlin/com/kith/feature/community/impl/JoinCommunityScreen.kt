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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.feature.community.impl.ui.MeshGradientBackground
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

    LaunchedEffect(uiState.joinSuccessful) {
        if (uiState.joinSuccessful) {
            onJoined()
        }
    }

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
                .imePadding()
                .verticalScroll(scrollState)
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
                if (!uiState.selectedCommunityImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = uiState.selectedCommunityImageUrl,
                        contentDescription = "Community Image",
                        modifier = Modifier
                            .size(250.dp, 200.dp)
                            .clip(RoundedCornerShape(60.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(250.dp, 200.dp)
                            .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(60.dp))
                    ) {
                        Icon(
                            imageVector = KithIcons.Groups,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.align(Alignment.Center).size(64.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Community Name Dropdown Field
            FieldLabel(text = "Community Name")
            MeshDropdownTextField(
                value = uiState.communityName,
                onValueChange = {
                    nameError = null
                    onCommunityNameChanged(it)
                },
                options = uiState.availableCommunities,
                placeholder = "Select or type community name",
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
private fun MeshDropdownTextField(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

    // Filters options as user types, or shows all when blank
    val filteredOptions = remember(value, options) {
        if (value.isBlank()) {
            options
        } else {
            options.filter { it.contains(value, ignoreCase = true) }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    expanded = true
                },
                placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.6f)) },
                singleLine = true,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size
                    },
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = KithIcons.ChevronForward,
                            contentDescription = if (expanded) "Close options" else "Show options",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.graphicsLayer {
                                rotationZ = if (expanded) 270f else 90f
                            }
                        )
                    }
                },
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
                    focusedTrailingIconColor = Color.White,
                    unfocusedTrailingIconColor = Color.White.copy(alpha = 0.85f)
                )
            )

            if (filteredOptions.isNotEmpty()) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                        .background(Color(0xFF0F2F7D).copy(alpha = 0.96f), RoundedCornerShape(12.dp))
                ) {
                    filteredOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                onValueChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

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

@Preview
@Composable
private fun JoinCommunityPreview() {
    var previewState by remember {
        mutableStateOf(
            JoinCommunityUiState(
                availableCommunities = listOf("Android Developers", "Kotlin Hub", "Kith Community", "Open Source")
            )
        )
    }

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