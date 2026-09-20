package com.kith.feature.auth.impl.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.GugiFontFamily
import com.kith.core.designsystem.theme.LightBlue
import com.kith.feature.auth.api.R

@Composable
internal fun WaveHeader(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White),
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
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        enabled = enabled,
        placeholder = {
            Text(
                text = stringResource(R.string.feature_auth_api_email_placeholder),
                fontSize = 14.sp,
            )
        },
        label = {
            Text(
                text = stringResource(R.string.feature_auth_api_email),
                color = Color.White,
                fontSize = 12.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = KithIcons.Mail,
                contentDescription = stringResource(R.string.feature_auth_api_email),
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
//        colors = OutlinedTextFieldDefaults.colors(
//            focusedBorderColor = KithColors.BorderLight,
//            unfocusedBorderColor = KithColors.BorderLight,
//            focusedLabelColor = KithColors.TextLight,
//            unfocusedLabelColor = KithColors.BorderLight,
//            cursorColor = KithColors.TextLight,
//            focusedTextColor = KithColors.TextLight,
//            unfocusedTextColor = KithColors.TextLight
//        ),
        modifier = modifier.fillMaxWidth(),
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
) {
    val passwordVisible = remember { mutableStateOf(false) }

    OutlinedSecureTextField(
        state = textFieldState,
        enabled = enabled,
        label = { Text(stringResource(labelRes)) },
        placeholder = { Text(stringResource(placeholderRes)) },
        isError = error != null,
        supportingText = error?.let { { Text(it, /*color = ErrorPink*/) } },
        leadingIcon = { Icon(KithIcons.Password, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = {
                passwordVisible.value = !passwordVisible.value
            }) {
                Icon(
                    imageVector =
                        if (error != null)
                            KithIcons.Error
                        else if (passwordVisible.value)
                            KithIcons.VisibilityOff
                        else
                            KithIcons.Visibility,
                    contentDescription =
                        if (error != null)
                            stringResource(R.string.feature_auth_api_error_password)
                        else if (passwordVisible.value)
                            stringResource(R.string.feature_auth_api_hide_password)
                        else
                            stringResource(R.string.feature_auth_api_show_password)
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
//        colors = OutlinedTextFieldDefaults.colors(
//            focusedTextColor = Color.White,
//            unfocusedTextColor = Color.White,
//            focusedBorderColor = Color.White,
//            unfocusedBorderColor = FieldOutline,
//            errorBorderColor = ErrorPink,
//            focusedLabelColor = Color.White,
//            unfocusedLabelColor = FieldOutline,
//            errorLabelColor = ErrorPink,
//            focusedPlaceholderColor = FieldOutline,
//            unfocusedPlaceholderColor = FieldOutline,
//            focusedLeadingIconColor = Color.White,
//            unfocusedLeadingIconColor = FieldOutline,
//            focusedTrailingIconColor = Color.White,
//            unfocusedTrailingIconColor = FieldOutline,
//            cursorColor = Color.White,
//            focusedContainerColor = Color.Transparent,
//            unfocusedContainerColor = Color.Transparent,
//            errorContainerColor = Color.Transparent
//        ),
        modifier = modifier.fillMaxWidth(),
    )
}