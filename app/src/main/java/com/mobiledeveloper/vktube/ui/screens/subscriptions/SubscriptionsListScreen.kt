package com.mobiledeveloper.vktube.ui.screens.subscriptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.*
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListAction
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListEvent
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListState
import com.mobiledeveloper.vktube.ui.theme.Fronton

@Composable
fun SubscriptionsListScreen(
    navController: NavController,
    viewModel: SubscriptionsListViewModel
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(initial = null)

    BackHandler(enabled = true){
        viewModel.obtainEvent(SubscriptionsListEvent.Back)
    }

    Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
        SubscriptionsView(
            viewState = viewState
    )}

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
    viewState: SubscriptionsListState
) {
    if (viewState.loading) {
        LoadingView()
    } else {
        SubscriptionView(viewState)
    }
}

@Composable
private fun SubscriptionView(
    viewState: SubscriptionsListState
) {
     LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(
            items = viewState.items,
            key = { item -> item.groupId }
        ) { viewModel ->
            SubscriptionCell(viewModel)
        }
    }
}

@Composable
private fun LoadingView() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(10) {
            item {
                SubscriptionGrayCell()
            }
        }
    }
}