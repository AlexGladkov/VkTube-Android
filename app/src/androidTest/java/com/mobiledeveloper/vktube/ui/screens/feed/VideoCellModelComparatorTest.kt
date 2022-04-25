package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellGroupInfo
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class VideoCellModelComparatorTest {
    private val videoCellModelComparator = VideoCellModelComparator()

    @Test
    fun contractCheckSameGroup() {
        val cellModelA = emptyVideoCellModel()
        val cellModelB = cellModelA.copy(
            groupOrder = cellModelA.groupOrder + 1
        )
        val cellModelC = cellModelB.copy(
            groupOrder = cellModelB.groupOrder + 1
        )
        val cellModelD = emptyVideoCellModel()
        assertEquals(
            videoCellModelComparator.compare(cellModelA, cellModelB),
            -1 * videoCellModelComparator.compare(cellModelB, cellModelA)
        )
        assertEquals(
            videoCellModelComparator.compare(cellModelB, cellModelC),
            -1 * videoCellModelComparator.compare(cellModelC, cellModelB)
        )
        assertEquals(
            videoCellModelComparator.compare(cellModelA, cellModelC),
            -1 * videoCellModelComparator.compare(cellModelC, cellModelA)
        )
        assertEquals(
            videoCellModelComparator.compare(cellModelA, cellModelD),
            -1 * videoCellModelComparator.compare(cellModelD, cellModelA)
        )
    }

    @Test
    fun contractCheckDifferentGroup() {
        val cellModelA = emptyVideoCellModel()
        val cellModelB = cellModelA.copy(
            dateAdded = cellModelA.dateAdded + 1,
            groupInfo = cellModelA.groupInfo.copy(id = cellModelA.groupInfo.id + 1)
        )
        val cellModelC = cellModelB.copy(
            dateAdded = cellModelB.dateAdded + 1,
            groupInfo = cellModelB.groupInfo.copy(id = cellModelB.groupInfo.id + 1)
        )
        val cellModelD = cellModelA.copy(
            groupInfo = cellModelA.groupInfo.copy(id = cellModelA.groupInfo.id + 1)
        )
        assertEquals(
            videoCellModelComparator.compare(cellModelA, cellModelB),
            -1 * videoCellModelComparator.compare(cellModelB, cellModelA)
        )
        assertEquals(
            videoCellModelComparator.compare(cellModelB, cellModelC),
            -1 * videoCellModelComparator.compare(cellModelC, cellModelB)
        )
        assertEquals(
            videoCellModelComparator.compare(cellModelA, cellModelC),
            -1 * videoCellModelComparator.compare(cellModelC, cellModelA)
        )
        assertEquals(
            videoCellModelComparator.compare(cellModelA, cellModelD),
            -1 * videoCellModelComparator.compare(cellModelD, cellModelA)
        )
    }

    companion object {
        private fun emptyVideoCellModel(): VideoCellModel {
            return VideoCellModel(
                videoId = 0,
                title = "",
                previewUrl = "",
                viewsCount = 0,
                dateAdded = 0,
                likes = 0,
                likesByMe = false,
                videoUrl = "",
                ownerId = 0,
                groupInfo = VideoCellGroupInfo(0, "", "", 0),
                groupOrder = 0
            )
        }
    }
}