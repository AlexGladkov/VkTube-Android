package com.mobiledeveloper.vktube.ui.screens.subscriptions.models

sealed class SubscriptionsListEvent {
    object ScreenShown : SubscriptionsListEvent()
    object ClearAction : SubscriptionsListEvent()
    object Back : SubscriptionsListEvent()
    data class GroupClick(val item:SubscriptionCellModel) : SubscriptionsListEvent()
}