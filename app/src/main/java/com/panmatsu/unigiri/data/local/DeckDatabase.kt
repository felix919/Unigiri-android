package com.panmatsu.unigiri.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [DeckEntity::class, DeckCardEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class DeckDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao

    companion object {
        @Volatile
        private var INSTANCE: DeckDatabase? = null

        fun getInstance(context: Context): DeckDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DeckDatabase::class.java,
                    "unigiri.db"
                ).build().also { INSTANCE = it }
            }
    }
}
