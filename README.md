# SiPinjam : Sistem Inventarisasi dan Peminjaman Barang Kampus

Aplikasi mobile Android untuk pengelolaan inventaris dan peminjaman barang kampus secara digital.  
Dibangun sebagai Proyek Akhir mata kuliah Pemrograman Mobile, Program Studi Teknologi Informasi, Universitas Lambung Mangkurat.

---

## Tim Pengembang

| Nama                    | NIM           | Role                               | GitHub               |
| ----------------------- | ------------- |------------------------------------|----------------------|
| Muhammad Maulana Azhari | 2410817210003 | Project Lead, Auth, Profil & Admin | @LeSisyphus          |
| Nabilla Putri Nugraha   | 2410817220009 | Katalog, Barang & Notifikasi       | @NabillaPutriNugraha |
| Rachel Wina Yuda        | 2410817220030 | Peminjaman dan Pengembalian        | @raequellee          |

## Tech Stack

| Komponen             | Teknologi                              |
| -------------------- | -------------------------------------- |
| Bahasa               | Kotlin                                 |
| UI Framework         | Jetpack Compose                        |
| Arsitektur           | MVVM + Clean Architecture              |
| Navigasi             | Navigation Compose                     |
| Autentikasi          | Firebase Authentication                |
| Remote Database      | Firebase Firestore                     |
| Local Database       | Room Database                          |
| API Pihak Ketiga     | API Hari Libur Indonesia               |
| HTTP Client          | Retrofit                               |
| JSON Converter       | Gson Converter                         |
| Network Logging      | OkHttp Logging Interceptor             |
| Penyimpanan Foto     | Cloudinary                             |
| Image Loader         | Coil                                   |
| State Management     | ViewModel, StateFlow, rememberSaveable |
| Dependency Injection | Manual DI melalui AppContainer         |
| Theme                | Custom Theme + Dark Mode               |
| Localization         | Android String Resources               |
| Version Control      | Git + GitHub                           |

---

## Fitur Utama

### Peminjam

* Registrasi dan login akun.
* Melihat katalog barang inventaris.
* Mencari barang dari halaman beranda atau katalog.
* Melihat detail barang.
* Mengajukan peminjaman barang.
* Melihat riwayat dan status peminjaman.
* Mengajukan pengembalian barang.
* Mengunggah foto kondisi barang saat pengembalian.
* Melihat informasi Hari Libur Indonesia dari API pihak ketiga.
* Menyimpan barang favorit secara lokal menggunakan Room Database.
* Menggunakan dark mode dan pilihan bahasa.

### Admin

* Melihat dashboard statistik inventaris.
* Melihat permintaan peminjaman terbaru.
* Mengelola data barang.
* Menambahkan barang baru.
* Mengedit data barang.
* Menghapus data barang.
* Meninjau detail permohonan peminjaman.
* Menyetujui atau menolak peminjaman.
* Melihat daftar pengembalian.
* Memverifikasi pengembalian barang.
* Melihat perubahan data secara real-time melalui Firestore.

---

## Pemenuhan Requirement UAS

| Requirement        | Implementasi di SiPinjam                                                                                                                                          |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Minimal 6 screen   | Login, Register, Beranda, Katalog, Detail Barang, Peminjaman, Riwayat, Pengembalian, Profil, Dashboard Admin, Kelola Barang, Persetujuan, Verifikasi Pengembalian |
| Recycle-able list  | Menggunakan LazyColumn dan LazyRow pada katalog, riwayat, dashboard, persetujuan, dan daftar barang                                                               |
| State management   | Menggunakan ViewModel, StateFlow, rememberSaveable, dan state Compose                                                                                             |
| MVVM               | Screen sebagai View, ViewModel sebagai pengelola state, Repository sebagai sumber data                                                                            |
| API pihak ketiga   | API Hari Libur Indonesia menggunakan Retrofit                                                                                                                     |
| Clean Architecture | Pemisahan UI Layer, Domain Layer, dan Data Layer                                                                                                                  |
| Local database     | Room Database untuk cache Hari Libur dan data favorit barang                                                                                                      |
| BREAD              | Admin dapat Browse, Read, Edit, Add, dan Delete data barang                                                                                                       |

---

## Fitur Tambahan

| Fitur Tambahan              | Implementasi                                                                           |
| --------------------------- | -------------------------------------------------------------------------------------- |
| Real-time data              | Firestore Snapshot Listener untuk data barang, peminjaman, pengembalian, dan dashboard |
| Cache strategy              | Data API Hari Libur diambil dari network lalu disimpan ke Room Database                |
| Manual Dependency Injection | AppContainer menyediakan Firebase, Room, Repository, API Service, dan Use Case         |
| Dark Mode                   | Menggunakan custom theme `SiPinjamTheme` dan `LocalAppColors`                          |
| Localization                | Menggunakan string resource untuk Bahasa Indonesia dan Bahasa Inggris                  |
| Favorite Barang             | Data favorit disimpan secara lokal menggunakan Room Database                           |
| Local Notification          | Dalam proses integrasi untuk perubahan status peminjaman dan pengembalian              |

---

## API Pihak Ketiga

SiPinjam menggunakan API Hari Libur Indonesia untuk menampilkan informasi hari libur pada halaman beranda.

Base URL:

```text
https://libur.deno.dev/
```

Endpoint yang digunakan:

```text
GET /api/today
GET /api?year={tahun}&month={bulan}
```

Contoh endpoint:

```text
https://libur.deno.dev/api/today
https://libur.deno.dev/api?year=2026&month=6
```

Data dari API ini digunakan untuk menampilkan informasi hari libur terdekat kepada pengguna. Data yang berhasil diambil akan disimpan ke Room Database sebagai cache lokal.

---

## Local Database

SiPinjam menggunakan Room Database sebagai local database.

### Tabel Lokal

| Tabel            | Fungsi                                                |
| ---------------- | ----------------------------------------------------- |
| `holidays`       | Menyimpan cache data Hari Libur dari API              |
| `favorite_items` | Menyimpan daftar barang favorit pengguna secara lokal |

### Cache Strategy

Alur cache Hari Libur:

```text
UI
→ ViewModel
→ Use Case
→ Repository
→ Remote Data Source / API
→ Room Database
→ UI menampilkan data cache terbaru
```

Jika API gagal dimuat karena koneksi internet bermasalah, aplikasi tetap dapat menampilkan data terakhir yang tersimpan di Room Database.

---

## Arsitektur Project

SiPinjam menerapkan MVVM dengan pendekatan Clean Architecture.

```text
com.example.sipinjam
├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   └── AppDatabase.kt
│   ├── remote
│   │   └── holiday
│   ├── repository
│   └── mapper
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── di
│   └── AppContainer.kt
├── navigation
├── screens
│   ├── admin
│   ├── auth
│   └── user
├── ui
│   ├── components
│   └── theme
└── utils
```

### UI Layer

Berisi screen Jetpack Compose, komponen UI reusable, navigation, dan theme.

Contoh:

```text
screens/user/
screens/admin/
ui/components/
ui/theme/
navigation/
```

### Domain Layer

Berisi model domain, repository interface, dan use case.

Contoh:

```text
domain/model/
domain/repository/
domain/usecase/
```

### Data Layer

Berisi implementasi repository, remote API, local database, entity, DAO, dan mapper.

Contoh:

```text
data/local/
data/remote/
data/repository/
data/mapper/
```

---

## Manual Dependency Injection

SiPinjam menggunakan manual dependency injection melalui `AppContainer`.

`AppContainer` bertugas menyediakan dependency utama seperti:

* Firebase Auth Repository
* Firestore Repository
* Retrofit API Service
* Room Database
* DAO
* Repository Implementation
* Use Case
* ViewModel Factory

Dengan pendekatan ini, ViewModel tidak membuat dependency secara langsung, sehingga struktur kode lebih rapi dan mudah dikembangkan.

---

## Struktur Firestore

| Collection      | Keterangan                                                       |
| --------------- | ---------------------------------------------------------------- |
| `users`         | Data akun dan profil pengguna                                    |
| `items`         | Data barang inventaris                                           |
| `borrowings`    | Data transaksi peminjaman                                        |
| `returns`       | Data pengembalian barang                                         |
| `notifications` | Data notifikasi/informasi status jika fitur notifikasi digunakan |

---

## Struktur Cloudinary

| Folder               | Isi                                   |
| -------------------- | ------------------------------------- |
| `foto_profil/`       | Foto profil pengguna                  |
| `foto_barang/`       | Foto barang inventaris                |
| `foto_pengembalian/` | Foto kondisi barang saat dikembalikan |

Pada rancangan awal PRD, penyimpanan foto direncanakan menggunakan Firebase Storage. Pada implementasi UAS, penyimpanan foto dipindahkan ke Cloudinary karena lebih sesuai untuk kebutuhan upload dan akses foto barang, foto profil, serta foto pengembalian.

---

## Alur Status Peminjaman

```text
Diproses
→ Disetujui
→ Dipinjam
→ Menunggu Verifikasi
→ Selesai
```

Jika permohonan ditolak:

```text
Diproses
→ Ditolak
```

---

## Screenshot Aplikasi

Simpan screenshot aplikasi pada folder `screenshots/`.

Contoh struktur:

```text
screenshots/
├── login.png
├── register.png
├── beranda.png
├── katalog.png
├── detail-barang.png
├── peminjaman.png
├── riwayat.png
├── profil.png
├── admin-dashboard.png
├── kelola-barang.png
├── persetujuan.png
└── verifikasi-pengembalian.png
```

Contoh tampilan di README:

| Login                           | Beranda                             | Katalog                             |
| ------------------------------- | ----------------------------------- | ----------------------------------- |
| ![Login](screenshots/login.png) | ![Beranda](screenshots/beranda.png) | ![Katalog](screenshots/katalog.png) |

| Detail Barang                                   | Dashboard Admin                                     | Kelola Barang                                   |
| ----------------------------------------------- | --------------------------------------------------- | ----------------------------------------------- |
| ![Detail Barang](screenshots/detail-barang.png) | ![Dashboard Admin](screenshots/admin-dashboard.png) | ![Kelola Barang](screenshots/kelola-barang.png) |

---

## Cara Instalasi

### Prasyarat

Pastikan sudah terpasang:

* Android Studio versi terbaru
* JDK 17 atau lebih baru
* Android SDK minimum API 24
* Akun Firebase
* Akun Cloudinary
* Koneksi internet

---

### 1. Clone Repository

```bash
git clone https://github.com/LeSisyphus/SiPinjam.git
cd SiPinjam
```

---

### 2. Buka Project di Android Studio

```text
File → Open → pilih folder SiPinjam
```

---

### 3. Setup Firebase

Buka Firebase Console, lalu buat atau gunakan project Firebase yang sudah ada.

Aktifkan layanan berikut:

* Firebase Authentication
* Firestore Database
* Firebase Analytics jika digunakan
* Firebase Cloud Messaging jika fitur push notification digunakan

Tambahkan aplikasi Android dengan package name:

```text
com.example.sipinjam
```

Unduh file:

```text
google-services.json
```

Letakkan di folder:

```text
SiPinjam/app/google-services.json
```

---

### 4. Setup Cloudinary

Buat akun Cloudinary, lalu ambil credential dari dashboard:

* Cloud Name
* API Key
* API Secret

Tambahkan ke file `local.properties` pada root project:

```properties
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

Jika `local.properties` belum ada, buat file tersebut di root project.

---

### 5. Konfigurasi `local.properties`

Contoh isi lengkap:

```properties
sdk.dir=/path/to/your/android/sdk
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

File `local.properties` tidak boleh di-commit karena berisi konfigurasi lokal dan credential sensitif.

---

### 6. Sinkronisasi Gradle

Di Android Studio:

```text
File → Sync Project with Gradle Files
```

Atau melalui terminal:

```bash
./gradlew clean
```

---

### 7. Jalankan Aplikasi

Melalui Android Studio:

```text
Run → Run 'app'
```

Atau melalui terminal:

```bash
./gradlew :app:assembleDebug
```

APK debug akan dibuat di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## Build APK

Untuk membuat APK debug:

```bash
./gradlew :app:assembleDebug
```

Untuk membuka lokasi APK di macOS:

```bash
open app/build/outputs/apk/debug/
```

Untuk menginstall APK ke emulator atau perangkat:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Catatan Gradle

Project ini menggunakan KSP untuk Room Database. Jika muncul warning berikut:

```text
The option setting 'android.disallowKotlinSourceSets=false' is experimental.
```

Warning tersebut tidak menghentikan build. Selama build menghasilkan `BUILD SUCCESSFUL`, aplikasi tetap aman dijalankan.

---

## Akun Default

| Role          | Cara Akses                                                                                        |
| ------------- | ------------------------------------------------------------------------------------------------- |
| User/Peminjam | Daftar melalui halaman Register                                                                   |
| Admin         | Daftar melalui halaman Register, lalu ubah field `role` menjadi `admin` melalui Firestore Console |

---

## Dokumentasi Tambahan

Dokumentasi tambahan disimpan pada folder `docs/`.


---

## Lisensi

Project ini dibuat untuk keperluan akademik Proyek Akhir/UAS mata kuliah Pemrograman Mobile, Program Studi Teknologi Informasi, Universitas Lambung Mangkurat.

---
