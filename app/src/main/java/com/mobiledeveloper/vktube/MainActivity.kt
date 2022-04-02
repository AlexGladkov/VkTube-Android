package com.mobiledeveloper.vktube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mobiledeveloper.vktube.navigation.NavigationGraph
import com.mobiledeveloper.vktube.ui.theme.Fronton
import com.mobiledeveloper.vktube.ui.theme.FrontonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrontonTheme {
                val systemUiController = rememberSystemUiController()
                val backgroundPrimary = Fronton.color.backgroundPrimary
                val isLight = Fronton.color.isLight
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = backgroundPrimary,
                        darkIcons = isLight
                    )
                }

                // A surface container using the 'background' color from the theme
                NavigationGraph()
            }
        }
    }
}