package com.mobiledeveloper.vktube.ui.screens.feed

import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel

internal class VideoCellModelComparator : Comparator<VideoCellModel> {
    override fun compare(m0: VideoCellModel, m1: VideoCellModel): Int {
        return if (m0.groupInfo.id == m1.groupInfo.id) {
            m0.groupOrder.compareTo(m1.groupInfo.id)
        } else {
            -m0.dateAdded.compareTo(m1.dateAdded)
        }
    }

}