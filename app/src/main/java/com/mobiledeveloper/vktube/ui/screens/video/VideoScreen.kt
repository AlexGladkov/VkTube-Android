package com.mobiledeveloper.vktube.ui.screens.video

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.extensions.VideoType
import com.mobiledeveloper.vktube.extensions.extractVideoType
import com.mobiledeveloper.vktube.ui.common.cell.VideoCellModel
import com.mobiledeveloper.vktube.ui.common.views.VideoActionView
import com.mobiledeveloper.vktube.ui.screens.comments.CommentCellModel
import com.mobiledeveloper.vktube.ui.screens.comments.CommentsScreen
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoAction
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoEvent
import com.mobiledeveloper.vktube.ui.screens.video.models.VideoViewState
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.mobiledeveloper.vktube.utils.DateUtil
import com.mobiledeveloper.vktube.utils.NumberUtil
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun VideoScreen(
    videoId: Long?,
    videoViewModel: VideoViewModel
) {
    val viewState by videoViewModel.viewStates().collectAsState()
    val viewAction by videoViewModel.viewActions().collectAsState(initial = null)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val videoHeight = (screenWidth / 16) * 9
    val bottomSheetPeekHeight = screenHeight - videoHeight

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    var bottomSheetHeight by remember { mutableStateOf(0.dp) }
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            CommentsScreen(viewState = viewState,
                onSendClick = {
                    videoViewModel.obtainEvent(VideoEvent.SendComment(it))
                }, onCloseClick = {

                })
        },
        sheetPeekHeight = bottomSheetHeight
    ) {
        VideoScreenView(viewState = viewState, onCommentsClick = {
            videoViewModel.obtainEvent(VideoEvent.CommentsClick)
        })
    }

    LaunchedEffect(key1 = viewAction, block = {
        when (viewAction) {
            VideoAction.OpenComments -> {
                coroutineScope.launch {
                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                        bottomSheetHeight = bottomSheetPeekHeight
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    } else {
                        bottomSheetHeight = 0.dp
                        bottomSheetScaffoldState.bottomSheetState.collapse()
                    }
                }
            }
        }

        videoViewModel.obtainEvent(VideoEvent.ClearAction)
    })

    LaunchedEffect(key1 = Unit, block = {
        videoViewModel.obtainEvent(VideoEvent.LaunchVideo(videoId))
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoScreenView(
    viewState: VideoViewState,
    onCommentsClick: () -> Unit
) {
    val context = LocalContext.current

    val video = viewState.video ?: return

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        stickyHeader {
            VideoPlayerView(video)
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

        val views = NumberUtil.formatNumberShort(video.viewsCount, context, R.plurals.number_short_format, R.plurals.views)
        val date = DateUtil.getTimeAgo(video.dateAdded,context)

        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                text = "$views • $date",
                color = Fronton.color.textSecondary,
                style = Fronton.typography.body.medium.short,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

        item {
            VideoActionsRow(
                video = video,
                onLikeClick = {

                },
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
            VideoCommentsView(viewState.comments, viewState, onCommentsClick)
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
            model = video.userImage,
            contentDescription = stringResource(id = R.string.comment_user_image_preview),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = video.userName,
                color = Fronton.color.textPrimary,
                style = Fronton.typography.body.medium.long
            )
            Text(
                text = video.subscribers,
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
    onCommentsClick: () -> Unit
) {
    val hasComments = comments.count() > 0

    Column(
        modifier = Modifier
            .clickable { onCommentsClick.invoke() }
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
            Row(modifier = Modifier.padding(top = 8.dp)) {
                AsyncImage(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    model = "https://sun1-30.userapi.com/s/v1/if1/HWVwYg9TvGZA1YCuBgOtSz3rb68518tAc8rH0SSdAdoGtsfF-YJ41XhlPJN0tmXhtryAjhGG.jpg?size=100x100&quality=96&crop=389,241,1069,1069&ava=1",
                    contentDescription = stringResource(id = R.string.comment_user_image_preview),
                    contentScale = ContentScale.Crop
                )

                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = "Если будет возможность через VPN выкладывай плиз и на ютуб. Я в Германии и буду смотреть. И таких как я очень много",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
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
private fun VideoPlayerView(video: VideoCellModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val videoHeight = (screenWidth / 16) * 9

    when (video.videoUrl.extractVideoType()) {
        VideoType.Vk -> VkVideoPlayer(url = video.videoUrl, height = videoHeight, width = screenWidth)
        VideoType.Youtube -> YoutubePlayer(url = video.videoUrl, height = videoHeight, width = screenWidth)
    }
}

@Composable
private fun VkVideoPlayer(url: String, width: Dp, height: Dp) {
    val widthPx = with(LocalDensity.current) { width.toPx() }
    val heightPx = with(LocalDensity.current) { height.toPx() }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        factory = {
            val dataUrl = "<html>" +
                    "<body>" +
                    "<iframe width=\"$widthPx\" height=\"$heightPx\" src=\"" + url + "\" frameborder=\"0\" allowfullscreen/>" +
                    "</body>" +
                    "</html>"

            WebView(it).apply {
                settings.javaScriptEnabled = true
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                loadData(dataUrl, "text/html", "utf-8")
            }
        })
}

@Composable
private fun YoutubePlayer(url: String, width: Dp, height: Dp) {
    val widthPx = with(LocalDensity.current) { width.toPx() }
    val heightPx = with(LocalDensity.current) { height.toPx() }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        factory = {
            val dataUrl = "<html>" +
                    "<body>" +
                    "<iframe width=\"$widthPx\" height=\"$heightPx\" src=\"" + url + "\" frameborder=\"0\" allowfullscreen/>" +
                    "</body>" +
                    "</html>"

            WebView(it).apply {
                settings.javaScriptEnabled = true
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                loadData(dataUrl, "text/html", "utf-8")
            }
        })
}

