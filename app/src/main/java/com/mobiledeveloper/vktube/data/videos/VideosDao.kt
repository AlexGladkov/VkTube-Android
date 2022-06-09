package com.mobiledeveloper.vktube.data.videos

import androidx.room.*

@Dao
interface VideosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addVideo(videoHistory: VideoHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideos(list: List<VideoHistory>)

    @Update
    fun updateVideo(videoHistory: VideoHistory)

    @Delete
    fun deleteVideo(videoHistory: VideoHistory)

    @Query("DELETE FROM VideoHistory")
    fun resetTable( )

    @Query("SELECT * FROM VideoHistory Where VideoHistory.videoId Like :id")
    fun getVideoById(id: Int): VideoHistory

    @Query("SELECT * FROM VideoHistory")
    fun getVideos(): List<VideoHistory>

}