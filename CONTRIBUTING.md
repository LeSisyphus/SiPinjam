# Panduan Kontribusi Tim SiPinjam

Dokumen ini berisi konvensi yang **wajib diikuti** oleh semua anggota tim selama pengerjaan proyek.

---

## Daftar Isi

- [Alur Kerja](#alur-kerja)
- [Pembagian Area Kerja](#pembagian-area-kerja)
- [Konvensi Penamaan Branch](#konvensi-penamaan-branch)
- [Konvensi Commit Message](#konvensi-commit-message)
- [Konvensi Issue](#konvensi-issue)
- [Konvensi Pull Request](#konvensi-pull-request)
- [File yang Tidak Boleh Diedit Bersamaan](#file-yang-tidak-boleh-diedit-bersamaan)
- [Aturan Tambahan](#aturan-tambahan)

---

## Alur Kerja

Setiap mengerjakan fitur, ikuti alur berikut:

```
Ambil issue yang di-assign → Buat branch baru → Kerjakan fitur
        → Push branch → Buat Pull Request → Minta review
        → Di-approve → Merge ke main → Hapus branch
```

Wajib jalankan perintah ini **setiap kali mulai kerja** agar kode selalu up to date:

```bash
git checkout main
git pull origin main
git checkout nama-branch-kamu
git merge main
```

---

## Pembagian Area Kerja

Untuk menghindari konflik, setiap anggota fokus di area masing-masing:

| Anggota | Area Utama                                                                                  |
| ------- | ------------------------------------------------------------------------------------------- |
| Maulana | `ui/auth/`, `ui/admin/`, Firebase Auth setup, Firestore Security Rules                      |
| Nabilla | `ui/catalog/`, `ui/item/`, `ui/profile/`, komponen card dan katalog barang                  |
| Rachel  | `ui/borrowing/`, `ui/return/`, logika peminjaman & pengembalian, integrasi FCM notifikasi   |

Kalau terpaksa harus edit file di luar area sendiri, **koordinasi dulu di grup WhatsApp** sebelum mulai.

---

## Konvensi Penamaan Branch

Format:

```
<prefix>/<nomor-issue>-<deskripsi-singkat>
```

Contoh yang **benar**:

```bash
git checkout -b feat/12-form-pengajuan-peminjaman
git checkout -b ui/15-redesign-card-katalog
git checkout -b fix/24-status-tidak-update-setelah-approve
git checkout -b chore/03-setup-firebase-fcm
git checkout -b docs/01-tambah-readme
```

Prefix yang dipakai:

| Prefix     | Dipakai untuk                     |
| ---------- | --------------------------------- |
| `feat`     | Fitur baru                        |
| `fix`      | Perbaikan bug                     |
| `ui`       | Perubahan tampilan murni          |
| `chore`    | Konfigurasi, setup, dependencies  |
| `docs`     | Dokumentasi                       |
| `refactor` | Rapikan kode tanpa ubah fungsi    |

---

## Konvensi Commit Message

Format:

```
<prefix>: <deskripsi singkat dalam bahasa indonesia>
```

Aturan penulisan:

- Huruf kecil semua
- Tanpa titik di akhir kalimat
- Maksimal 72 karakter
- Gunakan kalimat perintah

Contoh yang **benar**:

```bash
git commit -m "feat: tambah form pengajuan peminjaman barang"
git commit -m "fix: perbaiki status peminjaman tidak update setelah disetujui"
git commit -m "ui: redesign card katalog barang"
git commit -m "chore: integrasi firebase cloud messaging"
git commit -m "docs: tambah panduan instalasi di README"
git commit -m "refactor: sederhanakan logika verifikasi pengembalian"
```

Contoh yang **salah**:

```bash
# Huruf kapital
git commit -m "Feat: Tambah Form Peminjaman"

# Pakai titik di akhir
git commit -m "feat: tambah form peminjaman."

# Terlalu panjang
git commit -m "feat: tambah form pengajuan peminjaman yang memiliki input tanggal pinjam tanggal kembali dan alasan peminjaman"

# Tidak jelas
git commit -m "update"
git commit -m "fix bug"
git commit -m "perubahan"
```

Kalau commit terkait issue tertentu, sebut nomornya di body commit:

```bash
git commit -m "feat: tambah form pengajuan peminjaman barang

Closes #12"
```

---

## Konvensi Issue

Format judul issue:

```
<nomor urut>. [<prefix>] <deskripsi lengkap>
```

Contoh:

```
12. [feat] Buat form pengajuan peminjaman untuk peminjam
15. [ui] Redesign card katalog barang
24. [fix] Testing manual semua alur dan perbaikan bug
```

Prefix issue:

| Prefix  | Dipakai untuk          |
| ------- | ---------------------- |
| `feat`  | Fitur baru             |
| `fix`   | Bug atau perbaikan     |
| `ui`    | Perubahan tampilan     |
| `chore` | Konfigurasi dan setup  |
| `docs`  | Dokumentasi            |

---

## Konvensi Pull Request

Format judul PR:

```
<prefix>: <deskripsi singkat> (#<nomor issue>)
```

Contoh:

```
feat: tambah form pengajuan peminjaman (#12)
ui: redesign card katalog barang (#15)
fix: perbaiki status tidak update setelah approve (#24)
```

Wajib isi semua bagian di template deskripsi PR yang sudah tersedia.

---

## File yang Tidak Boleh Diedit Bersamaan

File-file berikut rawan konflik jika diedit bersamaan oleh lebih dari satu orang:

| File                          | Aturan                                                              |
| ----------------------------- | ------------------------------------------------------------------- |
| `app/Navigation.kt`           | Koordinasi dulu di grup WhatsApp sebelum edit                       |
| `app/ui/theme/Theme.kt`       | Sepakati design token di awal, jangan diubah sembarangan            |
| `app/ui/theme/Color.kt`       | Sama seperti Theme.kt, koordinasi sebelum mengubah                  |
| `google-services.json`        | **Jangan di-commit ke repository**, sudah masuk `.gitignore`        |
| `local.properties`            | Jangan di-commit, berisi path SDK lokal masing-masing komputer      |
| `build.gradle (Project)`      | Koordinasi jika perlu tambah plugin atau dependency baru            |

---

## Aturan Tambahan

- Jangan push langsung ke `main` — selalu lewat Pull Request
- Jangan merge PR milik sendiri — minta anggota lain untuk review
- Selesaikan semua konflik sebelum minta review
- Hapus branch setelah PR berhasil di-merge
- Jangan commit file `google-services.json`, `local.properties`, atau folder `build/`
- Pastikan aplikasi bisa di-build tanpa error sebelum membuat Pull Request
