package com.example.lab1.presentation.screens

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab1.App
import com.example.lab1.database.CharacterDao
import com.example.lab1.database.CharacterEntity
import com.example.lab1.database.CharacterRepository
import com.example.lab1.databinding.FragmentHomeBinding
import com.example.lab1.models.Character
import com.example.lab1.network.KtorNetwork
import com.example.lab1.network.KtorNetworkApi
import com.example.lab1.presentation.adapter.CharacterAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var adapter: CharacterAdapter
    private val networkApi = KtorNetwork()
    private lateinit var repository: CharacterRepository
    private lateinit var characterDao: CharacterDao

    private val FONT_KEY = stringPreferencesKey("font_size")
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = args.user.username
        Log.d(TAG, "Received username: $username")
        binding.usernameHeader.text = username

        val app = requireActivity().application as App
        characterDao = app.database.characterDao()
        repository = CharacterRepository(networkApi, characterDao)

        loadFontSize()
        setupRecyclerView()

        lifecycleScope.launch {
            observeCharacters()
        }


        binding.btnSettings.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment(args.user)
            findNavController().navigate(action)
        }

        binding.btnSaveToFile.setOnClickListener {
            saveCharactersToExternalStorage()
        }

        binding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                refreshCharactersFromApi()
            }
        }

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                deleteAllCharacters()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CharacterAdapter(emptyList(), 16f)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter
    }

    private suspend fun fetchCharactersFromApi() {
        try {
            val characters = repository.getCharacters()
            Log.d(TAG, "Fetched characters: $characters")
            if (characters.isNotEmpty()) {
                repository.cacheCharacters(characters)
            } else {
                Toast.makeText(requireContext(), "No characters found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // реактивное обновление списка персонажей путем создания наблюдаемого запроса в Room
    private suspend fun observeCharacters() {
        repository.getCharactersFlow().collect { characters ->
            if (characters.isEmpty()) {
                Log.d(TAG, "No cached characters, fetching from API...")
                fetchCharactersFromApi()
            } else {
                Log.d(TAG, "Displaying characters: $characters")
                adapter.setData(characters)
            }
        }
    }

    private suspend fun refreshCharactersFromApi() {
        try {
            repository.refreshCharacters()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error refreshing data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun deleteAllCharacters() {
        try {
            repository.deleteCharacters()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error refreshing data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadFontSize() {
        lifecycleScope.launch {
            val preferences = requireContext().dataStore.data.first()
            val fontSize = preferences[FONT_KEY] ?: "Medium"

            val fontSizeInPx = when (fontSize) {
                "Small" -> 12f
                "Medium" -> 16f
                "Large" -> 20f
                else -> 16f // Default to Medium
            }

            adapter.setFontSize(fontSizeInPx)
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
    }

    override fun onResume() {
        super.onResume()
        loadFontSize()
    }

    private fun saveCharactersToExternalStorage() {
        lifecycleScope.launch {
            try {
                if (!isExternalStorageWritable()) {
                    Toast.makeText(requireContext(), "External storage is not writable", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val characters = networkApi.getCharacters()
                val formattedCharacters = characters.joinToString("\n") { "${it.name} - ${it.homeworld ?: "Unknown"}" }

                val fileName = "19_characters.txt"
                val externalFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName)

                FileOutputStream(externalFile).use { output ->
                    output.write(formattedCharacters.toByteArray())
                    Toast.makeText(requireContext(), "File saved: ${externalFile.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving to external storage: ${e.message}")
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}
