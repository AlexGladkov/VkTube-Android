package com.mobiledeveloper.vktube.ui.screens.feed

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.Size
import com.mobiledeveloper.vktube.ui.common.cell.VideoCell
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.common.cell.VideoGrayCell
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedAction
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedEvent
import com.mobiledeveloper.vktube.ui.screens.feed.models.FeedState
import com.mobiledeveloper.vktube.ui.theme.Fronton

@Composable
fun FeedScreen(
    navController: NavController,
    feedViewModel: FeedViewModel
) {
    val viewState by feedViewModel.viewStates().collectAsState()
    val viewAction by feedViewModel.viewActions().collectAsState(initial = null)

    Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
        FeedView(
            viewState = viewState,
            onVideoClick = {
                feedViewModel.obtainEvent(FeedEvent.VideoClicked(it))
            },
        onScroll = {
            feedViewModel.obtainEvent(FeedEvent.OnScroll(it))
        }
    )}

    LaunchedEffect(key1 = viewAction, block = {
        feedViewModel.obtainEvent(FeedEvent.ClearAction)
        when (viewAction) {
            is FeedAction.OpenVideoDetail -> {
                navController.navigate("${NavigationTree.Root.Detail.name}/${(viewAction as FeedAction.OpenVideoDetail).videoId}")
            }
            null -> {
                // ignore
            }
        }
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

    val previewSize = remember(configuration.orientation) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val height = configuration.screenHeightDp.dp / 3
            Size(height * 16 / 9, height)
        } else {
            Size(configuration.screenWidthDp.dp + 1.dp, configuration.screenWidthDp.dp / 16 * 9)
        }
    }
    if (viewState.loading) {
        LoadingView(previewSize)
    } else {
        DataView(viewState, previewSize, onVideoClick, onScroll)
    }
}

@Composable
private fun DataView(
    viewState: FeedState,
    previewSize: Size,
    onVideoClick: (VideoCellModel) -> Unit,
    onScroll: (lastVisibleItemIndex: Int) -> Unit
) {
    val state: LazyListState = rememberLazyListState()
    val lastVisibleItemIndex = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    LaunchedEffect(lastVisibleItemIndex) {
        onScroll(lastVisibleItemIndex)
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), state = state) {
        items(
            items = viewState.items,
            key = { item -> item.id }
        ) { viewModel ->
            VideoCell(viewModel, previewSize) {
                onVideoClick.invoke(viewModel)
            }
        }
    }
}

@Composable
private fun LoadingView(previewSize: Size) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(10) {
            item {
                VideoGrayCell(previewSize)
            }
        }
    }
}