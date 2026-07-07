package com.example.optimizetool.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.optimizetool.R
import com.example.optimizetool.databinding.FragmentDashboardBinding
import com.example.optimizetool.shizuku.ShizukuManager
import com.example.optimizetool.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        binding.btnRequestShizuku.setOnClickListener {
            ShizukuManager.requestPermission(101) // Request code
        }

        binding.btnOptimize.setOnClickListener {
            viewModel.runSmartOptimize()
        }

        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observe Shizuku
                launch {
                    viewModel.isShizukuGranted.collect { isGranted ->
                        if (isGranted) {
                            binding.tvShizukuStatus.text = "Shizuku: Connected & Granted"
                            binding.tvShizukuStatus.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
                            binding.btnRequestShizuku.visibility = View.GONE
                        } else {
                            binding.tvShizukuStatus.text = "Shizuku: Not Granted"
                            binding.tvShizukuStatus.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
                            binding.btnRequestShizuku.visibility = View.VISIBLE
                        }
                    }
                }

                // Observe Live Stats (CPU, RAM)
                launch {
                    viewModel.liveStats.collect { stats ->
                        if (stats != null) {
                            val info = """
                                CPU Usage : ${stats.cpuUsage}%
                                RAM Free  : ${stats.ramFreeMb} MB
                                Batt Temp : ${stats.batteryTemp} °C
                                Net Speed : ${stats.networkSpeedKbps} Kbps
                                Ping      : ${stats.pingMs} ms
                            """.trimIndent()
                            binding.tvLiveStats.text = info
                        }
                    }
                }

                // Observe Optimize Result
                launch {
                    viewModel.optimizationMessage.collect { msg ->
                        binding.tvOptimizeResult.text = msg
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}