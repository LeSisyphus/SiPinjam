package com.example.sipinjam.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.sipinjam.R
import com.example.sipinjam.domain.model.BorrowingStatus
import com.example.sipinjam.domain.model.ReturnStatus
import com.example.sipinjam.utils.UiMessageKey

@Composable
fun localizedStatusText(status: String): String {
    val normalized = status.trim()
    return when {
        normalized.equals(BorrowingStatus.DIPROSES, ignoreCase = true) -> stringResource(R.string.status_diproses)
        normalized.equals(BorrowingStatus.DIPINJAM, ignoreCase = true) -> stringResource(R.string.status_dipinjam)
        normalized.equals(BorrowingStatus.DISETUJUI_LEGACY, ignoreCase = true) -> stringResource(R.string.status_disetujui_legacy)
        normalized.equals(BorrowingStatus.MENUNGGU_VERIFIKASI, ignoreCase = true) -> stringResource(R.string.status_menunggu_verifikasi)
        normalized.equals(BorrowingStatus.SELESAI, ignoreCase = true) -> stringResource(R.string.status_selesai)
        normalized.equals(BorrowingStatus.DITOLAK, ignoreCase = true) -> stringResource(R.string.status_ditolak)
        normalized.equals(ReturnStatus.MENUNGGU_VERIFIKASI, ignoreCase = true) -> stringResource(R.string.status_menunggu_verifikasi)
        normalized.equals(ReturnStatus.TERVERIFIKASI, ignoreCase = true) -> stringResource(R.string.status_terverifikasi)
        normalized.equals(ReturnStatus.DITOLAK, ignoreCase = true) -> stringResource(R.string.status_ditolak)
        normalized.equals("Tersedia", ignoreCase = true) || normalized.equals("Available", ignoreCase = true) -> stringResource(R.string.status_tersedia)
        normalized.equals("Tidak Tersedia", ignoreCase = true) || normalized.equals("Unavailable", ignoreCase = true) -> stringResource(R.string.status_tidak_tersedia)
        else -> status
    }
}

@Composable
fun localizedCategoryText(category: String): String {
    val normalized = category.trim()
    return when {
        normalized.equals("Semua", ignoreCase = true) || normalized.equals("All", ignoreCase = true) -> stringResource(R.string.filter_all)
        normalized.equals("Elektronik", ignoreCase = true) || normalized.equals("Electronics", ignoreCase = true) -> stringResource(R.string.category_electronics)
        normalized.equals("Optik", ignoreCase = true) || normalized.equals("Optic", ignoreCase = true) || normalized.equals("Optics", ignoreCase = true) -> stringResource(R.string.category_optic)
        normalized.equals("Kabel", ignoreCase = true) || normalized.equals("Cable", ignoreCase = true) -> stringResource(R.string.category_cable)
        else -> category
    }
}

@Composable
fun localizedRoleText(role: String): String {
    val normalized = role.trim()
    return when {
        normalized.equals("MAHASISWA", ignoreCase = true) || normalized.equals("student", ignoreCase = true) -> stringResource(R.string.role_mahasiswa)
        normalized.equals("DOSEN", ignoreCase = true) || normalized.equals("lecturer", ignoreCase = true) -> stringResource(R.string.role_dosen)
        normalized.equals("STAF", ignoreCase = true) || normalized.equals("STAFF", ignoreCase = true) -> stringResource(R.string.role_staf)
        else -> role.uppercase()
    }
}

@Composable
fun localizedItemConditionText(condition: String): String {
    val normalized = condition.trim()
    return when {
        normalized.equals("Mulus", ignoreCase = true) || normalized.equals("Pristine", ignoreCase = true) -> stringResource(R.string.condition_pristine)
        normalized.equals("Baik", ignoreCase = true) || normalized.equals("Good", ignoreCase = true) -> stringResource(R.string.condition_good)
        normalized.equals("Rusak Ringan", ignoreCase = true) || normalized.equals("Minor Damage", ignoreCase = true) -> stringResource(R.string.condition_minor_damage)
        normalized.equals("Rusak Berat", ignoreCase = true) || normalized.equals("Severe Damage", ignoreCase = true) -> stringResource(R.string.condition_heavy_damage)
        else -> condition
    }
}

@Composable
fun localizedDayCount(value: Int): String = stringResource(R.string.unit_days_count, value)

@Composable
fun localizedUiMessage(message: String?): String {
    if (message.isNullOrBlank()) return ""

    val raw = message.trim()

    val keyedMaxBorrowPrefix = "${UiMessageKey.MAX_BORROW_DURATION}|"
    if (raw.startsWith(keyedMaxBorrowPrefix)) {
        val maxDays = raw.removePrefix(keyedMaxBorrowPrefix).toIntOrNull() ?: 0
        return stringResource(R.string.error_max_borrow_duration, maxDays)
    }

    val keyedUploadPhotoPrefix = "${UiMessageKey.UPLOAD_PHOTO_FAILED}|"
    if (raw.startsWith(keyedUploadPhotoPrefix)) {
        val detail = raw.removePrefix(keyedUploadPhotoPrefix).trim().ifBlank { "-" }
        return stringResource(R.string.error_upload_photo, detail)
    }

    val maxBorrowRegex = Regex("Durasi peminjaman maksimal\\s+(\\d+)\\s+hari", RegexOption.IGNORE_CASE)
    val uploadPhotoPrefix = "Gagal upload foto:"

    maxBorrowRegex.find(raw)?.let { match ->
        val maxDays = match.groupValues.getOrNull(1)?.toIntOrNull() ?: return@let
        return stringResource(R.string.error_max_borrow_duration, maxDays)
    }

    if (raw.startsWith(uploadPhotoPrefix, ignoreCase = true)) {
        val detail = raw.removePrefix(uploadPhotoPrefix).trim().ifBlank { "-" }
        return stringResource(R.string.error_upload_photo, detail)
    }

    return when (raw) {
        UiMessageKey.INVALID_ITEM_ID -> stringResource(R.string.error_invalid_item_id)
        UiMessageKey.INVALID_BORROWING_ID -> stringResource(R.string.error_invalid_borrowing_id)
        UiMessageKey.ITEM_NOT_FOUND -> stringResource(R.string.error_item_not_found)
        UiMessageKey.ITEM_NOT_FOUND_OR_FAILED -> stringResource(R.string.error_item_not_found_or_failed)
        UiMessageKey.BORROWING_NOT_FOUND -> stringResource(R.string.error_borrowing_not_found)
        UiMessageKey.RETURN_NOT_FOUND -> stringResource(R.string.error_return_not_found)
        UiMessageKey.RETURN_INVALID -> stringResource(R.string.error_return_invalid)
        UiMessageKey.RETURN_ALREADY_PROCESSED -> stringResource(R.string.error_return_already_processed)
        UiMessageKey.USER_NOT_FOUND_LOGIN_AGAIN -> stringResource(R.string.error_user_not_found_login_again)
        UiMessageKey.USER_NOT_FOUND -> stringResource(R.string.error_user_not_found)
        UiMessageKey.USER_NOT_LOGGED_IN -> stringResource(R.string.error_user_not_logged_in)
        UiMessageKey.UID_NOT_FOUND -> stringResource(R.string.error_uid_not_found)
        UiMessageKey.FIRESTORE_USER_NOT_FOUND -> stringResource(R.string.error_firestore_user_not_found)
        UiMessageKey.BORROW_DATE_REQUIRED -> stringResource(R.string.error_borrow_date_required)
        UiMessageKey.RETURN_DATE_REQUIRED -> stringResource(R.string.error_return_date_required)
        UiMessageKey.BORROW_REASON_REQUIRED -> stringResource(R.string.error_borrow_reason_required)
        UiMessageKey.DATE_FORMAT_INVALID -> stringResource(R.string.error_date_format_invalid)
        UiMessageKey.BORROW_DATE_PAST -> stringResource(R.string.error_borrow_date_past)
        UiMessageKey.RETURN_DATE_BEFORE_BORROW_DATE -> stringResource(R.string.error_return_date_before_borrow_date)
        UiMessageKey.ITEM_UNAVAILABLE_OR_OUT_OF_STOCK -> stringResource(R.string.error_item_unavailable_or_out_of_stock)
        UiMessageKey.ITEM_OUT_OF_STOCK -> stringResource(R.string.error_item_out_of_stock)
        UiMessageKey.SEND_BORROW_REQUEST_FAILED -> stringResource(R.string.error_send_borrow_request_failed)
        UiMessageKey.LOAD_CATALOG_FAILED -> stringResource(R.string.error_load_catalog_failed)
        UiMessageKey.LOAD_FAVORITES_FAILED -> stringResource(R.string.error_load_favorites_failed)
        UiMessageKey.LOAD_HISTORY_FAILED -> stringResource(R.string.error_load_history_failed)
        UiMessageKey.LOAD_ITEMS_FAILED -> stringResource(R.string.error_load_items_failed)
        UiMessageKey.LOAD_BORROWINGS_FAILED -> stringResource(R.string.error_load_borrowings_failed)
        UiMessageKey.LOAD_RETURNS_FAILED -> stringResource(R.string.error_load_returns_failed)
        UiMessageKey.LOAD_BORROWING_DETAIL_FAILED -> stringResource(R.string.error_load_borrowing_detail_failed)
        UiMessageKey.ADD_ITEM_FAILED -> stringResource(R.string.error_add_item_failed)
        UiMessageKey.EDIT_ITEM_FAILED -> stringResource(R.string.error_edit_item_failed)
        UiMessageKey.DELETE_ITEM_FAILED -> stringResource(R.string.error_delete_item_failed)
        UiMessageKey.SUBMIT_RETURN_FAILED -> stringResource(R.string.error_submit_return_failed)
        UiMessageKey.REJECT_RETURN_FAILED -> stringResource(R.string.error_reject_return_failed)
        UiMessageKey.RETURN_CONDITION_REQUIRED -> stringResource(R.string.error_return_condition_required)
        UiMessageKey.ADMIN_NOTE_REQUIRED_REJECT_RETURN -> stringResource(R.string.error_admin_note_required_reject_return)
        UiMessageKey.EMPTY_NAME -> stringResource(R.string.error_empty_name)
        UiMessageKey.EMPTY_EMAIL_PASSWORD -> stringResource(R.string.error_empty_email_password)
        UiMessageKey.EMPTY_FULL_NAME -> stringResource(R.string.error_empty_full_name)
        UiMessageKey.EMPTY_EMAIL -> stringResource(R.string.error_empty_email)
        UiMessageKey.PASSWORD_MIN_6 -> stringResource(R.string.error_password_min_6)
        UiMessageKey.CURRENT_PASSWORD_REQUIRED -> stringResource(R.string.error_current_password_required)
        UiMessageKey.NEW_PASSWORD_MIN_8 -> stringResource(R.string.error_new_password_min_8)
        UiMessageKey.PASSWORD_CONFIRMATION_MISMATCH -> stringResource(R.string.error_password_confirmation_mismatch)
        UiMessageKey.SAVE_PROFILE_FAILED -> stringResource(R.string.error_save_profile_failed)
        UiMessageKey.URL_NOT_FOUND -> stringResource(R.string.error_url_not_found)
        UiMessageKey.LOAD_HOLIDAY_INFO_FAILED -> stringResource(R.string.error_load_holiday_info_failed)
        UiMessageKey.LOAD_HOLIDAY_CACHE_FAILED -> stringResource(R.string.error_load_holiday_cache_failed)
        UiMessageKey.LOAD_ITEM_FAILED -> stringResource(R.string.error_load_item_failed)
        UiMessageKey.CHANGE_PASSWORD_FAILED -> stringResource(R.string.error_change_password_failed)
        UiMessageKey.VERIFY_RETURN_FAILED -> stringResource(R.string.error_verify_return_failed)
        UiMessageKey.APPROVE_BORROWING_FAILED -> stringResource(R.string.error_approve_borrowing_failed)
        UiMessageKey.REJECT_BORROWING_FAILED -> stringResource(R.string.error_reject_borrowing_failed)
        UiMessageKey.TRY_AGAIN -> stringResource(R.string.error_try_again)
        UiMessageKey.EMAIL_NOT_REGISTERED -> stringResource(R.string.error_email_not_registered)
        UiMessageKey.WRONG_PASSWORD -> stringResource(R.string.error_wrong_password)
        UiMessageKey.CURRENT_PASSWORD_WRONG -> stringResource(R.string.error_current_password_wrong)
        UiMessageKey.INVALID_EMAIL_FORMAT -> stringResource(R.string.error_invalid_email_format)
        UiMessageKey.TOO_MANY_ATTEMPTS -> stringResource(R.string.error_too_many_attempts)
        UiMessageKey.NO_INTERNET -> stringResource(R.string.error_no_internet)
        UiMessageKey.LOGIN_FAILED -> stringResource(R.string.error_login_failed)
        UiMessageKey.EMAIL_ALREADY_REGISTERED -> stringResource(R.string.error_email_already_registered)
        UiMessageKey.REGISTER_FAILED -> stringResource(R.string.error_register_failed)
        UiMessageKey.SUCCESS_PROFILE_SAVED -> stringResource(R.string.success_profile_saved)
        UiMessageKey.GENERAL -> stringResource(R.string.error_general)

        "ID barang tidak valid" -> stringResource(R.string.error_invalid_item_id)
        "ID peminjaman tidak valid" -> stringResource(R.string.error_invalid_borrowing_id)
        "Data barang tidak ditemukan" -> stringResource(R.string.error_item_not_found)
        "Barang tidak ditemukan atau gagal dimuat." -> stringResource(R.string.error_item_not_found_or_failed)
        "Data peminjaman tidak ditemukan" -> stringResource(R.string.error_borrowing_not_found)
        "Data pengembalian tidak ditemukan" -> stringResource(R.string.error_return_not_found)
        "Data pengembalian tidak valid" -> stringResource(R.string.error_return_invalid)
        "Pengembalian ini sudah diproses" -> stringResource(R.string.error_return_already_processed)
        "User tidak ditemukan, silakan login ulang" -> stringResource(R.string.error_user_not_found_login_again)
        "User tidak ditemukan" -> stringResource(R.string.error_user_not_found)
        "UID tidak ditemukan" -> stringResource(R.string.error_uid_not_found)
        "Data user tidak ditemukan di Firestore" -> stringResource(R.string.error_firestore_user_not_found)
        "Tanggal pinjam wajib dipilih" -> stringResource(R.string.error_borrow_date_required)
        "Tanggal kembali wajib dipilih" -> stringResource(R.string.error_return_date_required)
        "Keperluan peminjaman wajib diisi" -> stringResource(R.string.error_borrow_reason_required)
        "Format tanggal tidak valid" -> stringResource(R.string.error_date_format_invalid)
        "Tanggal pinjam tidak boleh sebelum hari ini" -> stringResource(R.string.error_borrow_date_past)
        "Tanggal kembali tidak boleh sebelum tanggal pinjam" -> stringResource(R.string.error_return_date_before_borrow_date)
        "Barang tidak tersedia atau stok sudah habis" -> stringResource(R.string.error_item_unavailable_or_out_of_stock)
        "Stok barang sudah habis" -> stringResource(R.string.error_item_out_of_stock)
        "Gagal mengirim permohonan" -> stringResource(R.string.error_send_borrow_request_failed)
        "Gagal memuat katalog barang" -> stringResource(R.string.error_load_catalog_failed)
        "Gagal memuat favorit" -> stringResource(R.string.error_load_favorites_failed)
        "Gagal memuat riwayat peminjaman" -> stringResource(R.string.error_load_history_failed)
        "Gagal memuat data barang" -> stringResource(R.string.error_load_items_failed)
        "Gagal memuat data peminjaman" -> stringResource(R.string.error_load_borrowings_failed)
        "Gagal memuat data pengembalian" -> stringResource(R.string.error_load_returns_failed)
        "Gagal memuat detail peminjaman" -> stringResource(R.string.error_load_borrowing_detail_failed)

        "Gagal memuat info hari libur" -> stringResource(R.string.error_load_holiday_info_failed)
        "Gagal memuat cache hari libur" -> stringResource(R.string.error_load_holiday_cache_failed)
        "Gagal memuat barang" -> stringResource(R.string.error_load_item_failed)
        "Gagal mengganti password. Coba lagi." -> stringResource(R.string.error_change_password_failed)
        "Gagal memverifikasi pengembalian" -> stringResource(R.string.error_verify_return_failed)
        "Gagal menyetujui pengajuan" -> stringResource(R.string.error_approve_borrowing_failed)
        "Gagal menolak pengajuan" -> stringResource(R.string.error_reject_borrowing_failed)
        "Gagal menambahkan barang" -> stringResource(R.string.error_add_item_failed)
        "Gagal mengedit barang" -> stringResource(R.string.error_edit_item_failed)
        "Gagal menghapus barang" -> stringResource(R.string.error_delete_item_failed)
        "Gagal mengajukan pengembalian" -> stringResource(R.string.error_submit_return_failed)
        "Gagal menolak pengembalian" -> stringResource(R.string.error_reject_return_failed)
        "Kondisi barang wajib dipilih" -> stringResource(R.string.error_return_condition_required)
        "Catatan admin wajib diisi saat menolak pengembalian" -> stringResource(R.string.error_admin_note_required_reject_return)

        "Terjadi kesalahan, coba lagi." -> stringResource(R.string.error_try_again)
        "Email tidak terdaftar." -> stringResource(R.string.error_email_not_registered)
        "Password salah." -> stringResource(R.string.error_wrong_password)
        "Password saat ini salah." -> stringResource(R.string.error_current_password_wrong)
        "Format email tidak valid." -> stringResource(R.string.error_invalid_email_format)
        "Terlalu banyak percobaan. Coba lagi nanti." -> stringResource(R.string.error_too_many_attempts)
        "Tidak ada koneksi internet." -> stringResource(R.string.error_no_internet)
        "Login gagal. Periksa email dan password." -> stringResource(R.string.error_login_failed)
        "Email sudah terdaftar." -> stringResource(R.string.error_email_already_registered)
        "Registrasi gagal. Coba lagi." -> stringResource(R.string.error_register_failed)
        "Nama tidak boleh kosong." -> stringResource(R.string.error_empty_name)
        "Email dan password tidak boleh kosong." -> stringResource(R.string.error_empty_email_password)
        "Nama lengkap tidak boleh kosong." -> stringResource(R.string.error_empty_full_name)
        "Email tidak boleh kosong." -> stringResource(R.string.error_empty_email)
        "Password minimal 6 karakter." -> stringResource(R.string.error_password_min_6)
        "Password saat ini tidak boleh kosong." -> stringResource(R.string.error_current_password_required)
        "Password baru minimal 8 karakter." -> stringResource(R.string.error_new_password_min_8)
        "Konfirmasi password tidak cocok." -> stringResource(R.string.error_password_confirmation_mismatch)
        "Gagal menyimpan." -> stringResource(R.string.error_save_profile_failed)
        "URL tidak ditemukan" -> stringResource(R.string.error_url_not_found)
        "Profil berhasil disimpan." -> stringResource(R.string.success_profile_saved)
        else -> raw
    }
}
