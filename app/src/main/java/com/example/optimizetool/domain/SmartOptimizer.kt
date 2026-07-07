package com.example.optimizetool.domain

import com.example.optimizetool.shizuku.ShizukuManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class OptimizationResult(
    val success: Boolean,
    val message: String,
    val score: Int
)

class SmartOptimizer {

    /**
     * Mengeksekusi pembersihan mendalam (trim cache & am kill-all) via Shizuku.
     * Hanya akan berhasil jika Shizuku memiliki akses.
     */
    suspend fun executeDeepClean(): OptimizationResult = withContext(Dispatchers.IO) {
        try {
            // Trim caches pada semua aplikasi yang terinstall
            ShizukuManager.executeShell("pm trim-caches 9999999999999")

            // Matikan proses background yang tidak esensial
            ShizukuManager.executeShell("am kill-all")

            OptimizationResult(
                success = true,
                message = "Memori berhasil dibebaskan & Background Apps dihentikan.",
                score = 100
            )
        } catch (e: Exception) {
            OptimizationResult(
                success = false,
                message = "Gagal mengoptimasi: ${e.message}",
                score = 0
            )
        }
    }
}