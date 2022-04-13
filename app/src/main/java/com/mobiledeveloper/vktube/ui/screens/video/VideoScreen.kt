package com.mobiledeveloper.vktube.ui.screens.video

import android.content.Context
import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.common.views.VideoActionView
import com.mobiledeveloper.vktube.ui.screens.comments.CommentCellModel
import com.mobiledeveloper.vktube.ui.screens.comments.CommentsScreen
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoAction
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoEvent
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoViewState
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class
)
@Composable
fun VideoScreen(
    videoId: Long?,
    videoViewModel: VideoViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val viewState by videoViewModel.viewStates().collectAsState()
    val viewAction by videoViewModel.viewActions().collectAsState(initial = null)

    val configuration = LocalConfiguration.current

    val systemUiController = rememberSystemUiController()

    val bottomSheetPeekHeight = getBottomSheetPeekHeight(configuration)

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    var bottomSheetHeight by remember(configuration.orientation) { mutableStateOf(0.dp) }
    val coroutineScope = rememberCoroutineScope()

    val video = viewState.video

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
        if (video != null) {
            VideoPlayerView(
                modifier = Modifier
                    .background(color = Color.Black)
                    .pointerInteropFilter {
                        coroutineScope.launch {
                            delay(500)
                            setBarsVisible(systemUiController, false)
                        }

                        false
                    },
                getView = {
                    val view = videoViewModel.getWebView(it, video, onVideoLoading = {
                        videoViewModel.obtainEvent(VideoEvent.VideoLoading)
                    })
                    view
                },
                isLoadingVideo = viewState.isLoadingVideo
            )
        }
    } else {
        BottomSheetScaffold(
            modifier = Modifier.systemBarsPadding(),
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                CommentsScreen(viewState = viewState,
                    onSendClick = {
                        videoViewModel.obtainEvent(VideoEvent.SendComment(it))
                    }, onCloseClick = {
                        videoViewModel.obtainEvent(VideoEvent.CloseCommentsClick)
                    })
            },
            sheetPeekHeight = bottomSheetHeight
        ) {
            VideoScreenView(
                getView = { context, video ->
                    videoViewModel.getWebView(context, video, onVideoLoading = {
                        videoViewModel.obtainEvent(VideoEvent.VideoLoading)
                    })
                },
                viewState = viewState,
                onCommentsAvailable = {
                    videoViewModel.obtainEvent(VideoEvent.CommentsClick)
                },
                onLikeClick = {
                    videoViewModel.obtainEvent(VideoEvent.LikeClick)
                },
            )
        }
    }

    LaunchedEffect(key1 = viewAction, block = {
        videoViewModel.obtainEvent(VideoEvent.ClearAction)

        when (viewAction) {
            VideoAction.OpenComments -> {
                coroutineScope.launch {
                    bottomSheetHeight = bottomSheetPeekHeight
                    bottomSheetScaffoldState.bottomSheetState.expand()
                }
            }
            VideoAction.CloseComments -> {
                coroutineScope.launch {
                    bottomSheetHeight = 0.dp
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }
            null -> {
                // ignore
            }
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        videoViewModel.obtainEvent(VideoEvent.LaunchVideo(videoId))
    })

    LaunchedEffect(configuration.orientation) {
        coroutineScope.launch {
            val fullScreen = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            setBarsVisible(systemUiController, !fullScreen)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            setBarsVisible(systemUiController, true)
        }
    }
}

private fun getBottomSheetPeekHeight(configuration: Configuration): Dp {
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    return (screenHeight - (screenWidth / 16) * 9).coerceAtLeast(screenHeight / 2)
}

private fun setBarsVisible(systemUiController: SystemUiController, visible: Boolean) {
    systemUiController.isSystemBarsVisible = visible
    systemUiController.isStatusBarVisible = visible
    systemUiController.isNavigationBarVisible = visible
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoScreenView(
    getView: (context: Context, video: VideoCellModel) -> View,
    viewState: VideoViewState,
    onCommentsAvailable: () -> Unit,
    onLikeClick: () -> Unit
) {
    val video = viewState.video ?: return

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        stickyHeader {
            VideoPlayerView(
                getView = {
                    getView(it, video)
                },
                isLoadingVideo = viewState.isLoadingVideo
            )
        }

        item {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                text = video.title,
                style = Fronton.typography.body.large.long,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                text = video.videoText,
                color = Fronton.color.textSecondary,
                style = Fronton.typography.body.medium.short,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

        item {
            VideoActionsRow(
                video = video,
                onLikeClick = onLikeClick
            )
        }

        item {
            Divider(
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp), thickness = 1.dp,
                color = Fronton.color.controlMinor
            )
        }

        item {
            VideoUserRow(video)
        }

        item {
            Divider(
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp), thickness = 1.dp,
                color = Fronton.color.controlMinor
            )
        }

        item {
            VideoCommentsView(viewState.comments, viewState, onCommentsAvailable)
        }
    }
}

@Composable
private fun VideoActionsRow(
    video: VideoCellModel,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Fronton.color.backgroundPrimary)
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        VideoActionView(
            res = R.drawable.ic_baseline_thumb_up_24,
            title = video.likes.toString(),
            isPressed = video.likesByMe,
            onClick = onLikeClick
        )
    }
}

@Composable
private fun VideoUserRow(video: VideoCellModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            model = video.groupInfo.userImage,
            contentDescription = stringResource(id = R.string.comment_user_image_preview),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text =  video.groupInfo.userName,
                color = Fronton.color.textPrimary,
                style = Fronton.typography.body.medium.long
            )
            Text(
                text = video.subscribersText,
                color = Fronton.color.textSecondary,
                style = Fronton.typography.body.small.short
            )
        }
    }
}

@Composable
private fun VideoCommentsView(
    comments: List<CommentCellModel>,
    viewState: VideoViewState,
    onCommentsAvailable: () -> Unit
) {
    val hasComments = comments.count() > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 4.dp)
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.comments),
                color = Fronton.color.textPrimary,
                style = Fronton.typography.body.small.short
            )

            if (hasComments) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = comments.count().toString(),
                    color = Fronton.color.textPrimary,
                    style = Fronton.typography.body.small.short
                )
            }
        }

        if (hasComments) {
            onCommentsAvailable()
        } else {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    model = viewState.currentUser?.avatar.orEmpty(),
                    contentDescription = stringResource(id = R.string.comment_user_image_preview),
                    contentScale = ContentScale.Crop
                )

                TextField(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                        .height(48.dp),
                    value = stringResource(id = R.string.enter_comment_text),
                    onValueChange = { },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Fronton.color.backgroundSecondary,
                        textColor = Fronton.color.textPrimary,
                        unfocusedIndicatorColor = Fronton.color.backgroundSecondary,
                        focusedIndicatorColor = Fronton.color.backgroundSecondary
                    ),
                    textStyle = TextStyle(fontSize = 10.sp),
                    readOnly = true
                )
            }
        }
    }
}

@Composable
private fun VideoPlayerView(
    modifier: Modifier = Modifier,
    getView: (context: Context) -> View,
    isLoadingVideo: Boolean?
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val videoHeight = (screenWidth / 16) * 9
    val fullScreen = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLoadingVideo != true) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(videoHeight),
            factory = { context -> getView(context) }
        )
    } else {
        Box(
            modifier = modifier
                .background(if (fullScreen) Color.Black else Fronton.color.backgroundSecondary)
                .fillMaxWidth()
                .height(videoHeight)
                .shimmer()
        )
    }
}