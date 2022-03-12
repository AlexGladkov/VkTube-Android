package com.mobiledeveloper.vktube.data.login

import com.vk.api.sdk.VK
import javax.inject.Inject

class LoginRepository @Inject constructor() {

    fun checkLogin(): Boolean {
        return VK.isLoggedIn()
    }
}