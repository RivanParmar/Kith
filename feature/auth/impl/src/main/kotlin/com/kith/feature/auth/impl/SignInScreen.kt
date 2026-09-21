package com.kith.feature.auth.impl

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    onCreateAccountClicked: () -> Unit = {},
    onForgotPasswordClicked: () -> Unit = {},
    onSignInComplete: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignInScreen(
        modifier = modifier,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInClicked = viewModel::onSignInClicked,
        onForgotPasswordClicked = onForgotPasswordClicked,
        onCreateAccountClicked = onCreateAccountClicked,
        onAppleSignInClicked = viewModel::onAppleSignInClicked,
        onGoogleSignInClicked = viewModel::onGoogleSignInClicked,
        onSignInComplete = onSignInComplete,
    )
}

@Composable
internal fun SignInScreen(
    modifier: Modifier = Modifier,
    uiState: SignInUiState = SignInUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSignInClicked: () -> Unit = {},
    onForgotPasswordClicked: () -> Unit = {},
    onCreateAccountClicked: () -> Unit = {},
    onAppleSignInClicked: () -> Unit = {},
    onGoogleSignInClicked: () -> Unit = {},
    onSignInComplete: () -> Unit = {},
) {
    val passwordTextFieldState = rememberTextFieldState()
    var submitAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(passwordTextFieldState) {
        snapshotFlow { passwordTextFieldState.text }
            .collect { onPasswordChange(it.toString()) }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignInComplete()
        }
    }

    AuthLayout(modifier = modifier) {
        Text(
            text = stringResource(R.string.feature_auth_api_sign_in_title),
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
            error = null,
            textFieldState = passwordTextFieldState,
            attemptedSubmit = submitAttempted,
        )

        TextButton(
            onClick = onForgotPasswordClicked,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(
                text = "Forgot Password?",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                submitAttempted = true
                onSignInClicked()
            },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = if (uiState.isLoading)
                    "Signing In..."
                else
                    "Sign In",
                color = Color(0xFF3363CC),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
            onClick = onCreateAccountClicked,
        ) {
            val annotatedString = buildAnnotatedString {
                append("New here? ")
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                ) {
                    append("Create Account")
                }
            }

            Text(
                text = annotatedString,
                color = Color.White,
                fontSize = 13.sp,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF88B0FF),
            )

            Text(
                text = "OR",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp),
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF88B0FF),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onAppleSignInClicked,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text(
                text = "Apple",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onGoogleSignInClicked,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text(
                text = "Google",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    var state by remember {
        mutableStateOf(SignInUiState())
    }

    KithTheme {
        SignInScreen(
            uiState = state,
            onEmailChange = {
                state = state.copy(email = it)
            },
            onPasswordChange = {
                state = state.copy(password = it)
            }
        )
    }
}