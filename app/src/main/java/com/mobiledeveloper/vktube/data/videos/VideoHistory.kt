package com.mobiledeveloper.vktube.data.videos

import com.mobiledeveloper.vktube.ui.common.cell.VideoCellGroupInfo
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoHistory(
    @PrimaryKey
    val videoId: Long,
    val title: String,
    val previewUrl: String,
    val viewsCount: Int,
    val dateAdded: Int,
    val likes: Int,
    val likesByMe: Boolean,
    val videoUrl: String,
    val ownerId: Long,
    val groupOrder: Int,
    val groupId: Long,
    val groupUserImage: String,
    val groupUserName: String,
    val groupSubscribers: Int
){


    companion object {
        fun fromVideoCellModel(videoCellModel: VideoCellModel): VideoHistory = VideoHistory(
            videoId = videoCellModel.videoId,
            title = videoCellModel.title,
            previewUrl = videoCellModel.previewUrl,
            viewsCount = videoCellModel.viewsCount,
            dateAdded = videoCellModel.dateAdded,
            likes = videoCellModel.likes,
            likesByMe = videoCellModel.likesByMe,
            videoUrl = videoCellModel.videoUrl,
            ownerId = videoCellModel.ownerId,
            groupId = videoCellModel.groupInfo.id,
            groupUserImage = videoCellModel.groupInfo.userImage,
            groupUserName = videoCellModel.groupInfo.userName,
            groupSubscribers = videoCellModel.groupInfo.subscribers,
            groupOrder = videoCellModel.groupOrder
        )
    }

    fun toVideoCellModel(): VideoCellModel = VideoCellModel(
        videoId = this.videoId,
        title = this.title,
        previewUrl = this.previewUrl,
        viewsCount = this.viewsCount,
        dateAdded = this.dateAdded,
        likes = this.likes,
        likesByMe = this.likesByMe,
        videoUrl = this.videoUrl,
        ownerId = this.ownerId,
        groupInfo = VideoCellGroupInfo(
            id = this.groupId,
            userImage = this.groupUserImage,
            userName = this.groupUserName,
            subscribers = this.groupSubscribers),
        groupOrder = this.groupOrder)
}