package com.example.sipinjam2.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam.R

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
) {
    val sipinjamBlue   = colorResource(R.color.sipinjam_blue)
    val backgroundGray = colorResource(R.color.background_gray)
    val cardWhite      = colorResource(R.color.card_white)
    val inputBg        = colorResource(R.color.input_bg)
    val textPrimary    = colorResource(R.color.text_primary)
    val textSecondary  = colorResource(R.color.text_secondary)
    val dividerColor   = colorResource(R.color.divider_color)
    val toggleBg       = colorResource(R.color.toggle_bg)

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedLang    by remember { mutableStateOf("ID") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            Text(
                text = "SiPinjam",
                color = sipinjamBlue,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Selamat Datang",
                color = textPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Sistem informasi peminjaman barang\ninventaris kampus ULM.",
                color = textSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "EMAIL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textSecondary,
                            letterSpacing = 0.8.sp
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "nama@mhs.ulm.ac.id",
                                    color = textSecondary.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor    = Color.Transparent,
                                focusedBorderColor      = sipinjamBlue,
                                unfocusedContainerColor = inputBg,
                                focusedContainerColor   = inputBg,
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textSecondary,
                            letterSpacing = 0.8.sp
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Filled.Visibility
                                        else
                                            Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible)
                                            "Sembunyikan password"
                                        else
                                            "Tampilkan password",
                                        tint = textSecondary
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor    = Color.Transparent,
                                focusedBorderColor      = sipinjamBlue,
                                unfocusedContainerColor = inputBg,
                                focusedContainerColor   = inputBg,
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Lupa Password?",
                            color = sipinjamBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onForgotPasswordClick() }
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = { onLoginClick(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = sipinjamBlue)
                    ) {
                        Text(
                            text = "Masuk",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cardWhite
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = dividerColor)
                        Text(
                            text = "ATAU",
                            color = textSecondary,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = dividerColor)
                    }

                    OutlinedButton(
                        onClick = { onRegisterClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = sipinjamBlue
                        )
                    ) {
                        Text(
                            text = "Daftar Akun",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            LanguageToggle(
                selected = selectedLang,
                onSelect = { selectedLang = it },
                toggleBgColor      = toggleBg,
                textPrimaryColor   = textPrimary,
                textSecondaryColor = textSecondary
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LanguageToggle(
    selected: String,
    onSelect: (String) -> Unit,
    toggleBgColor: Color,
    textPrimaryColor: Color,
    textSecondaryColor: Color,
) {
    val cardWhite = colorResource(R.color.card_white)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(toggleBgColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf("ID", "EN").forEach { lang ->
            val isSelected = lang == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) cardWhite else Color.Transparent)
                    .clickable { onSelect(lang) }
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lang,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) textPrimaryColor else textSecondaryColor
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}