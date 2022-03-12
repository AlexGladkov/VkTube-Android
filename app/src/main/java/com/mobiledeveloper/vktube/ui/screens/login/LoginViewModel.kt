package com.mobiledeveloper.vktube.ui.screens.login

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.ui.screens.login.models.LoginAction
import com.mobiledeveloper.vktube.ui.screens.login.models.LoginEvent
import com.mobiledeveloper.vktube.ui.screens.login.models.LoginViewState
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope

class LoginViewModel : BaseViewModel<LoginViewState, LoginAction, LoginEvent>(initialState = LoginViewState()) {

    override fun obtainEvent(viewEvent: LoginEvent) {
        when (viewEvent) {
            is LoginEvent.LoginClicked -> performLoginClicked(viewEvent.activity)
        }
    }

    private fun performLoginClicked(activity: Activity) {
        VK.login(activity, arrayListOf(VKScope.WALL, VKScope.VIDEO))
    }
}