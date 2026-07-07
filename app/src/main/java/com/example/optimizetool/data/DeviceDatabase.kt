package com.example.optimizetool.data
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inisialisasi DataStore (Aman untuk menyimpan preferensi profil UI)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aim_panel_profiles")

data class AimProfile(
    val presetName: String,
    val sensitivitySlider: Float,
    val smoothSwitch: Boolean
)

class AimPanelRepository(private val context: Context) {

    private val PRESET_NAME = stringPreferencesKey("preset_name")
    private val SENSITIVITY = floatPreferencesKey("sensitivity")
    private val SMOOTH_SWITCH = booleanPreferencesKey("smooth_switch")

    // Load Profile via Flow (Real-time update ke UI)
    val currentProfile: Flow<AimProfile> = context.dataStore.data.map { prefs ->
        AimProfile(
            presetName = prefs[PRESET_NAME] ?: "Default",
            sensitivitySlider = prefs[SENSITIVITY] ?: 50f,
            smoothSwitch = prefs[SMOOTH_SWITCH] ?: false
        )
    }

    // Save Profile ke penyimpanan perangkat
    suspend fun saveProfile(profile: AimProfile) {
        context.dataStore.edit { prefs ->
            prefs[PRESET_NAME] = profile.presetName
            prefs[SENSITIVITY] = profile.sensitivitySlider
            prefs[SMOOTH_SWITCH] = profile.smoothSwitch
        }
    }

    // Export profil ke format JSON sederhana
    fun exportProfileToJson(profile: AimProfile): String {
        return """{"preset":"${profile.presetName}", "sens":${profile.sensitivitySlider}, "smooth":${profile.smoothSwitch}}"""
    }
}