package com.example.lab1.presentation.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lab1.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val EMAIL_KEY = stringPreferencesKey("email")

    private val args: SettingsFragmentArgs by navArgs()
    private val TAG = "SettingsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first()
            val savedEmail = preferences[EMAIL_KEY] ?: args.user.email

            binding.oldEmail.text = savedEmail
            binding.etEmail.setText(savedEmail)
            Log.d(TAG, "Loaded email for UI: $savedEmail")
        }
    }

    private fun saveSettings() {
        val emailToSave = binding.etEmail.text.toString()

        lifecycleScope.launch {
            requireContext().dataStore.edit { preferences ->
                preferences[EMAIL_KEY] = emailToSave
            }

            binding.oldEmail.text = emailToSave
            Log.d(TAG, "Saved email: $emailToSave")
        }
    }

    private suspend fun clearUserData() {
        requireContext().dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            clearUserData()

            requireActivity().finish()
            exitProcess(0)
        }
    }
}
