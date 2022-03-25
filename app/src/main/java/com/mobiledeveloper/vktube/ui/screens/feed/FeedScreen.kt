package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.VideoCell
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.common.cell.VideoGrayCell
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedAction
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedEvent
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedState

@Composable
fun FeedScreen(
    navController: NavController,
    feedViewModel: FeedViewModel
) {
    val viewState by feedViewModel.viewStates().collectAsState()
    val viewAction by feedViewModel.viewActions().collectAsState(initial = null)

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
            null -> {
                // ignore
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
    if (viewState.items.isEmpty()) {
        LoadingView()
    } else {
        DataView(viewState, onVideoClick)
    }
}

@Composable
private fun DataView(viewState: FeedState, onVideoClick: (VideoCellModel) -> Unit) {
    LazyColumn {
        items(viewState.items) {
            VideoCell(it) {
                onVideoClick.invoke(it)
            }
        }
    }
}

@Composable
private fun LoadingView() {
    val configuration = LocalConfiguration.current

    val imageHeight = remember {
        val screenWidth = configuration.screenWidthDp.dp
        (screenWidth / 16) * 9
    }
    LazyColumn {
        repeat(10) {
            item {
                VideoGrayCell(imageHeight)
            }
        }
    }
}