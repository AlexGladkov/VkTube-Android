package com.mobiledeveloper.vktube.ui.screens.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobiledeveloper.vktube.ui.screens.login.models.LoginEvent
import com.mobiledeveloper.vktube.ui.theme.Fronton
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.mobiledeveloper.vktube.R

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel
) {
    val activity = LocalContext.current as Activity

    Surface(
        modifier = Modifier
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
                loginViewModel.obtainEvent(LoginEvent.LoginClicked(activity))
            }
        ) {
            Text(text = stringResource(id = R.string.login_to_vk), color = Fronton.color.textInvert)
        }
    }
}