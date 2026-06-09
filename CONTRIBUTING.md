# Panduan Kontribusi Tim SiPinjam

Dokumen ini berisi panduan kerja tim SiPinjam agar proses pengembangan aplikasi tetap rapi, minim konflik, dan mudah ditelusuri melalui Git.

---

## Daftar Isi

* [Alur Kerja](#alur-kerja)
* [Pembagian Area Kerja](#pembagian-area-kerja)
* [Konvensi Penamaan Branch](#konvensi-penamaan-branch)
* [Konvensi Commit Message](#konvensi-commit-message)
* [Konvensi Issue](#konvensi-issue)
* [Konvensi Pull Request](#konvensi-pull-request)
* [File yang Tidak Boleh Diedit Bersamaan](#file-yang-tidak-boleh-diedit-bersamaan)
* [Aturan Build dan Testing](#aturan-build-dan-testing)
* [Aturan File Sensitif](#aturan-file-sensitif)
* [Aturan Tambahan](#aturan-tambahan)

---

## Alur Kerja

Setiap anggota tim wajib mengikuti alur kerja berikut:

```text
Ambil task → Buat branch baru → Kerjakan fitur
→ Commit bertahap → Push branch → Buat Pull Request
→ Review → Merge ke main
```

Sebelum mulai mengerjakan task baru, pastikan branch `main` sudah terbaru:

```bash
git switch main
git pull origin main
```

Setelah itu buat branch baru sesuai task:

```bash
git switch -c nama-branch
```

Setelah selesai mengerjakan fitur:

```bash
git status
./gradlew :app:assembleDebug
git add .
git commit -m "prefix: deskripsi singkat"
git push -u origin nama-branch
```

---

## Pembagian Area Kerja

Untuk menghindari konflik, setiap anggota tetap memiliki area utama berdasarkan pembagian awal proyek. Karena struktur folder project sudah berkembang, pembagian area kerja dijelaskan dalam dua bentuk: area fitur utama dan area folder saat ini.

### Area Fitur Utama

| Anggota | Area Utama | Cakupan |
| --- | --- | --- |
| Maulana | Auth, Admin, Profile, Arsitektur | Login/register, autentikasi, dashboard admin, approval admin, profile, integrasi arsitektur, API, Room, repository, use case, dan review merge |
| Nabilla | Katalog, Barang, Profile, Reusable Component, Notifikasi | Katalog barang, detail barang, pengelolaan komponen UI reusable, profile-related UI, dark mode, localization, dan notifikasi |
| Rachel | Peminjaman, Pengembalian, UI User | Form peminjaman, riwayat peminjaman, pengembalian barang, tampilan beranda user, favorite barang, dan user flow |

### Area Folder Project Saat Ini

| Anggota | Folder/File Utama |
| --- | --- |
| Maulana | `screens/auth/`, `screens/admin/`, `screens/user/ProfilScreen.kt`, `data/`, `domain/`, `di/`, `navigation/`, `MainActivity.kt`, `AndroidManifest.xml`, `app/build.gradle.kts`, `gradle/libs.versions.toml` |
| Nabilla | `screens/user/KatalogScreen.kt`, `screens/user/DetailBarangScreen.kt`, `screens/user/ProfilScreen.kt`, `ui/components/`, `ui/theme/`, `app/src/main/res/values/`, `app/src/main/res/values-en/`, `app/src/main/res/values-in/`, `notification/`, `docs/` |
| Rachel | `screens/user/BerandaUserScreen.kt`, `screens/user/PeminjamanScreen.kt`, `screens/user/PeminjamanViewModel.kt`, `screens/user/PengembalianScreen.kt`, `screens/user/PengembalianViewModel.kt`, `screens/user/RiwayatPeminjamanScreen.kt`, `screens/user/FavoriteBarangScreen.kt` |

Jika terdapat file yang masuk ke area lebih dari satu anggota, wajib koordinasi terlebih dahulu di grup sebelum mengedit.

### Pembagian Task UAS Terbaru

| Anggota | Task UAS |
| --- | --- |
| Maulana | Tarik API Hari Libur, implementasi Room Database, cache strategy, domain/use case, repository interface, manual DI AppContainer, use case Favorit Barang, dan review merge |
| Rachel | Splash screen, reusable header component, refactor header, search beranda user, tampilan API Hari Libur di Beranda, loading/empty/error state API, tombol favorit di Detail Barang, halaman Favorit Barang |
| Nabilla | Notifikasi lokal saat perubahan status, revisi PRD, fix string localization, cek form peminjaman/pengembalian agar tidak reset, testing flow aplikasi, dan pembuatan demo |

### Aturan Edit Area Silang

Beberapa file dapat bersinggungan dengan lebih dari satu anggota. Contohnya:

| File | Pemilik Utama | Anggota yang Mungkin Terkait | Aturan |
| --- | --- | --- | --- |
| `screens/user/ProfilScreen.kt` | Maulana/Nabilla | Maulana, Nabilla | Koordinasi sebelum edit karena berkaitan dengan profile, dark mode, dan localization |
| `screens/user/DetailBarangScreen.kt` | Nabilla | Rachel | Koordinasi jika menambah tombol favorit |
| `screens/user/BerandaUserScreen.kt` | Rachel | Maulana | Maulana boleh menyiapkan data/API, Rachel memoles UI |
| `navigation/NavGraph.kt` | Maulana | Semua anggota | Hanya diedit setelah koordinasi karena semua route berada di sini |
| `MainActivity.kt` | Maulana | Nabilla | Koordinasi karena berkaitan dengan auth, AppContainer, dark mode, dan localization |
| `ui/theme/` | Nabilla | Semua anggota | Jangan ubah warna/theme global tanpa koordinasi |
| `data/`, `domain/`, `di/` | Maulana | Rachel/Nabilla | Anggota lain hanya menggunakan use case/contract yang sudah disediakan |
---

## Konvensi Penamaan Branch

Format branch:

```text
<prefix>/<deskripsi-singkat>
```

Contoh branch yang disarankan:

```bash
git switch -c chore/android-build-config
git switch -c feat/room-local-cache
git switch -c feat/holiday-api-cache
git switch -c feat/favorite-items
git switch -c feat/manual-app-container
git switch -c feat/home-holiday-info
git switch -c feat/local-notification
git switch -c fix/restore-dark-localization
git switch -c docs/update-readme
```

Prefix yang digunakan:

| Prefix     | Fungsi                                     |
| ---------- | ------------------------------------------ |
| `feat`     | Fitur baru                                 |
| `fix`      | Perbaikan bug                              |
| `ui`       | Perubahan tampilan murni                   |
| `chore`    | Konfigurasi, setup, dependency, Gradle     |
| `docs`     | Dokumentasi                                |
| `refactor` | Merapikan kode tanpa mengubah fungsi utama |
| `test`     | Testing manual atau automated testing      |

Hindari nama branch yang terlalu umum seperti:

```bash
git switch -c update
git switch -c fix-bug
git switch -c percobaan
```

---

## Konvensi Commit Message

Format commit:

```text
<prefix>: <deskripsi singkat>
```

Aturan:

* Gunakan huruf kecil.
* Tidak memakai titik di akhir.
* Maksimal sekitar 72 karakter.
* Gunakan kalimat singkat dan jelas.
* Commit dibuat bertahap sesuai bagian kerja, jangan menumpuk semua perubahan dalam satu commit besar.

Contoh commit yang benar:

```bash
git commit -m "chore: add Retrofit Room and KSP build configuration"
git commit -m "feat: add Room entities DAO and database"
git commit -m "feat: add holiday API cache strategy"
git commit -m "feat: add favorite item domain foundation"
git commit -m "feat: add manual dependency injection app container"
git commit -m "feat: display holiday information on user home"
git commit -m "fix: restore dark mode and localization root setup"
git commit -m "docs: update README for final project documentation"
git commit -m "docs: add application screenshots"
```

Contoh commit yang salah:

```bash
git commit -m "update"
git commit -m "fix bug"
git commit -m "perubahan"
git commit -m "Feat: Tambah Fitur."
```

Jika commit berhubungan dengan issue tertentu, tulis nomor issue di body commit:

```bash
git commit -m "feat: add favorite item screen

Closes #12"
```

---

## Konvensi Issue

Format judul issue:

```text
<nomor>. [<prefix>] <deskripsi task>
```

Contoh:

```text
12. [feat] Buat halaman favorit barang
13. [fix] Perbaiki dark mode setelah integrasi AppContainer
14. [docs] Update README dan dokumentasi UAS
15. [test] Testing semua flow aplikasi
```

Prefix issue:

| Prefix  | Fungsi                |
| ------- | --------------------- |
| `feat`  | Fitur baru            |
| `fix`   | Bug atau perbaikan    |
| `ui`    | Perubahan tampilan    |
| `chore` | Konfigurasi dan setup |
| `docs`  | Dokumentasi           |
| `test`  | Testing               |

---

## Konvensi Pull Request

Format judul PR:

```text
<prefix>: <deskripsi singkat>
```

Contoh:

```text
feat: add holiday API cache strategy
feat: add favorite item screen
fix: restore dark mode and localization root setup
docs: update README and screenshots
```

Deskripsi PR minimal memuat:

```markdown
## Ringkasan
- Jelaskan perubahan utama yang dilakukan.

## File/Area yang Diubah
- Sebutkan folder atau file penting yang diubah.

## Cara Testing
- Jelaskan cara mengetes fitur.
- Sertakan hasil build jika sudah dilakukan.

## Catatan
- Tulis jika ada bagian yang perlu dicek reviewer.
```

Sebelum membuat PR, wajib menjalankan:

```bash
./gradlew :app:assembleDebug
```

Jika build berhasil, tulis pada deskripsi PR:

```text
Build: SUCCESS
```

---

## File yang Tidak Boleh Diedit Bersamaan

File berikut rawan konflik dan hanya boleh diedit setelah koordinasi:

| File                                                                | Aturan                                                                                   |
| ------------------------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| `app/src/main/java/com/example/sipinjam/MainActivity.kt`            | Koordinasi karena berhubungan dengan theme, localization, auth, dan AppContainer         |
| `app/src/main/java/com/example/sipinjam/navigation/NavGraph.kt`     | Koordinasi karena semua route aplikasi berada di sini                                    |
| `app/src/main/AndroidManifest.xml`                                  | Koordinasi jika menambah permission, application class, activity, service, atau deeplink |
| `app/build.gradle.kts`                                              | Koordinasi jika menambah dependency                                                      |
| `build.gradle.kts`                                                  | Koordinasi jika menambah plugin project-level                                            |
| `gradle/libs.versions.toml`                                         | Koordinasi jika menambah atau mengubah versi library                                     |
| `gradle.properties`                                                 | Koordinasi karena dapat memengaruhi build semua anggota                                  |
| `app/src/main/java/com/example/sipinjam/di/AppContainer.kt`         | Koordinasi karena semua dependency utama disediakan di sini                              |
| `app/src/main/java/com/example/sipinjam/data/local/AppDatabase.kt`  | Koordinasi karena perubahan entity/DAO dapat memengaruhi Room                            |
| `app/src/main/java/com/example/sipinjam/ui/theme/Theme.kt`          | Koordinasi karena memengaruhi dark mode                                                  |
| `app/src/main/java/com/example/sipinjam/ui/theme/Color.kt`          | Koordinasi karena memengaruhi warna global                                               |
| `app/src/main/java/com/example/sipinjam/ui/theme/LocalAppColors.kt` | Koordinasi karena memengaruhi custom color scheme                                        |
| `app/src/main/res/values/strings.xml`                               | Koordinasi agar key string tidak duplikat                                                |
| `app/src/main/res/values-en/strings.xml`                            | Koordinasi untuk localization Bahasa Inggris                                             |
| `README.md`                                                         | Koordinasi jika mengubah dokumentasi utama                                               |

Jika file-file tersebut perlu diubah, komunikasikan terlebih dahulu di grup dan jelaskan bagian yang akan diedit.

---

## Aturan Build dan Testing

Sebelum push atau PR, jalankan:

```bash
./gradlew :app:assembleDebug
```

Jika mengalami error build, jangan langsung push sebelum error diselesaikan.

Minimal testing manual sebelum PR:

```text
1. Aplikasi berhasil dibuka.
2. Login user berhasil.
3. Login admin berhasil.
4. Navigasi utama tidak crash.
5. Fitur yang dikerjakan berjalan sesuai tujuan.
6. Tidak merusak dark mode.
7. Tidak merusak localization.
8. Tidak merusak flow peminjaman/pengembalian.
```

Untuk task besar, tambahkan bukti screenshot atau rekaman singkat jika memungkinkan.

---

## Aturan File Sensitif

File berikut tidak boleh di-commit:

```text
local.properties
*.jks
*.keystore
build/
.gradle/
app/build/
```

Catatan untuk `google-services.json`:

* Jika repository bersifat publik, sebaiknya `google-services.json` tidak di-commit.
* Jika dosen meminta project langsung dapat dijalankan, file ini dapat disediakan melalui mekanisme pengumpulan terpisah atau repository private.
* Koordinasikan dulu sebelum mengubah aturan `google-services.json`.

File `local.properties` wajib dibuat sendiri oleh masing-masing anggota karena berisi path SDK dan credential Cloudinary:

```properties
sdk.dir=/path/to/android/sdk
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

---

## Aturan Tambahan

* Jangan push langsung ke `main` untuk perubahan besar.
* Gunakan branch fitur untuk setiap task.
* Jangan merge PR milik sendiri tanpa review anggota lain.
* Selesaikan konflik sebelum meminta review.
* Hapus branch setelah PR berhasil di-merge.
* Jangan commit file hasil build seperti APK ke source code, kecuali memang diminta sebagai release asset terpisah.
* Jangan commit credential pribadi.
* Jangan menghapus fitur anggota lain saat melakukan merge.
* Jika fitur dark mode atau localization rusak setelah merge, prioritaskan fix sebelum melanjutkan fitur baru.
* Jika fitur API/Room/DI mengubah file root seperti `MainActivity.kt` atau `NavGraph.kt`, pastikan fitur lama tetap berfungsi.

---

## Catatan Struktur Project

Struktur project utama:

```text
com.example.sipinjam
├── data
│   ├── local
│   ├── remote
│   ├── repository
│   └── mapper
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── di
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

Setiap perubahan sebaiknya mengikuti struktur tersebut agar project tetap rapi dan mudah dijelaskan saat presentasi.

---
