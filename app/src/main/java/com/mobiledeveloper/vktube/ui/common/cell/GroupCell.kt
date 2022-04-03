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
import androidx.compose.ui.draw.alpha
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
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.ignoredAlpha
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.imageSize
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.padding
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.textSize
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.valentinilk.shimmer.shimmer
import com.vk.sdk.api.groups.dto.GroupsGroupFull

data class GroupCellModel(
    val groupId: Long,
    val groupIcon: String,
    val groupName: String,
    var isIgnored: Boolean = false
)

object GroupCellParameters{
    const val imageSize = 75
    const val textSize = 20
    const val ignoredAlpha = 0.4f
    const val padding = 16
}

fun GroupsGroupFull.mapToGroupCellModel(
    imageUrl: String,
    name: String,
    id: Long,
    isIgnored: Boolean
): GroupCellModel {
    return GroupCellModel(
        groupId = id,
        groupIcon = imageUrl,
        groupName = name,
        isIgnored = isIgnored
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
            .padding(all = padding.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
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

        val alpha = when (isChecked.value){
            true -> 1f
            false -> ignoredAlpha
        }

        AsyncImage(
            modifier = Modifier
                .size(imageSize.dp)
                .clip(CircleShape)
                .alpha(alpha),
            model = model.groupIcon,
            contentDescription = stringResource(id = R.string.user_image_preview),
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier
                .alpha(alpha)
                .padding(16.dp),
                text = model.groupName,
                color = Fronton.color.textPrimary,
                overflow = TextOverflow.Ellipsis,
                fontSize = textSize.sp,
        )

    }
}

@Composable
fun GroupGrayCell(previewSize: Size) {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Row(modifier = Modifier.fillMaxWidth()) {
            GroupGrayImageView(previewSize)
        }
    else {
        Column {
            GroupGrayImageView(previewSize)
        }
    }
}

@Composable
private fun GroupGrayImageView(previewSize: Size) {
    Box(
        modifier = Modifier
            .padding(all = padding.dp)
            .background(Fronton.color.backgroundSecondary)
            .width(previewSize.width)
            .height(imageSize.dp)
            .shimmer()
    )
}
