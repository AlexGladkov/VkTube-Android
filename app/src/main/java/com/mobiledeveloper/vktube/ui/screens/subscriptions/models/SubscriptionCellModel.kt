package com.mobiledeveloper.vktube.ui.screens.subscriptions.models

data class SubscriptionCellModel(
    val groupId: Long,
    val groupIcon: String,
    val groupName: String,
    val isIgnored: Boolean = false
)