package com.example.lab1.presentation.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab1.databinding.FragmentHomeBinding
import com.example.lab1.network.KtorNetwork
import com.example.lab1.presentation.adapter.CharacterAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var adapter: CharacterAdapter
    private val networkApi = KtorNetwork()

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

        binding.usernameHeader.text = args.username

        setupRecyclerView()

        fetchCharacters()

    }

    private fun setupRecyclerView() {
        adapter = CharacterAdapter(emptyList())
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


}
