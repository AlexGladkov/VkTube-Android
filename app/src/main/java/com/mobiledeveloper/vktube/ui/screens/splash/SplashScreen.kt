package com.mobiledeveloper.vktube.ui.screens.splash

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.screens.splash.models.SplashAction
import com.mobiledeveloper.vktube.ui.screens.splash.models.SplashEvent

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel
) {
    val viewAction by splashViewModel.viewEffects().observeAsState()

    LaunchedEffect(key1 = viewAction, block = {
        when (viewAction) {
            SplashAction.OpenLogin -> navController.navigate(NavigationTree.Root.Auth.name)
            SplashAction.OpenMain -> navController.navigate(NavigationTree.Root.Main.name)
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        splashViewModel.obtainEvent(SplashEvent.CheckLogin)
    })
}