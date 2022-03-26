package com.mobiledeveloper.vktube.ui.screens.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
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
        },
        onScroll = {
            feedViewModel.obtainEvent(FeedEvent.OnScroll(it))
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
private fun FeedView(
    viewState: FeedState, onVideoClick: (VideoCellModel) -> Unit,
    onScroll: (lastVisibleItemIndex: Int) -> Unit
) {
    val configuration = LocalConfiguration.current

    val imageHeight = remember {
        val screenWidth = configuration.screenWidthDp.dp
        ((screenWidth / 16) * 9)
    }
    if (viewState.loading) {
        LoadingView(imageHeight)
    } else {
        DataView(viewState, imageHeight, onVideoClick, onScroll)
    }
}

@Composable
private fun DataView(
    viewState: FeedState,
    imageHeight: Dp,
    onVideoClick: (VideoCellModel) -> Unit,
    onScroll: (lastVisibleItemIndex: Int) -> Unit
) {
    val state: LazyListState = rememberLazyListState()
    val lastVisibleItemIndex = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    LaunchedEffect(lastVisibleItemIndex) {
        onScroll(lastVisibleItemIndex)
    }
    LazyColumn(
        state = state
    ) {
        items(viewState.items) { viewModel ->
            VideoCell(viewModel, imageHeight) {
                onVideoClick.invoke(viewModel)
            }
        }
    }
}

@Composable
private fun LoadingView(imageHeight: Dp) {
    LazyColumn {
        repeat(10) {
            item {
                VideoGrayCell(imageHeight)
            }
        }
    }
}