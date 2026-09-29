package com.kith.feature.auth.impl.ui

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.GugiFontFamily
import com.kith.core.designsystem.theme.LightBlue
import com.kith.feature.auth.api.R as apiR
import com.kith.feature.auth.impl.R

@Composable
internal fun WaveHeader(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
         Image(
             painter = painterResource(R.drawable.wave_backgroundimage),
             contentDescription = null,
             contentScale = ContentScale.Crop,
             modifier = Modifier.matchParentSize(),
         )

        Text(
            text = "KITH",
            color = Color.Black,
            fontSize = 54.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = GugiFontFamily,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-30).dp),
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .align(Alignment.BottomCenter),
        ) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, h * 0.5f)
                cubicTo(
                    w * 0.4f,
                    h * 0.2f,
                    w * 0.55f,
                    h * 1.2f,
                    w,
                    h * 0.8f,
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path, LightBlue)
        }
    }
}

@Composable
internal fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    attemptedSubmit: Boolean = false,
) {
    var touched by remember { mutableStateOf(false) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    val showErrors = touched || attemptedSubmit

    val errorMessage = when {
        !showErrors -> null
        email.isBlank() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
        else -> null
    }

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        enabled = enabled,
        isError = errorMessage != null,
        supportingText = {
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color(0xFFFF6B6B),
                    fontSize = 12.sp,
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(apiR.string.feature_auth_api_email_placeholder),
                fontSize = 14.sp,
            )
        },
        label = {
            Text(
                text = stringResource(apiR.string.feature_auth_api_email),
                fontSize = 12.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = KithIcons.Mail,
                contentDescription = stringResource(apiR.string.feature_auth_api_email),
                tint = if (errorMessage != null) Color(0xFFFF6B6B) else Color.White,
                modifier = Modifier.size(20.dp),
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            errorBorderColor = Color(0xFFFF6B6B),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            errorLabelColor = Color(0xFFFF6B6B),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
        ),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    hasBeenFocused = true
                } else if (hasBeenFocused) {
                    touched = true
                }
            },
    )
}

@Composable
internal fun PasswordField(
    @StringRes labelRes: Int,
    @StringRes placeholderRes: Int,
    error: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textFieldState: TextFieldState = rememberTextFieldState(),
    attemptedSubmit: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var touched by remember { mutableStateOf(false) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    val showErrors = touched || attemptedSubmit

    val passwordRegex = remember {
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
    }
    val text = textFieldState.text.toString()

    val validationError = when {
        text.isBlank() -> "Password is required"
        !passwordRegex.matches(text) -> "8+ characters with uppercase, lowercase, number & special character"
        else -> null
    }
    val displayError = if (showErrors) (error ?: validationError) else null

    OutlinedSecureTextField(
        state = textFieldState,
        enabled = enabled,
        label = { Text(stringResource(labelRes)) },
        placeholder = { Text(stringResource(placeholderRes)) },
        isError = displayError != null,
        supportingText = displayError?.let { { Text(it, color = Color(0xFFFF6B6B)) } },
        leadingIcon = { Icon(KithIcons.Password, contentDescription = null, tint = Color.White) },
        trailingIcon = {
            IconButton(onClick = {
                passwordVisible = !passwordVisible
            }) {
                Icon(
                    imageVector =
                        if (displayError != null)
                            KithIcons.Error
                        else if (passwordVisible)
                            KithIcons.VisibilityOff
                        else
                            KithIcons.Visibility,
                    contentDescription =
                        if (displayError != null)
                            stringResource(apiR.string.feature_auth_api_error_password)
                        else if (passwordVisible)
                            stringResource(apiR.string.feature_auth_api_hide_password)
                        else
                            stringResource(apiR.string.feature_auth_api_show_password),
                    tint = if (displayError != null) Color(0xFFFF6B6B) else Color.White,
                )
            }
        },
        textObfuscationMode = if (passwordVisible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.Hidden
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            errorBorderColor = Color(0xFFFF6B6B),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            errorLabelColor = Color(0xFFFF6B6B),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
        ),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    hasBeenFocused = true
                } else if (hasBeenFocused) {
                    touched = true
                }
            },
    )
}