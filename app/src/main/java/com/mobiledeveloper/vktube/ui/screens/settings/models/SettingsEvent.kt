package com.mobiledeveloper.vktube.ui.screens.settings.models

sealed class SettingsEvent {
    object ClearAction: SettingsEvent()
    object LogOut : SettingsEvent()
    object SubscribesScreen: SettingsEvent()
}