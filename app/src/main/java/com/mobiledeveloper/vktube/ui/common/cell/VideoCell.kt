package com.mobiledeveloper.vktube.ui.common.cell

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.mobiledeveloper.vktube.utils.DateUtil
import com.mobiledeveloper.vktube.utils.NumberUtil
import com.valentinilk.shimmer.shimmer
import com.vk.sdk.api.video.dto.VideoVideoFull

data class VideoCellModel(
    val videoId: Long, val subscribers: Int,
    val title: String, val previewUrl: String, val userImage: String, val userName: String,
    val viewsCount: Int, val dateAdded: Int,
    val likes: Int, val likesByMe: Boolean, val videoUrl: String, val ownerId: Long
)

fun VideoVideoFull.mapToVideoCellModel(
    userImage: String,
    userName: String,
    subscribers: Int
): VideoCellModel? {
    val videoId = id ?: return null
    val ownerId = ownerId ?: return null

    val maxQualityImage = image?.reversed()?.firstOrNull()


    return VideoCellModel(
        videoId = videoId.toLong(),
        title = title.orEmpty(),
        previewUrl = maxQualityImage?.url.orEmpty(),
        userImage = userImage,
        userName = userName,
        viewsCount = views ?: 0,
        dateAdded = addingDate ?: 0,
        subscribers = subscribers,
        likes = likes?.count ?: 0,
        likesByMe = likes?.userLikes?.value == 1,
        videoUrl = player.orEmpty(),
        ownerId = ownerId.value
    )
}

@Composable
fun VideoCell(model: VideoCellModel, onVideoClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onVideoClick.invoke() }) {
        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp
        val imageHeight = (screenWidth / 16) * 9

        val context = LocalContext.current

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            model = model.previewUrl,
            contentDescription = stringResource(id = R.string.video_preview),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {

            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                model = model.userImage,
                contentDescription = stringResource(id = R.string.user_image_preview),
                contentScale = ContentScale.Crop
            )

            val text = remember(model.userName, model.dateAdded, model.viewsCount) {
                val views =
                    NumberUtil.formatNumberShort(
                        model.viewsCount,
                        context,
                        R.plurals.number_short_format,
                        R.plurals.views
                    )

                val date = DateUtil.getTimeAgo(model.dateAdded, context)
                "${model.userName} • $views • $date"
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
}

@Composable
fun VideoGrayCell(imageHeight: Dp) {
    Column {
        Box(
            modifier = Modifier
                .background(Fronton.color.backgroundSecondary)
                .fillMaxWidth()
                .height(imageHeight)
                .shimmer()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
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
}