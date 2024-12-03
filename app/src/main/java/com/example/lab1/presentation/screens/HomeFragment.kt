package com.example.lab1.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab1.databinding.FragmentHomeBinding
import com.example.lab1.network.KtorNetwork
import com.example.lab1.presentation.adapter.CharacterAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var adapter: CharacterAdapter
    private val networkApi = KtorNetwork()

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

        loadFontSize()

        setupRecyclerView()

        fetchCharacters()

        binding.btnSettings.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment(args.user)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        adapter = CharacterAdapter(emptyList(), 16f)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = adapter
    }


    private fun fetchCharacters() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val characters = networkApi.getCharacters()

                val charactersWithHomeworld = characters.map { character ->
                    val homeworldName = if (character.homeworld != null) {
                        networkApi.getHomeworldName(character.homeworld)
                    } else {
                        "Unknown"
                    }
                    character.copy(homeworld = homeworldName)
                }
                adapter.setData(charactersWithHomeworld)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching characters: ${e.message}")
            } finally {
            }
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
}
