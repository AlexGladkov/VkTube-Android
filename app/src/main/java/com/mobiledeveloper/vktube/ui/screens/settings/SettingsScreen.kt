package com.mobiledeveloper.vktube.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.navigation.NavigationTree.POP_UP_TO_AUTH
import com.mobiledeveloper.vktube.ui.screens.settings.SettingsScreenParameters.heightItem
import com.mobiledeveloper.vktube.ui.screens.settings.SettingsScreenParameters.marginLeftText
import com.mobiledeveloper.vktube.ui.screens.settings.SettingsScreenParameters.paddingItems
import com.mobiledeveloper.vktube.ui.screens.settings.SettingsScreenParameters.sizeText
import com.mobiledeveloper.vktube.ui.screens.settings.SettingsScreenParameters.spaceBetweenItems
import com.mobiledeveloper.vktube.ui.screens.settings.models.SettingsAction
import com.mobiledeveloper.vktube.ui.screens.settings.models.SettingsEvent
import com.mobiledeveloper.vktube.ui.theme.Fronton

private object SettingsScreenParameters{
    const val spaceBetweenItems = 8
    const val marginLeftText = 16
    const val heightItem = 54
    const val sizeText = 18
    const val paddingItems = 16
}

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {

    val viewAction by settingsViewModel.viewActions().collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingItems.dp)
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(spaceBetweenItems.dp)
    ) {
        SettingsItem(modifier = Modifier,
            title = stringResource(id = R.string.logout_from_vk),
            onClick = { settingsViewModel.obtainEvent(SettingsEvent.LogOut) }
        )
        SettingsItem(modifier = Modifier,
            title = stringResource(id = R.string.subscribes),
            onClick = { settingsViewModel.obtainEvent(SettingsEvent.SubscriptionsScreen) }
        )
    }

    LaunchedEffect(key1 = viewAction, block = {
        when (viewAction) {
            is SettingsAction.NavigateLogin -> {
                navController.navigate(NavigationTree.Root.Auth.name) {
                    popUpTo(POP_UP_TO_AUTH)
                }
            }
            is SettingsAction.NavigateSubscribes -> {
                navController.navigate(NavigationTree.Root.SubscriptionsList.name)
            }
            null -> {
                // ignore
            }
        }
        settingsViewModel.obtainEvent(SettingsEvent.ClearAction)
    })
}


@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(heightItem.dp)
            .clickable {
                onClick()
            }
            .background(color = Fronton.color.controlPrimary)
            .padding(start = marginLeftText.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = Fronton.color.textInvert,
            fontSize = sizeText.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}