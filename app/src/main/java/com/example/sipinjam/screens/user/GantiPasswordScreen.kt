package com.example.sipinjam.screens.user

import com.example.sipinjam.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sipinjam.screens.user.GantiPasswordViewModel
import com.example.sipinjam.ui.theme.*

@Composable
fun GantiPasswordScreen(
    viewModel: GantiPasswordViewModel = viewModel(),
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Dialog sukses
    if (uiState.isSuccess) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(StatusGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.screen_password_changed_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.screen_password_changed_subtitle),
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onDismissDialog()
                        onBackClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue)
                ) {
                    Text(
                        text = stringResource(R.string.btn_oke),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            Surface(
                color = CardWhite,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = SiPinjamBlue
                        )
                    }
                    Text(
                        text = stringResource(R.string.screen_profile_change_password),
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Banner info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(InputBg)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SiPinjamBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = SiPinjamBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.screen_password_change_instruction),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.label_security_protocol),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp
                    )
                }
            }

            // Password Saat Ini
            PasswordField(
                label = "Password Saat Ini",
                value = uiState.passwordLama,
                visible = uiState.passwordLamaVisible,
                placeholder = "••••••••",
                onValueChange = { viewModel.onPasswordLamaChange(it) },
                onToggleVisibility = { viewModel.onTogglePasswordLama() }
            )

            // Password Baru
            PasswordField(
                label = "Password Baru",
                value = uiState.passwordBaru,
                visible = uiState.passwordBaruVisible,
                placeholder = "Minimal 8 karakter",
                onValueChange = { viewModel.onPasswordBaruChange(it) },
                onToggleVisibility = { viewModel.onTogglePasswordBaru() }
            )

            // Konfirmasi Password Baru
            PasswordField(
                label = "Konfirmasi Password Baru",
                value = uiState.konfirmasiPassword,
                visible = uiState.konfirmasiVisible,
                placeholder = "Ulangi password baru",
                onValueChange = { viewModel.onKonfirmasiChange(it) },
                onToggleVisibility = { viewModel.onToggleKonfirmasi() }
            )

            // Error
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = StatusRed,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(4.dp))

            // Tombol Simpan
            Button(
                onClick = { viewModel.onSimpanClick(onSuccess = {}) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SiPinjamBlue),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.btn_simpan_perubahan),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    visible: Boolean,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = SiPinjamBlue,
                unfocusedContainerColor = InputBg,
                focusedContainerColor = InputBg,
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun GantiPasswordScreenPreview() {
    MaterialTheme { GantiPasswordScreen() }
}