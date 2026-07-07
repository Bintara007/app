package com.example.optimizetool.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.optimizetool.data.AimPanelRepository
import com.example.optimizetool.data.AimProfile
import com.example.optimizetool.data.DeviceDatabase
import com.example.optimizetool.domain.SmartOptimizer
import com.example.optimizetool.shizuku.ShizukuManager
import com.example.optimizetool.utils.SystemMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Instansiasi Dependencies
    private val systemMonitor = SystemMonitor(application)
    private val optimizer = SmartOptimizer()
    private val aimRepository = AimPanelRepository(application)
    val deviceDatabase = DeviceDatabase()

    // Status Koneksi Shizuku
    val isShizukuConnected = ShizukuManager.isConnected
    val isShizukuGranted = ShizukuManager.hasPermission

    // Flow untuk System Monitor (Berhenti memantau otomatis jika aplikasi di latar belakang)
    val liveStats = systemMonitor.getLiveStats().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Flow untuk Profil Aim Panel
    val aimProfile = aimRepository.currentProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AimProfile("Default", 50f, false)
    )

    // State untuk Pesan Hasil Optimasi
    private val _optimizationMessage = MutableStateFlow("Ready to Optimize")
    val optimizationMessage: StateFlow<String> = _optimizationMessage

    // Mengeksekusi Deep Clean
    fun runSmartOptimize() {
        viewModelScope.launch {
            _optimizationMessage.value = "Optimizing System... Please wait."
            val result = optimizer.executeDeepClean()
            _optimizationMessage.value = result.message
        }
    }

    // Resolusi Manager (Gaming Preset)
    fun applyGamingResolution() {
        viewModelScope.launch {
            if (ShizukuManager.isValidResolution(720, 1600)) {
                ShizukuManager.applyResolution(720, 1600)
                ShizukuManager.applyDensity(320)
            }
        }
    }

    fun restoreResolution() {
        viewModelScope.launch {
            ShizukuManager.resetDisplay()
        }
    }

    // Simpan Profil Aim Panel
    fun saveAimProfile(name: String, sens: Float, smooth: Boolean) {
        viewModelScope.launch {
            aimRepository.saveProfile(AimProfile(name, sens, smooth))
        }
    }
}