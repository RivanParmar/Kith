package com.kith.feature.auth.impl

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
        onSubmitClicked = viewModel::onSubmitClicked
    )
}

@Composable
internal fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    uiState: ForgotPasswordUiState = ForgotPasswordUiState(),
    onEmailChange: (String) -> Unit = {},
    onSubmitClicked: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KithColors.BlueBackground)
    ) {

        // Exact same wave header as SignInScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                val path = Path().apply {

                    moveTo(0f, 0f)

                    lineTo(
                        size.width,
                        0f
                    )

                    lineTo(
                        size.width,
                        size.height * 0.92f
                    )

                    cubicTo(
                        size.width * 0.75f,
                        size.height * 1.1f,

                        size.width * 0.35f,
                        size.height * 0.65f,

                        0f,
                        size.height * 0.82f
                    )

                    close()
                }

                drawPath(
                    path = path,
                    color = Color.White
                )
            }

            Text(
                text = "KITH",
                color = KithColors.TextDark,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        // Main Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Forgot Password",
                color = KithColors.TextLight,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // Email Field -- same styling as SignInScreen's email field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,

                placeholder = {
                    Text(
                        "email@id.com",
                        color = KithColors.Placeholder,
                        fontSize = 14.sp
                    )
                },

                label = {
                    Text(
                        "Email",
                        color = KithColors.TextLight,
                        fontSize = 12.sp
                    )
                },

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email",
                        tint = KithColors.TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                },

                singleLine = true,

                shape = RoundedCornerShape(12.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KithColors.BorderLight,
                    unfocusedBorderColor = KithColors.BorderLight,
                    focusedLabelColor = KithColors.TextLight,
                    unfocusedLabelColor = KithColors.BorderLight,
                    cursorColor = KithColors.TextLight,
                    focusedTextColor = KithColors.TextLight,
                    unfocusedTextColor = KithColors.TextLight
                ),

                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // Submit Button -- same styling as SignInScreen's Sign In button
            Button(
                onClick = onSubmitClicked,

                shape = RoundedCornerShape(24.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = KithColors.ButtonLight
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {

                Text(
                    text = if (uiState.isLoading)
                        "Submitting..."
                    else
                        "Submit",

                    color = Color(0xFF3363CC),

                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {

    var state by remember {
        mutableStateOf(ForgotPasswordUiState())
    }

    ForgotPasswordScreen(
        uiState = state,

        onEmailChange = {
            state = state.copy(
                email = it
            )
        }
    )
}