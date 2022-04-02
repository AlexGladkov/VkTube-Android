package com.mobiledeveloper.vktube.ui.common.cell

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.valentinilk.shimmer.shimmer
import com.vk.sdk.api.groups.dto.GroupsGroupFull

data class GroupCellModel(
    val groupId: Long,
    val groupIcon: String,
    val groupName: String,
    var isIgnored: Boolean = false
)

fun GroupsGroupFull.mapToGroupCellModel(
    imageUrl: String,
    name: String,
    id: Long
): GroupCellModel {
    return GroupCellModel(
        groupId = id,
        groupIcon = imageUrl,
        groupName = name
    )
}

@Composable
fun GroupCell(model: GroupCellModel, previewSize: Size, onGroupClick: () -> Unit) {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { onGroupClick.invoke() }) {
            GroupDataView(model = model)
        }
    else {
        Column(modifier = Modifier.clickable { onGroupClick.invoke() }) {
            GroupDataView(model = model)
        }
    }
}


@Composable
private fun GroupDataView(model: GroupCellModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {

        AsyncImage(
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape),
            model = model.groupIcon,
            contentDescription = stringResource(id = R.string.user_image_preview),
            contentScale = ContentScale.Crop
        )

        Text(
                text = model.groupName,
                color = Fronton.color.textPrimary,
                overflow = TextOverflow.Ellipsis,
                fontSize = 20.sp
        )

        val isChecked = remember {
            mutableStateOf(!model.isIgnored)
        }

        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                model.isIgnored = !model.isIgnored
                isChecked.value = !model.isIgnored
            }
        )
    }
}

@Composable
fun GroupGrayCell(previewSize: Size) {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Row(modifier = Modifier.fillMaxWidth()) {
            GroupGrayImageView(previewSize)
            GroupGrayDataView()
        }
    else {
        Column {
            GroupGrayImageView(previewSize)
            GroupGrayDataView()
        }
    }
}

@Composable
private fun GroupGrayImageView(previewSize: Size) {
    Box(
        modifier = Modifier
            .background(Fronton.color.backgroundSecondary)
            .width(previewSize.width)
            .height(previewSize.height)
            .shimmer()
    )
}

@Composable
private fun GroupGrayDataView() {
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