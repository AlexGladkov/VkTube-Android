package com.mobiledeveloper.vktube.ui.screens.subscriptions.models

import javax.annotation.concurrent.Immutable

@Immutable
data class SubscriptionsListState(
    val items: List<SubscriptionCellModel> = emptyList(),
    val allAreIgnored: Boolean = true,
    val loading: Boolean = true
)