package com.kith.feature.auth.impl

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


object KithColors {
    val BlueBackground = Color(0xFF5589F8)
    val TextDark = Color(0xFF1E1E1E)
    val TextLight = Color(0xFFFFFFFF)
    val BorderLight = Color(0xFFE2ECFF)
    val Placeholder = Color(0xFFA1C1FF)
    val ButtonLight = Color(0xFFE2ECFF)
    val Divider = Color(0xFF88B0FF)
}


data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val passwordError: String? = null,
    val emailError: String? = null,
    val errorMessage: String = ""
)

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignInScreen(
        modifier = modifier,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInClicked = viewModel ::onSignInClicked,
        onCreateAccountClicked = viewModel::onCreateAccountClicked,
        onAppleSignInClicked = viewModel :: onAppleSignInClicked,
        onGoogleSignInClicked = viewModel :: onGoogleSignInClicked
    )
}

@Composable
internal fun SignInScreen(
    modifier: Modifier = Modifier,
    uiState: AuthUiState = AuthUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSignInClicked: () -> Unit = {},
    onForgotPasswordClicked: () -> Unit = {},
    onCreateAccountClicked: () -> Unit = {},
    onAppleSignInClicked: () -> Unit = {},
    onGoogleSignInClicked: () -> Unit = {}
)
{

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KithColors.BlueBackground)
    ) {

        // Exact header matching original wave shape
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


        // Main dynamic Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Sign In.",
                color = KithColors.TextLight,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // Dynamic Email Field
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
                modifier = Modifier.height(14.dp)
            )


            // Dynamic Password Field with Search Icon matching original image
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,

                placeholder = {
                    Text(
                        "*****",
                        color = KithColors.Placeholder,
                        fontSize = 14.sp
                    )
                },

                label = {
                    Text(
                        "Password",
                        color = KithColors.TextLight,
                        fontSize = 12.sp
                    )
                },

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Password",
                        tint = KithColors.TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                },

                singleLine = true,

                visualTransformation = PasswordVisualTransformation(),

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


            // Forgot Password Trigger
            TextButton(
                onClick = onForgotPasswordClicked,
                modifier = Modifier.align(Alignment.End)
            ) {

                Text(
                    text = "Forgot Password?",
                    color = KithColors.TextLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // Dynamic Sign In Button
            Button(
                onClick = onSignInClicked,

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
                        "Signing In..."
                    else
                        "Sign In",

                    color = Color(0xFF3363CC),

                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // Create Account Link
            TextButton(
                onClick = onCreateAccountClicked
            ) {

                val annotatedString = buildAnnotatedString {

                    append("New here? ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = KithColors.TextLight
                        )
                    ) {
                        append("Create Account")
                    }
                }

                Text(
                    text = annotatedString,
                    color = KithColors.TextLight,
                    fontSize = 13.sp
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // OR Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = KithColors.Divider
                )

                Text(
                    text = "OR",
                    color = KithColors.TextLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = KithColors.Divider
                )
            }


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // Apple Sign In Button
            OutlinedButton(
                onClick = onAppleSignInClicked,

                shape = RoundedCornerShape(24.dp),

                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    KithColors.BorderLight
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {

                Text(
                    text = "Apple",
                    color = KithColors.TextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // Google Sign In Button
            OutlinedButton(
                onClick = onGoogleSignInClicked,

                shape = RoundedCornerShape(24.dp),

                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    KithColors.BorderLight
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {

                Text(
                    text = "Google",
                    color = KithColors.TextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun SignInScreenPreview() {

    var state by remember {
        mutableStateOf(AuthUiState())
    }

    SignInScreen(
        uiState = state,

        onEmailChange = {
            state = state.copy(
                email = it
            )
        },

        onPasswordChange = {
            state = state.copy(
                password = it
            )
        }
    )
}
