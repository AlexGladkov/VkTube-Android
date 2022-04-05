package com.mobiledeveloper.vktube.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.screens.settings.models.SettingsAction
import com.mobiledeveloper.vktube.ui.screens.settings.models.SettingsEvent
import com.mobiledeveloper.vktube.ui.theme.Fronton

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    val viewAction by settingsViewModel.viewActions().collectAsState(initial = null)

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = ButtonDefaults.buttonColors(backgroundColor = Fronton.color.controlPrimary),
        onClick = { settingsViewModel.obtainEvent(SettingsEvent.LogOut) }
    ) {
        Text(text = stringResource(id = R.string.logout_from_vk), color = Fronton.color.textInvert)
    }

    LaunchedEffect(key1 = viewAction, block = {
        when (viewAction) {
            is SettingsAction.NavigateLogin -> {
                navController.navigate(NavigationTree.Root.Auth.name) {
                    popUpTo(0)
                }
            }
        }
        settingsViewModel.obtainEvent(SettingsEvent.ClearAction)
    })
}