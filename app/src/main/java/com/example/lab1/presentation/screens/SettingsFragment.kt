package com.example.lab1.presentation.screens

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import java.io.File
import kotlin.system.exitProcess

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val EMAIL_KEY = stringPreferencesKey("email")
    private val FONT_KEY = stringPreferencesKey("font_size")

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
        displayFileStatus()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }


        binding.btnDeleteFile.setOnClickListener {
            deleteFile()
        }

        binding.btnRestoreFile.setOnClickListener {
            restoreFile()
        }
    }
    private fun loadSettings() {
        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first()
            val savedEmail = preferences[EMAIL_KEY] ?: args.user.email
            val savedFontSize = preferences[FONT_KEY] ?: "Medium"

            binding.oldEmail.text = savedEmail
            binding.etEmail.setText(savedEmail)
            binding.seekBarFontSize.progress = when (savedFontSize) {
                "Small" -> 0
                "Medium" -> 50
                "Large" -> 100
                else -> 50
            }

            Log.d(TAG, "Loaded email: $savedEmail, font size: $savedFontSize")
        }
    }

    private fun saveSettings() {
        val emailToSave = binding.etEmail.text.toString()
        val fontSize = when (binding.seekBarFontSize.progress) {
            in 0..33 -> "Small"
            in 34..66 -> "Medium"
            else -> "Large"
        }

        saveFontSize(fontSize)
        saveEmail(emailToSave)

        binding.oldEmail.text = emailToSave
        Log.d(TAG, "Saved email: $emailToSave, font size: $fontSize")
    }

    private fun saveFontSize(fontSize: String) {
        lifecycleScope.launch {
            requireContext().dataStore.edit { preferences ->
                preferences[FONT_KEY] = fontSize
            }
        }
    }

    private fun saveEmail(email: String) {
        lifecycleScope.launch {
            requireContext().dataStore.edit { preferences ->
                preferences[EMAIL_KEY] = email
            }
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

    private fun displayFileStatus() {
        val externalFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "19_characters.txt")
        val internalBackup = File(requireContext().filesDir, "backup_characters.txt")

        if (externalFile.exists()) {
            binding.tvFileStatus.text = "File exists: ${externalFile.absolutePath}"
            binding.btnDeleteFile.visibility = View.VISIBLE
            binding.btnRestoreFile.visibility = View.GONE
        } else if (internalBackup.exists()) {
            binding.tvFileStatus.text = "Backup exists in internal storage"
            binding.btnDeleteFile.visibility = View.GONE
            binding.btnRestoreFile.visibility = View.VISIBLE
        } else {
            binding.tvFileStatus.text = "No file or backup found"
            binding.btnDeleteFile.visibility = View.GONE
            binding.btnRestoreFile.visibility = View.GONE
        }
    }

    private fun deleteFile() {
        val externalFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "19_characters.txt")
        val internalBackup = File(requireContext().filesDir, "backup_characters.txt")

        if (externalFile.exists()) {
            externalFile.copyTo(internalBackup, overwrite = true)
            externalFile.delete()
            Toast.makeText(requireContext(), "File deleted and backup created", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show()
        }
        displayFileStatus()
    }

    private fun restoreFile() {
        val internalBackup = File(requireContext().filesDir, "backup_characters.txt")
        val externalFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "19_characters.txt")

        if (internalBackup.exists()) {
            internalBackup.copyTo(externalFile, overwrite = true)
            Toast.makeText(requireContext(), "File restored to external storage", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No backup file found", Toast.LENGTH_SHORT).show()
        }
        displayFileStatus()
    }
}
