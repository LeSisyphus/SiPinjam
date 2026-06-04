package com.example.sipinjam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sipinjam.R
import com.example.sipinjam.data.model.BorrowingStatus
import com.example.sipinjam.data.model.ReturnStatus
import com.example.sipinjam.ui.theme.InputBg
import com.example.sipinjam.ui.theme.StatusBlue
import com.example.sipinjam.ui.theme.StatusBlueBg
import com.example.sipinjam.ui.theme.StatusGreen
import com.example.sipinjam.ui.theme.StatusGreenBg
import com.example.sipinjam.ui.theme.StatusOrange
import com.example.sipinjam.ui.theme.StatusOrangeBg
import com.example.sipinjam.ui.theme.StatusRed
import com.example.sipinjam.ui.theme.StatusRedLightBg
import com.example.sipinjam.ui.theme.TextSecondary

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier,
) {
    val (bgColor, textColor) = when {
        status.equals(BorrowingStatus.DIPROSES, ignoreCase = true) -> StatusOrangeBg to StatusOrange
        status.equals(BorrowingStatus.DIPINJAM, ignoreCase = true) -> StatusOrangeBg to StatusOrange
        status.equals(BorrowingStatus.DISETUJUI_LEGACY, ignoreCase = true) -> StatusGreenBg to StatusGreen
        status.equals(BorrowingStatus.MENUNGGU_VERIFIKASI, ignoreCase = true) -> StatusBlueBg to StatusBlue
        status.equals(BorrowingStatus.DITOLAK, ignoreCase = true) -> StatusRedLightBg to StatusRed
        status.equals(BorrowingStatus.SELESAI, ignoreCase = true) -> StatusGreenBg to StatusGreen
        status.equals(ReturnStatus.TERVERIFIKASI, ignoreCase = true) -> StatusGreenBg to StatusGreen
        else -> InputBg to TextSecondary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = localizedStatus(status),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun localizedStatus(status: String): String = when {
    status.equals(BorrowingStatus.DIPROSES, ignoreCase = true) -> stringResource(R.string.status_diproses)
    status.equals(BorrowingStatus.DIPINJAM, ignoreCase = true) -> stringResource(R.string.status_dipinjam)
    status.equals(BorrowingStatus.DISETUJUI_LEGACY, ignoreCase = true) -> stringResource(R.string.status_disetujui_legacy)
    status.equals(BorrowingStatus.MENUNGGU_VERIFIKASI, ignoreCase = true) -> stringResource(R.string.status_menunggu_verifikasi)
    status.equals(BorrowingStatus.DITOLAK, ignoreCase = true) -> stringResource(R.string.status_ditolak)
    status.equals(BorrowingStatus.SELESAI, ignoreCase = true) -> stringResource(R.string.status_selesai)
    status.equals(ReturnStatus.TERVERIFIKASI, ignoreCase = true) -> stringResource(R.string.status_terverifikasi)
    else -> status
}
