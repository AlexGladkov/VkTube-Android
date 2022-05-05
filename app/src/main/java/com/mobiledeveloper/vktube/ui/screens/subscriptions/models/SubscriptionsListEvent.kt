package com.mobiledeveloper.vktube.ui.screens.subscriptions.models

sealed class SubscriptionsListEvent {
    object ScreenShown : SubscriptionsListEvent()
    object ClearAction : SubscriptionsListEvent()
    object Back : SubscriptionsListEvent()
    object ToggleAllisClicked : SubscriptionsListEvent()
    data class GroupClicked(val item: SubscriptionCellModel) : SubscriptionsListEvent()
    data class SearchTextChanged(val searchBy: String) : SubscriptionsListEvent()
}