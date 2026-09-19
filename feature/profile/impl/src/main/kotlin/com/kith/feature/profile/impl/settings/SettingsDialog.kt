package com.kith.feature.profile.impl.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(), // NOTIFICATION SETTING
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle() // NOTIFICATION SETTING (lifecycle-safe)
    
    // NOTIFICATION SETTING: Handle Android 13+ Notification Permission
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            viewModel.setPushNotificationsEnabled(true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Settings",
                fontSize = 32.sp
            )
        },
        text = {
            Column(
                // ADDED: verticalScroll so the options don't get cut off when the list is too long
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                
                // NOTIFICATION SETTING
                SettingsSectionTitle(
                    text = "Notifications"
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Notifications",
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        fontSize = 20.sp
                    )
                    Switch(
                        checked = uiState.notificationsEnabled && hasNotificationPermission,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val isGranted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (!isGranted) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        hasNotificationPermission = true
                                        viewModel.setPushNotificationsEnabled(true)
                                    }
                                } else {
                                    viewModel.setPushNotificationsEnabled(true)
                                }
                            } else {
                                viewModel.setPushNotificationsEnabled(false)
                            }
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

               
                // DYNAMIC COLOR
                SettingsSectionTitle(
                    text = "Use Dynamic Color"
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsRadioOption(
                    text = "Yes",
                    selected = uiState.dynamicColor ==
                            DynamicColorOption.YES,
                    onClick = {
                        viewModel.setDynamicColor(
                            DynamicColorOption.YES
                        )
                    }
                )
                SettingsRadioOption(
                    text = "No",
                    selected = uiState.dynamicColor ==
                            DynamicColorOption.NO,
                    onClick = {
                        viewModel.setDynamicColor(
                            DynamicColorOption.NO
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // DARK MODE
                SettingsSectionTitle(
                    text = "Dark mode preference"
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsRadioOption(
                    text = "System default",
                    selected = uiState.darkMode ==
                            DarkModePreference.SYSTEM_DEFAULT,
                    onClick = {
                        viewModel.setDarkMode(
                            DarkModePreference.SYSTEM_DEFAULT
                        )
                    }
                )
                SettingsRadioOption(
                    text = "Light",
                    selected = uiState.darkMode ==
                            DarkModePreference.LIGHT,
                    onClick = {
                        viewModel.setDarkMode(
                            DarkModePreference.LIGHT
                        )
                    }
                )
                SettingsRadioOption(
                    text = "Dark",
                    selected = uiState.darkMode ==
                            DarkModePreference.DARK,
                    onClick = {
                        viewModel.setDarkMode(
                            DarkModePreference.DARK
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}



@Composable
private fun SettingsSectionTitle(
    text: String
) {
    Text(
        text = text,
        fontSize = 26.sp
    )
}

@Composable
private fun SettingsRadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 12.dp),
            fontSize = 20.sp
        )
    }
}