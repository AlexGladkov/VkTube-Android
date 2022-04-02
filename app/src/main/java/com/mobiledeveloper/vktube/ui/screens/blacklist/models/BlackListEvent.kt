package com.mobiledeveloper.vktube.ui.screens.blacklist.models

sealed class BlackListEvent {
    object ScreenShown : BlackListEvent()
    object ClearAction : BlackListEvent()
    object Back : BlackListEvent()
}