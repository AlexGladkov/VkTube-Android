package com.mobiledeveloper.vktube.ui.screens.blacklist.models

import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel

sealed class BlackListEvent {
    object ScreenShown : BlackListEvent()
    object ClearAction : BlackListEvent()
    data class ClubsClicked(val videoCellModel: VideoCellModel) : BlackListEvent()
    data class OnScroll(val lastVisibleItemIndex: Int) : BlackListEvent()
}