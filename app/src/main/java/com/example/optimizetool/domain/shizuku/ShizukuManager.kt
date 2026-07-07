package com.example.optimizetool.shizuku

import android.content.pm.PackageManager
import dev.rikka.shizuku.Shizuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuManager {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission

    init {
        Shizuku.addBinderReceivedListenerSticky {
            _isConnected.value = true
            _hasPermission.value = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
        Shizuku.addBinderDeadListener {
            _isConnected.value = false
            _hasPermission.value = false
        }
    }

    suspend fun executeShell(command: String): String = withContext(Dispatchers.IO) {
        if (!_hasPermission.value) return@withContext "Permission Denied"
        try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) output.append(line).append("\n")
            process.waitFor()
            output.toString().trim()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    // Fitur 2: Resolution & Density Manager
    suspend fun applyResolution(width: Int, height: Int) = executeShell("wm size ${width}x${height}")
    suspend fun applyDensity(dpi: Int) = executeShell("wm density $dpi")
    suspend fun resetDisplay() {
        executeShell("wm size reset")
        executeShell("wm density reset")
    }

    // Preset Validations
    fun isValidResolution(width: Int, height: Int): Boolean = width in 480..2160 && height in 800..3840
    fun isValidDensity(dpi: Int): Boolean = dpi in 120..1000
}