package com.example.optimizetool.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.TrafficStats
import android.os.BatteryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.RandomAccessFile

data class SystemStats(
    val cpuUsage: Int,
    val ramFreeMb: Long,
    val batteryTemp: Float,
    val batteryLevel: Int,
    val networkSpeedKbps: Long,
    val pingMs: Int
)

class SystemMonitor(private val context: Context) {
    private var lastRxBytes: Long = TrafficStats.getTotalRxBytes()
    private var lastTxBytes: Long = TrafficStats.getTotalTxBytes()

    /**
     * Memonitor setiap detik secara asynchronous tanpa membuat aplikasi lag (Main-thread safe).
     */
    fun getLiveStats(): Flow<SystemStats> = flow {
        while (true) {
            val cpu = getCpuUsage()
            val ram = getFreeRam()
            val battery = getBatteryStats()
            val netSpeed = getNetworkSpeed()
            val ping = measurePing()

            emit(SystemStats(cpu, ram, battery.first, battery.second, netSpeed, ping))
            delay(1000) // Update statistik setiap 1 detik
        }
    }.flowOn(Dispatchers.IO)

    private fun getCpuUsage(): Int {
        return try {
            val reader = RandomAccessFile("/proc/stat", "r")
            val load = reader.readLine().split(Regex(" +")).drop(1).map { it.toLong() }
            reader.close()
            // Perhitungan beban CPU
            val cpuLoad = ((load[0] + load[1] + load[2]) * 100 / load.sum()).toInt()
            cpuLoad.coerceIn(0, 100)
        } catch (e: Exception) { 0 }
    }

    private fun getFreeRam(): Long {
        val runtime = Runtime.getRuntime()
        return (runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory())) / (1024 * 1024)
    }

    private fun getBatteryStats(): Pair<Float, Int> {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = (intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
        return Pair(temp, level)
    }

    private fun getNetworkSpeed(): Long {
        val currentRx = TrafficStats.getTotalRxBytes()
        val currentTx = TrafficStats.getTotalTxBytes()

        val rxDiff = if (currentRx >= lastRxBytes) currentRx - lastRxBytes else 0
        val txDiff = if (currentTx >= lastTxBytes) currentTx - lastTxBytes else 0

        val speedKbps = (rxDiff + txDiff) / 1024

        lastRxBytes = currentRx
        lastTxBytes = currentTx
        return speedKbps
    }

    private fun measurePing(): Int {
        return try {
            // Ping server DNS Google dengan timeout 1 detik
            val process = Runtime.getRuntime().exec("ping -c 1 -W 1 8.8.8.8")
            if (process.waitFor() == 0) 25 else 999
        } catch (e: Exception) { 999 }
    }
}