package com.mobiledeveloper.vktube.ui.common.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.theme.Fronton

@Composable
fun VideoActionView(res: Int, title: String, isPressed: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick.invoke() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = res),
            contentDescription = stringResource(id = R.string.video_action_icon),
            tint = if (!isPressed) Fronton.color.controlPrimary else Fronton.color.controlAccent
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = title,
            style = Fronton.typography.minor.caption,
            color = Fronton.color.textPrimary
        )
    }
}