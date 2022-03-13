package com.mobiledeveloper.vktube.ui.common.cell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import coil.compose.AsyncImage
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.vk.sdk.api.video.dto.VideoVideoFull

data class VideoCellModel(
    val videoId: Long, val subscribers: String,
    val title: String, val previewUrl: String, val userImage: String, val userName: String,
    val viewsCount: String, val dateAdded: String,
    val likes: Int, val likesByMe: Boolean
)

fun VideoVideoFull.mapToVideoCellModel(userImage: String, userName: String): VideoCellModel? {
    val videoId = id ?: return null

    val maxQualityImage = image?.reversed()?.firstOrNull()

    return VideoCellModel(
        videoId = videoId.toLong(),
        title = title.orEmpty(),
        previewUrl = maxQualityImage?.url.orEmpty(),
        userImage = "https://sun1-24.userapi.com/s/v1/ig2/Tmi9K7MVPybKuTwNCNg4atl5GcmntL2W6YrLLrTHsPJdQKieSp5NGzskGfct5ks7uzy9vYybLOS4eoCXpN-icYCw.jpg?size=200x200&quality=95&crop=0,0,1051,1051&ava=1",
        userName = "Мобильный разработчик",
        viewsCount = "${views ?: 0} просмотров",
        dateAdded = "1 час назад",
        subscribers = "1.2 тыс подписчиков",
        likes = likes?.count ?: 0,
        likesByMe = likes?.userLikes?.value == 1
    )
}

@Composable
fun VideoCell(model: VideoCellModel, onVideoClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onVideoClick.invoke() }) {
        val configuration = LocalConfiguration.current

        val screenWidth = configuration.screenWidthDp.dp
        val imageHeight = (screenWidth / 16) * 9

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
                modifier = Modifier.size(40.dp).clip(CircleShape),
                model = model.userImage,
                contentDescription = stringResource(id = R.string.user_image_preview),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp).weight(1f)) {
                Text(
                    text = model.title,
                    color = Fronton.color.textPrimary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = "${model.userName} • ${model.viewsCount} • ${model.dateAdded}",
                    color = Fronton.color.textSecondary,
                    style = Fronton.typography.body.small.short
                )
            }
        }
    }
}