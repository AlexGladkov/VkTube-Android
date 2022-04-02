package com.mobiledeveloper.vktube.ui.screens.blacklist.models

import com.mobiledeveloper.vktube.ui.common.cell.GroupCellModel
import javax.annotation.concurrent.Immutable

@Immutable
data class BlackListState(
    val items: List<GroupCellModel> = emptyList(),
    val loading: Boolean = true
)