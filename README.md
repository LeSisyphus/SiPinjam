# SiPinjam : Sistem Inventarisasi dan Peminjaman Barang Kampus

Aplikasi mobile Android untuk pengelolaan inventaris dan peminjaman barang kampus secara digital.  
Dibangun sebagai Tugas UTS mata kuliah Pemrograman Mobile, Program Studi Teknologi Informasi, Universitas Lambung Mangkurat.

---

## Tim Pengembang

| Nama                    | NIM           | Role                          | GitHub       |
| ----------------------- | ------------- | ----------------------------- | ------------ |
| Muhammad Maulana Azhari | 2410817210003 | Project Lead, Auth & Admin    | @maulanaazhari |
| Nabilla Putri Nugraha   | 2410817220009 | Katalog, Barang & Profil      | @nabillanugraha |
| Rachel Wina Yuda        | 2410817220030 | Peminjaman, Pengembalian & Notifikasi | @rachelwina |

---

## Tech Stack

| Komponen          | Teknologi                     |
| ----------------- | ----------------------------- |
| Bahasa            | Kotlin                        |
| UI Framework      | Jetpack Compose               |
| Arsitektur        | MVVM                          |
| Navigasi          | Navigation Compose            |
| Autentikasi       | Firebase Authentication       |
| Database          | Firebase Firestore            |
| Push Notification | Firebase Cloud Messaging (FCM)|
| Penyimpanan Foto  | Firebase Storage              |
| Image Loader      | Coil                          |
| Version Control   | Git + GitHub                  |

---

## Cara Instalasi

### Prasyarat

Pastikan sudah terinstall di komputer:

- Android Studio Hedgehog atau lebih baru
- JDK 17+
- Android SDK (minimum API 26 / Android 8.0)
- Akun Google untuk Firebase Console

### Langkah Instalasi

1. Clone repository

    ```bash
    git clone https://github.com/username/SiPinjam.git
    cd SiPinjam
    ```

2. Buka project di Android Studio

    ```
    File → Open → pilih folder SiPinjam
    ```

3. Hubungkan ke Firebase

    - Buka [Firebase Console](https://console.firebase.google.com)
    - Buat project baru atau gunakan project yang sudah ada
    - Tambahkan aplikasi Android dengan package name `com.example.sipinjam`
    - Unduh file `google-services.json` dan letakkan di folder `app/`

    ```
    SiPinjam/
    └── app/
        └── google-services.json   ← letakkan di sini
    ```

4. Aktifkan layanan Firebase yang dibutuhkan di Console:

    - Authentication → Email/Password
    - Firestore Database
    - Storage
    - Cloud Messaging

5. Sinkronisasi Gradle

    ```
    File → Sync Project with Gradle Files
    ```

6. Jalankan aplikasi

    ```
    Run → Run 'app' (Shift+F10)
    ```

    Atau build APK:

    ```
    Build → Build Bundle(s) / APK(s) → Build APK(s)
    ```

---

## Akun Default

| Role  | Cara Akses                         |
| ----- | ---------------------------------- |
| Admin | Daftar via halaman register, lalu ubah role menjadi `admin` langsung di Firestore Console |
| Peminjam | Daftar langsung via halaman Register di aplikasi |

---

## Struktur Firestore

| Collection       | Keterangan                                          |
| ---------------- | --------------------------------------------------- |
| `users`          | Data akun dan profil semua pengguna                 |
| `items`          | Data barang inventaris kampus                       |
| `borrowings`     | Data transaksi peminjaman                           |
| `returns`        | Data pengembalian beserta foto kondisi barang       |
| `notifications`  | Riwayat notifikasi per pengguna                     |

---

## Alur Status Peminjaman

```
Diproses → Disetujui  → Dipinjam → Menunggu Verifikasi → Selesai
         → Ditolak
```

---

## Role Pengguna

### Peminjam (Mahasiswa / Dosen)

- Melihat katalog barang inventaris secara real-time
- Mengajukan permohonan peminjaman
- Memantau status permohonan
- Mengajukan pengembalian barang disertai foto kondisi
- Menerima notifikasi perubahan status dan pengingat jatuh tempo

### Admin

- Memantau dashboard statistik inventaris
- Mengelola data barang (tambah, edit, hapus)
- Meninjau dan menyetujui / menolak permohonan peminjaman
- Memverifikasi pengembalian barang berdasarkan foto
- Menerima notifikasi permohonan dan pengembalian masuk

---

## Lisensi

Proyek ini dibuat untuk keperluan akademik Tugas UTS mata kuliah Pemrograman Mobile,  
Program Studi Teknologi Informasi, Universitas Lambung Mangkurat.
