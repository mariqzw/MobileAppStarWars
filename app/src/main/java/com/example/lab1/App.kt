package com.example.lab1

import android.app.Application
import androidx.room.Room
import com.example.lab1.database.AppDatabase

class App : Application() {
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "starwars_database"
        ).build()
    }
}
