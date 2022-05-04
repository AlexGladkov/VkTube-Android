package com.mobiledeveloper.vktube

import android.util.Log
import com.mobiledeveloper.vktube.data.videos.VideosRepository
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellGroupInfo
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import javax.inject.Inject

@HiltAndroidTest
class VideoRepositoryTest {

    private val tag = "Test_tag"

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var name = TestName()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Inject
    lateinit var videoRepository: VideosRepository

    @Test
    fun testSaveAndLoad() {
        Log.d(tag, name.methodName)

        videoRepository.clearVideos()

        val video1 = VideoCellModel(
            videoId = 1,
            title = "1",
            previewUrl = "url1",
            viewsCount = 1,
            dateAdded = 1,
            likes = 1,
            likesByMe = false,
            videoUrl = "1",
            ownerId = 1L,
            groupInfo = VideoCellGroupInfo(1L, "1", "1", 1),
            groupOrder = 1
        )

        val video2 = VideoCellModel(
            videoId = 2,
            title = "2",
            previewUrl = "url2",
            viewsCount = 2,
            dateAdded = 2,
            likes = 2,
            likesByMe = false,
            videoUrl = "2",
            ownerId = 2L,
            groupInfo = VideoCellGroupInfo(2L, "2", "2", 2),
            groupOrder = 2
        )

        videoRepository.saveVideo(video1)
        videoRepository.saveVideo(video2)

        val videos = videoRepository.loadVideos()

        Log.d(tag, videos.joinToString(separator = ",", transform = { it.videoId.toString() }))

        assert(videos.size == 2)
    }

}
