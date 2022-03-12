package com.mobiledeveloper.vktube.ui.screens.login.models

import android.app.Activity

sealed class LoginEvent {
    data class LoginClicked(val activity: Activity) : LoginEvent()
}