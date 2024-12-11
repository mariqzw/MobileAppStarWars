package com.example.lab1.database

import com.example.lab1.network.KtorNetworkApi
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val api: KtorNetworkApi,
    private val dao: CharacterDao
) {
    private suspend fun getCharacters(orderNumber: Int, page: Int = 1): List<CharacterEntity> {
        return try {
            val characters = api.getCharacters(page)

            characters.map { character ->
                val homeworldName =
                    character.homeworld?.let { api.getHomeworldName(it) } ?: "Unknow"
                CharacterEntity(
                    name = character.name ?: "",
                    height = character.height,
                    mass = character.mass,
                    hair_color = character.hair_color,
                    eye_color = character.eye_color,
                    gender = character.gender,
                    homeworld = homeworldName
                )
            }
        } catch (e: Exception) {
            throw Exception("Error getting characters: ${e.message}, e")
        }
    }

    private suspend fun cacheCharacters(characters: List<CharacterEntity>) {
        dao.insertCharacters(characters)
    }

    private fun deleteCharacters() {
        dao.deleteAllCharacters()
    }

    fun getCharactersFlow(): Flow<List<CharacterEntity>> {
        return dao.getAllCharacters()
    }

    suspend fun refreshCharacters() {
        try {
            val characters = getCharacters(1)
            deleteCharacters()
            cacheCharacters(characters)
        } catch (e: Exception) {
            throw Exception("Error refresh characters: ${e.message}", e)
        }
    }
}