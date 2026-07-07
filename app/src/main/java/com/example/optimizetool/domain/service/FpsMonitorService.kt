package com.example.optimizetool.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Service ini disediakan sebagai skeleton/kerangka dasar untuk memonitor FPS
 * atau menampilkan Floating Window (Overlay) di atas layar jika nantinya Anda
 * ingin menambahkan fitur overlay live performance saat berada di dalam game.
 */
class FpsMonitorService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null // Tidak memerlukan binding untuk saat ini
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Logika untuk Floating Window Overlay (Window Manager) bisa diletakkan di sini.
        // Diperlukan permission SYSTEM_ALERT_WINDOW di AndroidManifest.
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Bersihkan view floating window saat service dihentikan
    }
}