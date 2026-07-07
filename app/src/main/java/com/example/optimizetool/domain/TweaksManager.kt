package com.example.optimizetool.domain

class TweaksManager {
    fun getRecommendations(ramMb: Long): String {
        return if (ramMb < 4000) {
            "Rekomendasi: Kurangi animasi sistem (Window Scale 0.5x) untuk performa lebih ringan."
        } else {
            "Rekomendasi: Perangkat Anda cukup mumpuni, aktifkan High Performance Mode."
        }
    }
}


### 5. Pengisian `AndroidManifest.xml` (Update)
Pastikan `App.kt` yang Anda buat didaftarkan di `AndroidManifest.xml` agar inisialisasi berjalan saat aplikasi dimulai:
```xml
<application
    android:name=".App"
    ... >