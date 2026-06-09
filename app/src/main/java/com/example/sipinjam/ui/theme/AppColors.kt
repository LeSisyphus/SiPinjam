package com.example.sipinjam.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized app color tokens.
 *
 * Keep the original SiPinjam visual identity in light mode while providing
 * equivalent dark-mode tokens. Screens should consume these tokens through
 * [LocalAppColors] or through the compatibility aliases in Color.kt.
 */
data class AppColors(
    val primary: Color,
    val background: Color,
    val card: Color,
    val inputBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val toggleBackground: Color,
    val imageBackground: Color,
    val infoOrangeBackground: Color,
    val statusGreen: Color,
    val statusGreenBackground: Color,
    val statusOrange: Color,
    val statusOrangeBackground: Color,
    val statusRed: Color,
    val statusRedBackground: Color,
    val statusBlue: Color,
    val statusBlueBackground: Color,
    val onPrimary: Color = Color.White,
)

val LightAppColors = AppColors(
    primary = Color(0xFF2196F3),
    background = Color(0xFFF0F2F5),
    card = Color(0xFFFFFFFF),
    inputBackground = Color(0xFFF3F4F6),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF6B7280),
    divider = Color(0xFFE5E7EB),
    toggleBackground = Color(0xFFE2E8F0),
    imageBackground = Color(0xFF1A1A2E),
    infoOrangeBackground = Color(0xFFFFF7ED),
    statusGreen = Color(0xFF16A34A),
    statusGreenBackground = Color(0xFFDCFCE7),
    statusOrange = Color(0xFFD97706),
    statusOrangeBackground = Color(0xFFFEF3C7),
    statusRed = Color(0xFFDC2626),
    statusRedBackground = Color(0xFFFEE2E2),
    statusBlue = Color(0xFF1E88E5),
    statusBlueBackground = Color(0xFFE3F2FD),
)

val DarkAppColors = AppColors(
    primary = Color(0xFF64B5F6),
    background = Color(0xFF0F172A),
    card = Color(0xFF1E293B),
    inputBackground = Color(0xFF263449),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFCBD5E1),
    divider = Color(0xFF334155),
    toggleBackground = Color(0xFF334155),
    imageBackground = Color(0xFF111827),
    infoOrangeBackground = Color(0xFF3A2A17),
    statusGreen = Color(0xFF86EFAC),
    statusGreenBackground = Color(0xFF14532D),
    statusOrange = Color(0xFFFBBF24),
    statusOrangeBackground = Color(0xFF451A03),
    statusRed = Color(0xFFFCA5A5),
    statusRedBackground = Color(0xFF7F1D1D),
    statusBlue = Color(0xFF93C5FD),
    statusBlueBackground = Color(0xFF1E3A8A),
)
