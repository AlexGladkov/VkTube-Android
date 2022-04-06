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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.navigation.NavigationTree.POP_UP_TO_AUTH
import com.mobiledeveloper.vktube.ui.screens.settings.models.SettingsAction
import com.mobiledeveloper.vktube.ui.screens.settings.models.SettingsEvent
import com.mobiledeveloper.vktube.ui.theme.Fronton


@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    val viewAction by settingsViewModel.viewActions().collectAsState(initial = null)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        SettingsItem(modifier = Modifier,
            title = stringResource(id = R.string.logout_from_vk),
            onClick = { settingsViewModel.obtainEvent(SettingsEvent.LogOut) }
        )
        SettingsItem(modifier = Modifier,
            title = stringResource(id = R.string.subscribes),
            onClick = { settingsViewModel.obtainEvent(SettingsEvent.SubscribesScreen) }
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
            .height(54.dp)
            .clickable {
                onClick()
            }
            .background(color=Fronton.color.backgroundSecondary)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Icon(
//            modifier = Modifier.size(24.dp),
//            tint = AppTheme.colors.secondaryVariant,
//            painter = icon,
//            contentDescription = null
//        )
        Text(
            text = title,
            color = Fronton.color.textPrimary,
            modifier = Modifier.padding(start = 16.dp),
            maxLines = 1,
        )
    }
}