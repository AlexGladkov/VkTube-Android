package com.mobiledeveloper.vktube.ui.screens.login

import com.mobiledeveloper.vktube.base.BaseViewModel
import com.mobiledeveloper.vktube.ui.screens.login.models.LoginAction
import com.mobiledeveloper.vktube.ui.screens.login.models.LoginEvent
import com.mobiledeveloper.vktube.ui.screens.login.models.LoginViewState

class LoginViewModel :
    BaseViewModel<LoginViewState, LoginAction, LoginEvent>(initialState = LoginViewState()) {

    override fun obtainEvent(viewEvent: LoginEvent) {
    }
}
