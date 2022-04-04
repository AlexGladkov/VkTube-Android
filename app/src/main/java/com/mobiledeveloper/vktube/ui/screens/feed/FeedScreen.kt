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
import com.mobiledeveloper.vktube.ui.screens.feed.FeedViewParameters.landscapeItemsPerScreen
import com.mobiledeveloper.vktube.ui.screens.feed.FeedViewParameters.ratio
import com.mobiledeveloper.vktube.ui.screens.feed.FeedViewParameters.space
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

object FeedViewParameters{
    const val ratio = 16 / 9
    const val landscapeItemsPerScreen = 3
    const val space = 4

}

@Composable
private fun FeedView(
    viewState: FeedState, onVideoClick: (VideoCellModel) -> Unit,
    onScroll: (lastVisibleItemIndex: Int) -> Unit
) {
    val configuration = LocalConfiguration.current

    val previewSize = remember(configuration.orientation) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val height = configuration.screenHeightDp.dp / landscapeItemsPerScreen
            Size(height * ratio, height)
        } else {
            Size(configuration.screenWidthDp.dp, configuration.screenWidthDp.dp / ratio)
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
    LazyColumn(verticalArrangement = Arrangement.spacedBy(space.dp), state = state) {
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
    LazyColumn(verticalArrangement = Arrangement.spacedBy(space.dp)) {
        repeat(10) {
            item {
                VideoGrayCell(previewSize)
            }
        }
    }
}