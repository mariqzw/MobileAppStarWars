package com.example.lab1.network

import android.util.Log
import com.example.lab1.models.Character
import com.example.lab1.models.Homeworld
import com.example.lab1.models.PeopleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import kotlin.time.Duration.Companion.seconds


interface KtorNetworkApi {
    suspend fun getCharacters(page: Int = 1): List<Character>
    suspend fun getHomeworldName(url: String): String
}

private const val NETWORK_BASE_URL = "swapi.dev"

class KtorNetwork : KtorNetworkApi {
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val client: HttpClient by lazy {
        HttpClient(engine = OkHttp.create()) {
            install(ContentNegotiation) { json(json) }

            install(HttpTimeout) {
                connectTimeoutMillis = 20.seconds.inWholeMilliseconds
                requestTimeoutMillis = 60.seconds.inWholeMilliseconds
                socketTimeoutMillis = 20.seconds.inWholeMilliseconds
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    override suspend fun getCharacters(page: Int): List<Character> {
        return try {
            client.get {
                url {
                    host = NETWORK_BASE_URL
                    protocol = URLProtocol.HTTPS
                    path("api/people")
                    contentType(ContentType.Application.Json)
                    parameter("page", page)
                }
            }.let { response ->
                Log.d("Ktor Response", response.body())
                val peopleResponse: PeopleResponse = response.body()
                peopleResponse.results
            }
        } catch (exception: Exception) {
            Log.e("Error", exception.message.toString())
            listOf()
        }
    }


    override suspend fun getHomeworldName(url: String): String {
        return try {
            client.get { url(url) }.let { response ->
                val planet: Homeworld = response.body()
                planet.name ?: "Unknown"
            }
        } catch (exception: Exception) {
            "Unknown"
        }
    }
}
