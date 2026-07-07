package com.example.optimizetool.ui.resolution

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.optimizetool.R
import com.example.optimizetool.databinding.FragmentResolutionBinding
import com.example.optimizetool.ui.viewmodel.MainViewModel

class ResolutionFragment : Fragment(R.layout.fragment_resolution) {

    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentResolutionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResolutionBinding.bind(view)

        binding.btnGamingRes.setOnClickListener {
            viewModel.applyGamingResolution()
            Toast.makeText(context, "Applying Gaming Resolution...", Toast.LENGTH_SHORT).show()
        }

        binding.btnResetRes.setOnClickListener {
            viewModel.restoreResolution()
            Toast.makeText(context, "Restoring Default Display...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}