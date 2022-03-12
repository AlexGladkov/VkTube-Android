package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.VideoCell
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedAction
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedEvent
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedState

@Composable
fun FeedScreen(
    navController: NavController,
    feedViewModel: FeedViewModel
) {
    val viewState by feedViewModel.viewStates().observeAsState(FeedState())
    val viewAction by feedViewModel.viewEffects().observeAsState()

    FeedView(
        viewState = viewState,
        onVideoClick = {
            feedViewModel.obtainEvent(FeedEvent.VideoClicked(it))
        }
    )

    LaunchedEffect(key1 = viewAction, block = {
        when (viewAction) {
            is FeedAction.OpenVideoDetail -> {
                navController.navigate("${NavigationTree.Root.Detail.name}/${(viewAction as FeedAction.OpenVideoDetail).videoId}")
            }
        }

        feedViewModel.obtainEvent(FeedEvent.ClearAction)
    })

    LaunchedEffect(key1 = Unit, block = {
        feedViewModel.obtainEvent(FeedEvent.ScreenShown)
    })
}

@Composable
private fun FeedView(viewState: FeedState, onVideoClick: (VideoCellModel) -> Unit) {
    LazyColumn {
        viewState.items.forEach {
            item {
                VideoCell(it) {
                    onVideoClick.invoke(it)
                }
            }
        }
    }
}