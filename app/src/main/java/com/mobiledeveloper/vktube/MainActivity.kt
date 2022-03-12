package com.mobiledeveloper.vktube

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mobiledeveloper.vktube.navigation.NavigationGraph
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.mobiledeveloper.vktube.ui.theme.FrontonTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.exceptions.VKAuthException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var vkAuthResultToken: MutableStateFlow<String> = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrontonTheme {
                val systemUiController = rememberSystemUiController()
                val backgroundPrimary = Fronton.color.backgroundPrimary

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = backgroundPrimary,
                        darkIcons = true
                    )
                }

                // A surface container using the 'background' color from the theme
                NavigationGraph()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                vkAuthResultToken.value = token.accessToken
            }

            override fun onLoginFailed(authException: VKAuthException) {
                Log.e("TAG", "exception ${authException.authError}")
            }
        }

        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}