package com.kith.feature.auth.impl

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kith.auth.resetpassword.ResetPasswordViewModel

private val KithBlue = Color(0xFF6B8AF6)
private val ButtonSurface = Color(0xFFF2F5FF)
private val FieldOutline = Color(0xFFBFD0FF)
private val ErrorPink = Color(0xFFFFD6D6)

/** Stateful entry point. Owns the ViewModel, nothing else. */
@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    onResetSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Password updated")
            viewModel.onSuccessHandled()
            onResetSuccess()
        }
    }

    ResetPasswordContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onToggleConfirmVisibility = viewModel::toggleConfirmVisibility,
        onResetClick = viewModel::onResetClick
    )
}

/** Stateless. This is the one the previews render. */
@Composable
fun ResetPasswordContent(
    state: ResetPasswordUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onToggleConfirmVisibility: () -> Unit = {},
    onResetClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = KithBlue,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            WaveHeader()

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Reset Your Password",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(32.dp))

            PasswordField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "Enter a new password",
                visible = state.passwordVisible,
                onToggleVisibility = onTogglePasswordVisibility,
                error = state.passwordError,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(Modifier.height(16.dp))

            PasswordField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm New Password",
                placeholder = "Re-enter the new password",
                visible = state.confirmVisible,
                onToggleVisibility = onToggleConfirmVisibility,
                error = state.confirmError,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    onResetClick()
                }
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onResetClick,
                enabled = state.canSubmit,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonSurface,
                    contentColor = KithBlue,
                    disabledContainerColor = ButtonSurface.copy(alpha = 0.5f),
                    disabledContentColor = KithBlue.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = KithBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text("Reset Password", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?,
    imeAction: ImeAction,
    onImeAction: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it, color = ErrorPink) } },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Hide password" else "Show password"
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = FieldOutline,
            errorBorderColor = ErrorPink,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = FieldOutline,
            errorLabelColor = ErrorPink,
            focusedPlaceholderColor = FieldOutline,
            unfocusedPlaceholderColor = FieldOutline,
            focusedLeadingIconColor = Color.White,
            unfocusedLeadingIconColor = FieldOutline,
            focusedTrailingIconColor = Color.White,
            unfocusedTrailingIconColor = FieldOutline,
            cursorColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun WaveHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White)
    ) {
        // Drop your topographic pattern here:
        // Image(
        //     painter = painterResource(R.drawable.topo_pattern),
        //     contentDescription = null,
        //     contentScale = ContentScale.Crop,
        //     modifier = Modifier.matchParentSize()
        // )

        Text(
            text = "KITH",
            color = Color.Black,
            fontSize = 44.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-30).dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
        ) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, h * 0.55f)
                cubicTo(w * 0.25f, h * 0.15f, w * 0.55f, h * 0.95f, w, h * 0.35f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path, KithBlue)
        }
    }
}

/* ----------  Previews  ---------- */

@Preview(name = "Empty", showBackground = true, device = "id:pixel_6")
@Composable
private fun ResetPasswordEmptyPreview() {
    MaterialTheme {
        ResetPasswordContent(state = ResetPasswordUiState())
    }
}

@Preview(name = "Filled", showBackground = true, device = "id:pixel_6")
@Composable
private fun ResetPasswordFilledPreview() {
    MaterialTheme {
        ResetPasswordContent(
            state = ResetPasswordUiState(
                password = "Kith@2026",
                confirmPassword = "Kith@2026",
                passwordVisible = true
            )
        )
    }
}

@Preview(name = "Mismatch error", showBackground = true, device = "id:pixel_6")
@Composable
private fun ResetPasswordErrorPreview() {
    MaterialTheme {
        ResetPasswordContent(
            state = ResetPasswordUiState(
                password = "kith2026",
                confirmPassword = "kith20",
                passwordError = "Add at least one uppercase letter",
                confirmError = "Passwords don't match"
            )
        )
    }
}

@Preview(name = "Loading", showBackground = true, device = "id:pixel_6")
@Composable
private fun ResetPasswordLoadingPreview() {
    MaterialTheme {
        ResetPasswordContent(
            state = ResetPasswordUiState(
                password = "Kith@2026",
                confirmPassword = "Kith@2026",
                isLoading = true
            )
        )
    }
}