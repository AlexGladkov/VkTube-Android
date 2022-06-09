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

        private const val HISTORY_DB_NAME = "db_videos_history"

        fun getDatabase(context: Context): VideosDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                VideosDatabase::class.java,
                HISTORY_DB_NAME
            ).setJournalMode(JournalMode.TRUNCATE).build()
        }
    }
}
