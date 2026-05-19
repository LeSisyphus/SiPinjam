# SiPinjam : Sistem Inventarisasi dan Peminjaman Barang Kampus

Aplikasi mobile Android untuk pengelolaan inventaris dan peminjaman barang kampus secara digital.  
Dibangun sebagai Proyek Akhir mata kuliah Pemrograman Mobile, Program Studi Teknologi Informasi, Universitas Lambung Mangkurat.

---

## Tim Pengembang

| Nama                    | NIM           | Role                                          | GitHub               |
| ----------------------- | ------------- | --------------------------------------------- |----------------------|
| Muhammad Maulana Azhari | 2410817210003 | Project Lead, Auth & Admin                    | @LeSisyphus          |
| Nabilla Putri Nugraha   | 2410817220009 | Katalog, Barang & Profil                      | @NabillaPutriNugraha |
| Rachel Wina Yuda        | 2410817220030 | Peminjaman, Pengembalian & Notifikasi         | @raequellee          |

---

## Tech Stack

| Komponen          | Teknologi                        |
| ----------------- | -------------------------------- |
| Bahasa            | Kotlin                           |
| UI Framework      | Jetpack Compose                  |
| Arsitektur        | MVVM                             |
| Navigasi          | Navigation Compose               |
| Autentikasi       | Firebase Authentication          |
| Database          | Firebase Firestore               |
| Penyimpanan Foto  | Cloudinary (v2.3.1)              |
| Push Notification | Firebase Cloud Messaging (FCM)   |
| Image Loader      | Coil                             |
| Version Control   | Git + GitHub                     |

---

## Cara Instalasi

### Prasyarat

Pastikan sudah terinstall di komputer:

- Android Studio Hedgehog atau lebih baru
- JDK 17+
- Android SDK (minimum API 24 / Android 7.0)
- Akun Google untuk Firebase Console
- Akun Cloudinary 

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
    - Cloud Messaging

5. Setup Cloudinary

    - Daftar di [cloudinary.com](https://cloudinary.com) (gratis, tanpa kartu kredit)
    - Buka dashboard → **Product Environment Credentials**
    - Catat `Cloud Name`, `API Key`, dan `API Secret`
    - Tambahkan ke file `local.properties` di root project:

    ```properties
    CLOUDINARY_CLOUD_NAME=your_cloud_name
    CLOUDINARY_API_KEY=your_api_key
    CLOUDINARY_API_SECRET=your_api_secret
    ```

6. Sinkronisasi Gradle

    ```
    File → Sync Project with Gradle Files
    ```

7. Jalankan aplikasi

    ```
    Run → Run 'app' (Shift+F10)
    ```

    Atau build APK:

    ```
    Build → Build Bundle(s) / APK(s) → Build APK(s)
    ```

---

## Konfigurasi `local.properties`

File `local.properties` **tidak di-commit ke repository** karena berisi kredensial sensitif. Setiap anggota tim wajib membuat file ini secara lokal dengan isi berikut:

```properties
sdk.dir=/path/to/your/android/sdk
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

---

## Akun Default

| Role     | Cara Akses                                                                                  |
| -------- | ------------------------------------------------------------------------------------------- |
| Admin    | Daftar via halaman Register, lalu ubah field `role` menjadi `admin` langsung di Firestore Console |
| Peminjam | Daftar langsung via halaman Register di aplikasi                                            |

---

## Struktur Firestore

| Collection      | Keterangan                                          |
| --------------- | --------------------------------------------------- |
| `users`         | Data akun dan profil semua pengguna                 |
| `items`         | Data barang inventaris kampus                       |
| `borrowings`    | Data transaksi peminjaman                           |
| `returns`       | Data pengembalian beserta foto kondisi barang       |
| `notifications` | Riwayat notifikasi per pengguna                     |

---

## Struktur Cloudinary

| Folder              | Isi                                      |
| ------------------- | ---------------------------------------- |
| `foto_profil/`      | Foto profil pengguna (public_id = UID)   |
| `foto_pengembalian/`| Foto kondisi barang saat dikembalikan    |

---

## Alur Status Peminjaman

```
Diproses → Disetujui  → Dipinjam → Menunggu Verifikasi → Selesai
         → Ditolak
```

---

## Role Pengguna

### Peminjam (Mahasiswa / Dosen / Staf)

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

Proyek ini dibuat untuk keperluan akademik Proyek Akhir mata kuliah Pemrograman Mobile,  
Program Studi Teknologi Informasi, Universitas Lambung Mangkurat.
