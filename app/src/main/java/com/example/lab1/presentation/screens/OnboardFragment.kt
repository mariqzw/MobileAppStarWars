package com.example.lab1.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lab1.R
import com.example.lab1.databinding.FragmentOnboardBinding

class OnboardFragment : Fragment() {

    private lateinit var binding: FragmentOnboardBinding

    private val TAG = "OnboardFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onCreateView called")

        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_onboardFragment_to_signUpFragment)
        }
    }
}
