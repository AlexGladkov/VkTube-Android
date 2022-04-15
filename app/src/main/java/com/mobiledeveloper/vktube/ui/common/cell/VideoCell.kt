package com.mobiledeveloper.vktube.ui.common.cell

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.mobiledeveloper.vktube.utils.DateUtil
import com.mobiledeveloper.vktube.utils.NumberUtil
import com.valentinilk.shimmer.shimmer
import com.vk.sdk.api.video.dto.VideoVideoFull

data class VideoCellGroupInfo(
    val id: Long,
    val userImage: String,
    val userName: String,
    val subscribers: Int,
)

fun VideoVideoFull.mapToVideoCellModel(
    groupOrder: Int,
    userImage: String,
    userName: String,
    subscribers: Int,
    dateUtil: DateUtil,
    numberUtil: NumberUtil,
): VideoCellModel? {
    val videoId = id ?: return null
    val ownerId = ownerId ?: return null

    val maxQualityImage = image?.reversed()?.firstOrNull()

    val groupInfo = VideoCellGroupInfo(
        id = ownerId.value,
        userImage = userImage,
        userName = userName,
        subscribers = subscribers
    )
    val dateAdded = addingDate ?: 0

    val viewsCount = views ?: 0

    return VideoCellModel(
        groupOrder = groupOrder,
        videoId = videoId.toLong(),
        title = title.orEmpty(),
        previewUrl = maxQualityImage?.url.orEmpty(),
        viewsCount = viewsCount,
        dateAdded = dateAdded,
        likes = likes?.count ?: 0,
        likesByMe = likes?.userLikes?.value == 1,
        videoUrl = player.orEmpty(),
        ownerId = ownerId.value,
        groupInfo = groupInfo,
        formattedVideoInfo = getFormattedVideoInfo(dateUtil, numberUtil, viewsCount, dateAdded, userName),
        videoText = getVideoText(dateUtil, numberUtil, viewsCount, dateAdded),
        subscribersText = getVideoSubscribers(numberUtil, subscribers),
        viewsCountFormatted = getViewsCountFormatted(numberUtil, viewsCount)
    )
}

fun getViewsCountFormatted(
    numberUtil: NumberUtil,
    viewsCount: Int
): String = numberUtil.formatNumberShort(
    number = viewsCount,
    idFormat = R.plurals.number_short_format,
    idDescriptor = R.plurals.views
)

private fun getFormattedVideoInfo(
    dateUtil: DateUtil,
    numberUtil: NumberUtil,
    viewsCount: Int,
    dateAdded: Int,
    userName: String
): String {
    val views = numberUtil.formatNumberShort(
        number = viewsCount,
        idFormat = R.plurals.number_short_format,
        idDescriptor = R.plurals.views
    )

    val date = dateUtil.getTimeAgo(dateAdded)
    return "$userName • $views • $date"
}

private fun getVideoText(
    dateUtil: DateUtil,
    numberUtil: NumberUtil,
    viewsCount: Int,
    dateAdded: Int,
): String {
    val views = numberUtil.formatNumberShort(
        number = viewsCount,
        idFormat = R.plurals.number_short_format,
        idDescriptor = R.plurals.views
    )
    val date = dateUtil.getTimeAgo(dateAdded)
    return "$views • $date"
}

private fun getVideoSubscribers(
    numberUtil: NumberUtil,
    subscribers: Int,
): String = numberUtil.formatNumberShort(
    number = subscribers,
    idFormat = R.plurals.number_short_format,
    idDescriptor = R.plurals.subscribers
)

@Composable
fun VideoCell(model: VideoCellModel, previewSize: Size, onVideoClick: () -> Unit) {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { onVideoClick.invoke() }) {
            VideoImageView(model.previewUrl, previewSize)
            VideoDataView(model = model)
        }
    else {
        Column(modifier = Modifier.clickable { onVideoClick.invoke() }) {
            VideoImageView(model.previewUrl, previewSize)
            VideoDataView(model = model)
        }
    }
}

@Composable
private fun VideoImageView(previewUrl: String, previewSize: Size) {
    AsyncImage(
        modifier = Modifier
            .width(previewSize.width)
            .height(previewSize.height),
        model = previewUrl,
        contentDescription = stringResource(id = R.string.video_preview),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun VideoDataView(model: VideoCellModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {

        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            model = model.groupInfo.userImage,
            contentDescription = stringResource(id = R.string.user_image_preview),
            contentScale = ContentScale.Crop
        )

        val text = remember(model.groupInfo.userName, model.dateAdded, model.viewsCount) {
            model.formattedVideoInfo
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = model.title,
                color = Fronton.color.textPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = text,
                color = Fronton.color.textSecondary,
                style = Fronton.typography.body.small.short
            )
        }
    }
}

@Composable
fun VideoGrayCell(previewSize: Size) {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Row(modifier = Modifier.fillMaxWidth()) {
            VideoGrayImageView(previewSize)
            VideoGrayDataView()
        }
    else {
        Column {
            VideoGrayImageView(previewSize)
            VideoGrayDataView()
        }
    }
}

@Composable
private fun VideoGrayImageView(previewSize: Size) {
    Box(
        modifier = Modifier
            .background(Fronton.color.backgroundSecondary)
            .width(previewSize.width)
            .height(previewSize.height)
            .shimmer()
    )
}

@Composable
private fun VideoGrayDataView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {

        Card(
            modifier = Modifier
                .size(40.dp)
                .shimmer(),
            elevation = 0.dp,
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Fronton.color.backgroundSecondary,
            content = {}
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .weight(1f)
        ) {
            Card(
                Modifier
                    .width(240.dp)
                    .height(24.dp)
                    .shimmer(),
                elevation = 0.dp,
                shape = RoundedCornerShape(4.dp),
                backgroundColor = Fronton.color.backgroundSecondary,
                content = {}
            )
            Card(
                Modifier
                    .padding(top = 4.dp)
                    .width(140.dp)
                    .height(20.dp)
                    .shimmer(),
                elevation = 0.dp,
                shape = RoundedCornerShape(4.dp),
                backgroundColor = Fronton.color.backgroundSecondary,
                content = {}
            )
        }
    }
}