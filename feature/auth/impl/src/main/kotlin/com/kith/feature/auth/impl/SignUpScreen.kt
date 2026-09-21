package com.kith.feature.auth.impl

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.theme.KithTheme
import com.kith.feature.auth.api.R
import com.kith.feature.auth.impl.ui.AuthLayout
import com.kith.feature.auth.impl.ui.EmailField
import com.kith.feature.auth.impl.ui.PasswordField

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignInClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreen(
        modifier = modifier,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUpClicked = viewModel::onSignUpClicked,
        onSignInClicked = onSignInClicked,
    )
}

@Composable
internal fun SignUpScreen(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState = SignUpUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSignUpClicked: () -> Unit = {},
    onSignInClicked: () -> Unit = {},
) {
    val passwordTextFieldState = rememberTextFieldState()
    val confirmPasswordTextFieldState = rememberTextFieldState()
    var submitAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(passwordTextFieldState) {
        snapshotFlow { passwordTextFieldState.text }
            .collect { onPasswordChange(it.toString()) }
    }

    LaunchedEffect(confirmPasswordTextFieldState) {
        snapshotFlow { confirmPasswordTextFieldState.text }
            .collect { onConfirmPasswordChange(it.toString()) }
    }

    val confirmPasswordMismatchError = if (
        submitAttempted &&
        confirmPasswordTextFieldState.text.isNotBlank() &&
        confirmPasswordTextFieldState.text.toString() != passwordTextFieldState.text.toString()
    ) {
        "Passwords do not match"
    } else {
        null
    }

    AuthLayout(modifier = modifier) {
        Text(
            text = "Sign Up.",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        EmailField(
            email = uiState.email,
            onEmailChange = onEmailChange,
            attemptedSubmit = submitAttempted,
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordField(
            labelRes = R.string.feature_auth_api_password,
            placeholderRes = R.string.feature_auth_api_enter_password,
            error = uiState.passwordError,
            textFieldState = passwordTextFieldState,
            attemptedSubmit = submitAttempted,
        )

        Spacer(modifier = Modifier.height(20.dp))

//        OutlinedTextField(
//            value = uiState.confirmPassword,
//            onValueChange = onConfirmPasswordChange,
//            placeholder = {
//                Text(
//                    "*****",
//                    color = KithSignUpColors.Placeholder,
//                    fontSize = 14.sp
//                )
//            },
//            label = {
//                Text(
//                    "Confirm Password",
//                    color = KithSignUpColors.TextLight,
//                    fontSize = 12.sp
//                )
//            },
//            leadingIcon = {
//                Icon(
//                    imageVector = KithIcons.Search,
//                    contentDescription = "Confirm Password",
//                    tint = KithSignUpColors.TextLight,
//                    modifier = Modifier.size(20.dp)
//                )
//            },
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation(),
//            shape = RoundedCornerShape(12.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = KithSignUpColors.BorderLight,
//                unfocusedBorderColor = KithSignUpColors.BorderLight,
//                focusedLabelColor = KithSignUpColors.TextLight,
//                unfocusedLabelColor = KithSignUpColors.BorderLight,
//                cursorColor = KithSignUpColors.TextLight,
//                focusedTextColor = KithSignUpColors.TextLight,
//                unfocusedTextColor = KithSignUpColors.TextLight
//            ),
//            modifier = Modifier.fillMaxWidth()
//        )

        PasswordField(
            labelRes = R.string.feature_auth_api_confirm_password,
            placeholderRes = R.string.feature_auth_api_enter_password_again,
            error = uiState.confirmPasswordError ?: confirmPasswordMismatchError,
            textFieldState = confirmPasswordTextFieldState,
            attemptedSubmit = submitAttempted,
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                submitAttempted = true
                onSignUpClicked()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text(
                text = if (uiState.isLoading)
                    "Signing Up..."
                else
                    "Sign Up",
                color = Color(0xFF3363CC),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onSignInClicked,
        ) {
            val annotatedString = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                ) {
                    append("Sign in")
                }
            }

            Text(
                text = annotatedString,
                color = Color.White,
                fontSize = 13.sp,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    var state by remember {
        mutableStateOf(SignUpUiState())
    }

    KithTheme {
        SignUpScreen(
            uiState = state,
            onEmailChange = {
                state = state.copy(email = it)
            },
            onPasswordChange = {
                state = state.copy(password = it)
            },
            onConfirmPasswordChange = {
                state = state.copy(confirmPassword = it)
            }
        )
    }
}