package com.mobiledeveloper.vktube.ui.common.cell

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.eyeIconSize
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.eyeIgnoreAlpha
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.ignoredAlpha
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.groupImageSize
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.maxTextLines
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.padding
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.textSize
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.weightEye
import com.mobiledeveloper.vktube.ui.common.cell.GroupCellParameters.weightGroupInfo
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
    const val groupImageSize = 50
    const val eyeIconSize = 15
    const val textSize = 15
    const val ignoredAlpha = 0.4f
    const val eyeIgnoreAlpha = 0.0f
    const val padding = 16
    const val weightGroupInfo = 10f
    const val weightEye = 1f
    const val maxTextLines = 2
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
fun GroupCell(model: GroupCellModel) {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Row(modifier = Modifier
            .fillMaxWidth()) {
            GroupDataView(model = model)
        }
    else {
        Column(modifier = Modifier
            .fillMaxWidth()) {
            GroupDataView(model = model)
        }
    }
}


@Composable
private fun GroupDataView(model: GroupCellModel) {

    val isChecked = remember {
        mutableStateOf(!model.isIgnored)
    }
    Row(
        modifier = Modifier
            .padding(all = padding.dp)
            .clickable {
                model.isIgnored = !model.isIgnored
                isChecked.value = !model.isIgnored
            }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        val alpha = when (isChecked.value){
            true -> 1f
            false -> ignoredAlpha
        }

        val eyeAlpha = when (isChecked.value){
            true -> 1f
            false -> eyeIgnoreAlpha
        }

        Row(
            modifier = Modifier.weight(weightGroupInfo),
            horizontalArrangement = Arrangement.Start
        )
        {
            AsyncImage(
                modifier = Modifier
                    .size(groupImageSize.dp)
                    .clip(CircleShape)
                    .alpha(alpha),
                model = model.groupIcon,
                contentDescription = stringResource(id = R.string.user_image_preview),
                contentScale = ContentScale.Crop
            )

            Text(
                modifier = Modifier
                    .alpha(alpha)
                    .padding(padding.dp),
                text = model.groupName,
                color = Fronton.color.textPrimary,
                overflow = TextOverflow.Ellipsis,
                fontSize = textSize.sp,
                maxLines = maxTextLines,
            )

        }

        Icon(
            modifier = Modifier
                .size(eyeIconSize.dp)
                .clip(CircleShape)
                .alpha(eyeAlpha)
                .weight(weightEye),
            painter = painterResource(id = R.drawable.ic_eye),
            contentDescription = null
        )
    }
}

@Composable
fun GroupGrayCell() {
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        Row(modifier = Modifier.fillMaxWidth()) {
            GroupGrayImageView()
        }
    else {
        Column {
            GroupGrayImageView()
        }
    }
}

@Composable
private fun GroupGrayImageView() {
    Box(
        modifier = Modifier
            .padding(all = padding.dp)
            .background(Fronton.color.backgroundSecondary)
            .fillMaxWidth()
            .height(groupImageSize.dp)
            .shimmer()
    )
}
