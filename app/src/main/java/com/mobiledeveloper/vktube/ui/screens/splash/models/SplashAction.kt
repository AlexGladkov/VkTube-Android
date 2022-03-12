package com.mobiledeveloper.vktube.ui.screens.splash.models

sealed class SplashAction {
    object OpenMain : SplashAction()
    object OpenLogin : SplashAction()
}