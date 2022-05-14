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
        @Volatile
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
                    context.getString(R.string.db_videos_history_name)
                ).setJournalMode(JournalMode.TRUNCATE).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}