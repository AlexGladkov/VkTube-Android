package com.mobiledeveloper.vktube.ui.screens.subscriptions.models

sealed class SubscriptionsListEvent {
    object ScreenShown : SubscriptionsListEvent()
    object ClearAction : SubscriptionsListEvent()
    object Back : SubscriptionsListEvent()
    data class Add(val id: Long) : SubscriptionsListEvent()
    data class Remove(val id: Long) : SubscriptionsListEvent()
    data class ToggleIgnore(val item:SubscriptionCellModel) : SubscriptionsListEvent()
}