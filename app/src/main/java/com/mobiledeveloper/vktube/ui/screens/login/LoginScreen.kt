package com.mobiledeveloper.vktube.ui.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.vk.api.sdk.VK.getVKAuthActivityResultContract
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope

@Composable
fun LoginScreen(
    navController: NavController,
) {
    val launcher = rememberLauncherForActivityResult(getVKAuthActivityResultContract()) { result ->
        when (result) {
            is VKAuthenticationResult.Failed -> {
                Log.e("TAG", "exception ${result.exception}")
            }
            is VKAuthenticationResult.Success -> {
                println("Token ${result.token.accessToken}")
                navController.navigate(NavigationTree.Root.Main.name)
            }
        }
    }
    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .background(Fronton.color.backgroundPrimary)
            .fillMaxSize()
    ) {

        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Fronton.color.controlPrimary
            ),
            onClick = {
                launcher.launch(setOf(VKScope.WALL, VKScope.VIDEO, VKScope.GROUPS))
            }
        ) {
            Text(text = stringResource(id = R.string.login_to_vk), color = Fronton.color.textInvert)
        }
    }
}