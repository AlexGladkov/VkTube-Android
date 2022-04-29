package com.mobiledeveloper.vktube.ui.screens.subscriptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.*
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.ignoreAllAlpha
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.countGreyCells
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.eyeIconSize
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.ignoreAllIconPaddingVert
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.margin
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.notAllIgnoredAlpha
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.spaceBetween
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionCellModel
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListAction
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListEvent
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListState
import com.mobiledeveloper.vktube.ui.theme.Fronton


private object SubscriptionListParameters{
    const val spaceBetween = 8
    const val margin = 16
    const val countGreyCells = 30

    const val ignoreAllIconPaddingVert = 16
    const val eyeIconSize = 18
    const val notAllIgnoredAlpha = 1f
    const val ignoreAllAlpha = 0.4f
}

@Composable
fun SubscriptionsListScreen(
    navController: NavController,
    viewModel: SubscriptionsListViewModel
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(initial = null)

    val eyeAlpha = when (viewState.ignoreAll) {
        true -> ignoreAllAlpha
        else -> notAllIgnoredAlpha
    }

    val groupClick = fun (item: SubscriptionCellModel) {
        viewModel.obtainEvent(SubscriptionsListEvent.GroupClick(item))
    }

    val toggleAll = fun () {
        viewModel.obtainEvent(SubscriptionsListEvent.ToggleAll)
    }

    BackHandler(enabled = true){
        viewModel.obtainEvent(SubscriptionsListEvent.Back)
    }

    Column() {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(color = Fronton.color.backgroundPrimary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            Icon(
                modifier = Modifier
                    .padding(
                        end = margin.dp,
                        bottom = ignoreAllIconPaddingVert.dp,
                        top = ignoreAllIconPaddingVert.dp
                    )
                    .clickable {
                        toggleAll()
                    }
                    .size(eyeIconSize.dp)
                    .clip(CircleShape)
                    .alpha(eyeAlpha),
                painter = painterResource(id = com.mobiledeveloper.vktube.R.drawable.ic_ignore_all),
                tint = Fronton.color.textPrimary,
                contentDescription = null
            )
        }

        Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
            SubscriptionsView(
                viewState = viewState,
                groupClick
            )}
    }

    LaunchedEffect(key1 = viewAction, block = {
        viewModel.obtainEvent(SubscriptionsListEvent.ClearAction)
        when (viewAction) {
            is SubscriptionsListAction.BackToFeed -> {
                navController.navigate(NavigationTree.Root.Main.name)
            }
            null -> {
                // ignore
            }
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        viewModel.obtainEvent(SubscriptionsListEvent.ScreenShown)
    })

}

@Composable
private fun SubscriptionsView(
    viewState: SubscriptionsListState,
    groupClick: (SubscriptionCellModel) -> Unit
) {
    if (viewState.loading) {
        LoadingView()
    } else {
        SubscriptionView(viewState, groupClick)
    }
}

@Composable
private fun SubscriptionView(
    viewState: SubscriptionsListState,
    groupClick: (SubscriptionCellModel) -> Unit
) {
     LazyColumn(
         modifier = Modifier.padding(horizontal = margin.dp),
         verticalArrangement = Arrangement.spacedBy(spaceBetween.dp)
     ) {
        items(
            items = viewState.items,
            key = { item -> item.groupId }
        ) { viewModel ->
            SubscriptionCell(viewModel, groupClick)
        }
    }
}

@Composable
private fun LoadingView() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(spaceBetween.dp)) {
        repeat(countGreyCells) {
            item {
                SubscriptionGrayCell()
            }
        }
    }
}