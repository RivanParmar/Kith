package com.kith.feature.auth.impl

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.theme.KithTheme
import com.kith.feature.auth.api.R
import com.kith.feature.auth.impl.ui.AuthLayout
import com.kith.feature.auth.impl.ui.EmailField

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        modifier = modifier,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onSubmitClicked = viewModel::onSubmitClicked,
    )
}

@Composable
internal fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit = {},
    onSubmitClicked: () -> Unit = {},
) {
    AuthLayout(modifier = modifier) {
        Text(
            text = stringResource(R.string.feature_auth_api_forgot_password_title),
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
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onSubmitClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text(
                text = if (uiState.isLoading)
                    "Submitting..."
                else
                    "Submit",
                color = Color(0xFF3363CC),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    var state by remember {
        mutableStateOf(ForgotPasswordUiState())
    }

    KithTheme {
        ForgotPasswordScreen(
            uiState = state,
            onEmailChange = {
                state = state.copy(email = it)
            },
        )
    }
}