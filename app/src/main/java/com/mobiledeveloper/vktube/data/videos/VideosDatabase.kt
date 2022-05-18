package com.mobiledeveloper.vktube.data.videos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobiledeveloper.vktube.R

@Database(entities = [VideoHistory::class], version = 1, exportSchema = false)
abstract class VideosDatabase : RoomDatabase() {

    abstract fun videosDao(): VideosDao

    companion object {

        const val HISTORY_DB_NAME = "db_videos_history"

        private var INSTANCE: VideosDatabase? = null

        fun getDatabase(context: Context): VideosDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideosDatabase::class.java,
                    HISTORY_DB_NAME
                ).setJournalMode(JournalMode.TRUNCATE).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}