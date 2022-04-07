package com.mobiledeveloper.vktube.ui.screens.subscriptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.*
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.countGreyCells
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.margin
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
}

@Composable
fun SubscriptionsListScreen(
    navController: NavController,
    viewModel: SubscriptionsListViewModel
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(initial = null)

    val groupClick = fun (item: SubscriptionCellModel) {
        viewModel.obtainEvent(SubscriptionsListEvent.GroupClick(item))
    }

    BackHandler(enabled = true){
        viewModel.obtainEvent(SubscriptionsListEvent.Back)
    }

    Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
        SubscriptionsView(
            viewState = viewState,
            groupClick
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