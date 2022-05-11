package com.mobiledeveloper.vktube.ui.screens.subscriptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.navigation.NavigationTree
import com.mobiledeveloper.vktube.ui.common.cell.*
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.ignoreAllAlpha
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.countGreyCells
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.eyeIconSize
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.eyeIconSizePaddingVert
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.notAllIgnoredAlpha
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.nothingFoundTextSize
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.searchTextSize
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.spaceBetween
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionCellModel
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListAction
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListEvent
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListState
import com.mobiledeveloper.vktube.ui.theme.Fronton

private object SubscriptionListParameters{
    const val spaceBetween = 8
    const val countGreyCells = 30
    const val nothingFoundTextSize = 20
    const val eyeIconSize = 24
    const val eyeIconSizePaddingVert = 16
    const val notAllIgnoredAlpha = 1f
    const val ignoreAllAlpha = 0.4f
    const val searchTextSize = 17
}

@Composable
fun SubscriptionsListScreen(
    navController: NavController,
    viewModel: SubscriptionsListViewModel,
) {
    val viewState by viewModel.viewStates().collectAsState()
    val viewAction by viewModel.viewActions().collectAsState(initial = null)

    var text by remember { mutableStateOf("") }

    val eyeAlpha = when (viewState.allAreIgnored) {
        true -> ignoreAllAlpha
        else -> notAllIgnoredAlpha
    }

    val groupClick = fun (item: SubscriptionCellModel) {
        viewModel.obtainEvent(SubscriptionsListEvent.GroupClicked(item))
    }

    val toggleAll = fun () {
        viewModel.obtainEvent(SubscriptionsListEvent.ToggleAllisClicked)
    }

    val search = fun (searchBy: String) {
        text = searchBy
        viewModel.obtainEvent(SubscriptionsListEvent.SearchTextChanged(searchBy))
    }

    BackHandler(enabled = true){
        viewModel.obtainEvent(SubscriptionsListEvent.Back)
    }

    Column(modifier = Modifier.fillMaxHeight()) {
        Row(modifier = Modifier
            .padding(vertical = spaceBetween.dp)
            .fillMaxWidth()
            .background(color = Fronton.color.backgroundPrimary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            Spacer(modifier = Modifier.width(eyeIconSize.dp + (2 * eyeIconSizePaddingVert).dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                BasicTextField(
                    modifier = Modifier
                        .border(
                            BorderStroke(1.dp, Fronton.color.textPrimary),
                            shape = RoundedCornerShape(percent = 10)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .fillMaxWidth(),
                    value = text,
                    onValueChange = { search(it) },
                    textStyle = TextStyle(color = Fronton.color.textPrimary, fontSize = searchTextSize.sp),
                )
                if(text.isBlank()){
                    Text(
                        text = LocalContext.current.resources.getString(R.string.search),
                        color = Fronton.color.textSecondary
                    )
                }
            }
            Icon(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = eyeIconSizePaddingVert.dp
                    )
                    .clickable {
                        toggleAll()
                    }
                    .size(eyeIconSize.dp)
                    .clip(CircleShape)
                    .alpha(eyeAlpha),
                painter = painterResource(id = com.mobiledeveloper.vktube.R.drawable.ic_ignore_all),
                tint = Fronton.color.textPrimary,
                contentDescription = null
            )
        }

        if (viewState.items.isEmpty() && !viewState.loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = LocalContext.current.resources.getString(R.string.nothing_found),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        color = Fronton.color.textSecondary,
                        fontSize = nothingFoundTextSize.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
        else {
            Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
                SubscriptionsView(
                    viewState = viewState,
                    groupClick
                )
            }
        }
    }

    LaunchedEffect(key1 = viewAction, block = {
        viewModel.obtainEvent(SubscriptionsListEvent.ClearAction)
        when (viewAction) {
            is SubscriptionsListAction.BackToFeed -> {
                navController.navigate(NavigationTree.Root.Main.name)
            }
            null -> {
                // ignore
            }
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        viewModel.obtainEvent(SubscriptionsListEvent.ScreenShown)
    })

}

@Composable
private fun SubscriptionsView(
    viewState: SubscriptionsListState,
    groupClick: (SubscriptionCellModel) -> Unit
) {
    if (viewState.loading) {
        LoadingView()
    } else {
        SubscriptionView(viewState, groupClick)
    }
}

@Composable
private fun SubscriptionView(
    viewState: SubscriptionsListState,
    groupClick: (SubscriptionCellModel) -> Unit
) {
     LazyColumn(
         modifier = Modifier.padding(horizontal = 16.dp),
         verticalArrangement = Arrangement.spacedBy(spaceBetween.dp)
     ) {
        items(
            items = viewState.items,
            key = { item -> item.groupId }
        ) { viewModel ->
            SubscriptionCell(viewModel, groupClick)
        }
    }
}

@Composable
private fun LoadingView() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(spaceBetween.dp)) {
        repeat(countGreyCells) {
            item {
                SubscriptionGrayCell()
            }
        }
    }
}