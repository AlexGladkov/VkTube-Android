package com.mobiledeveloper.vktube.ui.screens.subscriptions.models

import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionCellModel
import javax.annotation.concurrent.Immutable

@Immutable
data class SubscriptionsListState(
    val items: List<SubscriptionCellModel> = emptyList(),
    val loading: Boolean = true
)