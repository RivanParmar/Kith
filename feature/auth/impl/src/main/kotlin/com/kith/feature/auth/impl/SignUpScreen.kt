package com.kith.feature.auth.impl

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


object KithSignUpColors {
    val BlueBackground = Color(0xFF5589F8)
    val TextDark = Color(0xFF1E1E1E)
    val TextLight = Color(0xFFFFFFFF)
    val BorderLight = Color(0xFFE2ECFF)
    val Placeholder = Color(0xFFA1C1FF)
    val ButtonLight = Color(0xFFE2ECFF)
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignUpScreen(
        modifier = modifier,
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUpClicked = viewModel::onSignUpClicked,
    )
}

@Composable
internal fun SignUpScreen(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState = SignUpUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSignUpClicked: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KithSignUpColors.BlueBackground)
    ) {

        // ============================================================
        // HEADER
        // EXACT SAME HEADER AS SIGN IN SCREEN
        // ============================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(android.R.drawable.ic_menu_camera),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

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
                color = KithSignUpColors.TextDark,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        // ============================================================
        // SIGN UP FORM
        // ============================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ========================================================
            // SIGN UP TITLE
            // ========================================================

            Text(
                text = "Sign Up.",
                color = KithSignUpColors.TextLight,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ========================================================
            // EMAIL FIELD
            // SAME AS SIGN IN SCREEN
            // ========================================================

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,

                placeholder = {
                    Text(
                        "email@id.com",
                        color = KithSignUpColors.Placeholder,
                        fontSize = 14.sp
                    )
                },

                label = {
                    Text(
                        "Email",
                        color = KithSignUpColors.TextLight,
                        fontSize = 12.sp
                    )
                },

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email",
                        tint = KithSignUpColors.TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                },

                singleLine = true,

                shape = RoundedCornerShape(12.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KithSignUpColors.BorderLight,
                    unfocusedBorderColor = KithSignUpColors.BorderLight,
                    focusedLabelColor = KithSignUpColors.TextLight,
                    unfocusedLabelColor = KithSignUpColors.BorderLight,
                    cursorColor = KithSignUpColors.TextLight,
                    focusedTextColor = KithSignUpColors.TextLight,
                    unfocusedTextColor = KithSignUpColors.TextLight
                ),

                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // ========================================================
            // PASSWORD FIELD
            // SAME AS SIGN IN SCREEN
            // ========================================================

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,

                placeholder = {
                    Text(
                        "*****",
                        color = KithSignUpColors.Placeholder,
                        fontSize = 14.sp
                    )
                },

                label = {
                    Text(
                        "Password",
                        color = KithSignUpColors.TextLight,
                        fontSize = 12.sp
                    )
                },

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Password",
                        tint = KithSignUpColors.TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                },

                singleLine = true,

                visualTransformation = PasswordVisualTransformation(),

                shape = RoundedCornerShape(12.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KithSignUpColors.BorderLight,
                    unfocusedBorderColor = KithSignUpColors.BorderLight,
                    focusedLabelColor = KithSignUpColors.TextLight,
                    unfocusedLabelColor = KithSignUpColors.BorderLight,
                    cursorColor = KithSignUpColors.TextLight,
                    focusedTextColor = KithSignUpColors.TextLight,
                    unfocusedTextColor = KithSignUpColors.TextLight
                ),

                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ========================================================
            // CONFIRM PASSWORD FIELD
            // ========================================================

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,

                placeholder = {
                    Text(
                        "*****",
                        color = KithSignUpColors.Placeholder,
                        fontSize = 14.sp
                    )
                },

                label = {
                    Text(
                        "Confirm Password",
                        color = KithSignUpColors.TextLight,
                        fontSize = 12.sp
                    )
                },

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Confirm Password",
                        tint = KithSignUpColors.TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                },

                singleLine = true,

                visualTransformation = PasswordVisualTransformation(),

                shape = RoundedCornerShape(12.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KithSignUpColors.BorderLight,
                    unfocusedBorderColor = KithSignUpColors.BorderLight,
                    focusedLabelColor = KithSignUpColors.TextLight,
                    unfocusedLabelColor = KithSignUpColors.BorderLight,
                    cursorColor = KithSignUpColors.TextLight,
                    focusedTextColor = KithSignUpColors.TextLight,
                    unfocusedTextColor = KithSignUpColors.TextLight
                ),

                modifier = Modifier.fillMaxWidth()
            )


            Spacer(
                modifier = Modifier.height(50.dp)
            )


            // ========================================================
            // SIGN UP BUTTON
            // ========================================================

            Button(
                onClick = onSignUpClicked,

                shape = RoundedCornerShape(24.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = KithSignUpColors.ButtonLight
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {

                Text(
                    text = if (uiState.isLoading)
                        "Signing Up..."
                    else
                        "Sign Up",

                    color = Color(0xFF3363CC),

                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


// ================================================================
// PREVIEW
// ================================================================

@Preview(showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {

    var state by remember {
        mutableStateOf(SignUpUiState())
    }

    SignUpScreen(
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
        },

        onConfirmPasswordChange = {
            state = state.copy(
                confirmPassword = it
            )
        }
    )
}