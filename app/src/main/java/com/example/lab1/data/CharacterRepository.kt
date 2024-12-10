package com.example.lab1.data

import com.example.lab1.network.KtorNetworkApi
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val api: KtorNetworkApi,
    private val dao: CharacterDao
) {
    fun getAllCharacters(): Flow<List<CharacterEntity>> = dao.getAllCharacters()

    suspend fun refreshCharacters() {
        val characters = api.getCharacters().map { character ->
            CharacterEntity(
                name = character.name ?: "",
                height = character.height,
                mass = character.mass,
                hairColor = character.hair_color,
                eyeColor = character.eye_color,
                gender = character.gender,
                homeworld = api.getHomeworldName(character.homeworld ?: "")
            )
        }
        dao.deleteAllCharacters()
        dao.insertCharacters(characters)
    }
}