package com.mobiledeveloper.vktube.ui.common.cell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.eyeIconSize
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.eyeIgnoreAlpha
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.groupImageSize
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.ignoredAlpha
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.marginLR
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.marginUD
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.maxTextLines
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.textSize
import com.mobiledeveloper.vktube.ui.common.cell.SubscriptionListParameters.weight
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionCellModel
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.valentinilk.shimmer.shimmer

private object SubscriptionListParameters{
    const val groupImageSize = 50
    const val eyeIconSize = 15
    const val textSize = 15
    const val ignoredAlpha = 0.4f
    const val eyeIgnoreAlpha = 0.0f
    const val marginLR = 16
    const val marginUD = 4
    const val weight= 1f
    const val maxTextLines = 2
}

@Composable
fun SubscriptionCell(
    model: SubscriptionCellModel,
    groupClick: (SubscriptionCellModel) -> Unit
) {
        Row(modifier = Modifier
            .fillMaxWidth()) {
            SubscriptionDataView(model = model, groupClick)
        }
}


@Composable
private fun SubscriptionDataView(
    model: SubscriptionCellModel,
    groupClick: (SubscriptionCellModel) -> Unit
) {

    Row(
        modifier = Modifier
            .clickable {
                groupClick(model)
            }
            .padding(horizontal = marginLR.dp, vertical = marginUD.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {

        val alpha = when (model.isIgnored) {
            false -> 1f
            true -> ignoredAlpha
        }

        val eyeAlpha = when (model.isIgnored) {
            false -> 1f
            true -> eyeIgnoreAlpha
        }

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
                .padding(start = marginLR.dp, end = marginLR.dp)
                .weight(weight),
            text = model.groupName,
            color = Fronton.color.textPrimary,
            overflow = TextOverflow.Ellipsis,
            fontSize = textSize.sp,
            maxLines = maxTextLines,
        )
        Icon(
            modifier = Modifier
                .size(eyeIconSize.dp)
                .clip(CircleShape)
                .alpha(eyeAlpha),
            painter = painterResource(id = R.drawable.ic_eye),
            tint = Fronton.color.textPrimary,
            contentDescription = null
        )
    }
}

@Composable
fun SubscriptionGrayCell() {
        Row(modifier = Modifier.fillMaxWidth()) {
            SubscriptionGrayImageView()
        }
}

@Composable
private fun SubscriptionGrayImageView() {
    Box(
        modifier = Modifier
            .padding(horizontal = marginLR.dp, vertical = marginUD.dp)
            .background(Fronton.color.backgroundSecondary)
            .fillMaxWidth()
            .height(groupImageSize.dp)
            .shimmer()
    )
}
