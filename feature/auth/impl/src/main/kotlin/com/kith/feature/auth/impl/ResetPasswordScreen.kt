package com.kith.feature.auth.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.designsystem.theme.KithTheme
import com.kith.feature.auth.api.R
import com.kith.feature.auth.impl.ui.AuthLayout
import com.kith.feature.auth.impl.ui.PasswordField

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    onResetSuccess: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onResetClick = viewModel::onResetClick,
        onSuccessHandled = viewModel::onSuccessHandled,
        onResetSuccess = onResetSuccess,
    )
}

@Composable
internal fun ResetPasswordScreen(
    state: ResetPasswordUiState,
    modifier: Modifier = Modifier,
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onResetClick: () -> Unit = {},
    onSuccessHandled: () -> Unit = {},
    onResetSuccess: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val newPasswordTextFieldState = rememberTextFieldState()
    val confirmPasswordTextFieldState = rememberTextFieldState()

    LaunchedEffect(newPasswordTextFieldState) {
        snapshotFlow { newPasswordTextFieldState.text }
            .collect { onPasswordChange(it.toString()) }
    }

    LaunchedEffect(confirmPasswordTextFieldState) {
        snapshotFlow { confirmPasswordTextFieldState.text }
            .collect { onConfirmPasswordChange(it.toString()) }
    }

    LaunchedEffect(state) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Password updated!")
            onSuccessHandled()
            onResetSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuthLayout(modifier = modifier) {
            Text(
                text = stringResource(R.string.feature_auth_api_reset_password_title),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(32.dp))

//            PasswordField(
//                value = state.password,
//                onValueChange = onPasswordChange,
//                label = "Password",
//                placeholder = "Enter a new password",
//                visible = state.passwordVisible,
//                onToggleVisibility = onTogglePasswordVisibility,
//                error = state.passwordError,
//                imeAction = ImeAction.Next,
//                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
//            )

            PasswordField(
                labelRes = R.string.feature_auth_api_new_password,
                placeholderRes = R.string.feature_auth_api_enter_new_password,
                error = state.passwordError,
                textFieldState = newPasswordTextFieldState,
                enabled = !(state.isLoading || state.isSuccess),
            )

            Spacer(Modifier.height(16.dp))

//            PasswordField(
//                value = state.confirmPassword,
//                onValueChange = onConfirmPasswordChange,
//                label = "Confirm New Password",
//                placeholder = "Re-enter the new password",
//                visible = state.confirmVisible,
//                onToggleVisibility = onToggleConfirmVisibility,
//                error = state.confirmError,
//                imeAction = ImeAction.Done,
//                onImeAction = {
//                    focusManager.clearFocus()
//                    onResetClick()
//                },
//            )

            PasswordField(
                labelRes = R.string.feature_auth_api_confirm_new_password,
                placeholderRes = R.string.feature_auth_api_enter_new_password_again,
                error = state.confirmError,
                textFieldState = confirmPasswordTextFieldState,
                enabled = !(state.isLoading || state.isSuccess),
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onResetClick,
                enabled = state.canSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    disabledContainerColor = Color.White.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                if (state.isLoading) {
                    LoadingWheel(
                        contentDesc = stringResource(R.string.feature_auth_api_resetting_password),
                        modifier = Modifier.size(36.dp),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.feature_auth_api_reset_password),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun ResetPasswordEmptyPreview() {
    KithTheme {
        ResetPasswordScreen(state = ResetPasswordUiState())
    }
}

@Preview(name = "Filled", showBackground = true)
@Composable
private fun ResetPasswordFilledPreview() {
    KithTheme {
        ResetPasswordScreen(
            state = ResetPasswordUiState(
                password = "Kith@2026",
                confirmPassword = "Kith@2026",
            )
        )
    }
}

@Preview(name = "Mismatch error", showBackground = true)
@Composable
private fun ResetPasswordErrorPreview() {
    KithTheme {
        ResetPasswordScreen(
            state = ResetPasswordUiState(
                password = "kith2026",
                confirmPassword = "kith20",
                passwordError = "Add at least one uppercase letter",
                confirmError = "Passwords don't match",
            )
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun ResetPasswordLoadingPreview() {
    KithTheme {
        ResetPasswordScreen(
            state = ResetPasswordUiState(
                password = "Kith@2026",
                confirmPassword = "Kith@2026",
                isLoading = true,
            )
        )
    }
}