package com.mobiledeveloper.vktube.navigation

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobiledeveloper.vktube.ui.common.LocalSnackbarHostState
import com.mobiledeveloper.vktube.ui.screens.feed.FeedScreen
import com.mobiledeveloper.vktube.ui.screens.feed.FeedViewModel
import com.mobiledeveloper.vktube.ui.screens.login.LoginScreen
import com.mobiledeveloper.vktube.ui.screens.login.LoginViewModel
import com.mobiledeveloper.vktube.ui.screens.settings.SettingsScreen
import com.mobiledeveloper.vktube.ui.screens.settings.SettingsViewModel
import com.mobiledeveloper.vktube.ui.screens.splash.SplashScreen
import com.mobiledeveloper.vktube.ui.screens.splash.SplashViewModel
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionsListScreen
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionsListViewModel
import com.mobiledeveloper.vktube.ui.screens.video.VideoScreen
import com.mobiledeveloper.vktube.ui.screens.video.VideoViewModel

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState(
        snackbarHostState = remember { SnackbarHostState() }
    )
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { MainBottomNavigation(navController) }
    ) {
        CompositionLocalProvider(
            LocalSnackbarHostState provides scaffoldState.snackbarHostState
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationTree.Root.Splash.name
            ) {
                composable(NavigationTree.Root.Splash.name) {
                    val splashViewModel = hiltViewModel<SplashViewModel>()
                    SplashScreen(navController, splashViewModel)
                }
                composable(NavigationTree.Root.Auth.name) {
                    val loginViewModel = hiltViewModel<LoginViewModel>()
                    LoginScreen(navController)
                }
                composable(NavigationTree.Root.Main.name) {
                    val feedViewModel = hiltViewModel<FeedViewModel>()
                    FeedScreen(navController, feedViewModel)
                }
                composable(NavigationTree.Root.SubscriptionsList.name) {
                    val blackListViewModel = hiltViewModel<SubscriptionsListViewModel>()
                    SubscriptionsListScreen(navController, blackListViewModel)
                }

                composable(
                    "${NavigationTree.Root.Detail.name}/{videoId}",
                    arguments = listOf(navArgument("videoId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val param = backStackEntry.arguments?.getString("videoId")?.toLong()
                    val videoViewModel = hiltViewModel<VideoViewModel>()
                    VideoScreen(param, videoViewModel)
                }

                composable(NavigationTree.Root.Settings.name) {
                    val settingsViewModel = hiltViewModel<SettingsViewModel>()
                    SettingsScreen(settingsViewModel, navController)
                }
            }
        }
    }
}