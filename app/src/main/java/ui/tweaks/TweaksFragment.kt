package com.example.optimizetool.ui.tweaks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.optimizetool.R
import com.example.optimizetool.databinding.FragmentTweaksBinding
import com.example.optimizetool.ui.viewmodel.MainViewModel

class TweaksFragment : Fragment(R.layout.fragment_tweaks) {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentTweaksBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTweaksBinding.bind(view)

        val db = viewModel.deviceDatabase
        val ram = db.totalRamMb

        binding.tvDeviceInfo.text = "Device: ${db.brand} ${db.model}\nRAM: ${ram} MB\nScore: ${db.getGamingScore()}"

        // Logika rekomendasi tweak sederhana
        val recommendation = if (ram < 4000) {
            "Rekomendasi: Kurangi animasi sistem (Window Scale 0.5x) untuk performa lebih ringan."
        } else {
            "Rekomendasi: Perangkat Anda mumpuni, aktifkan High Performance Mode."
        }
        binding.tvRecommendation.text = recommendation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}