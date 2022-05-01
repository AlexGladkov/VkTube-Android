package com.mobiledeveloper.vktube.ui.screens.subscriptions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.ignoreAllIconPaddingVert
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.ignoreIconPaddingHor
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.notAllIgnoredAlpha
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.nothingFoundFieldSize
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.nothingFoundTextSize
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.searchFieldPaddingHor
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.searchFieldPaddingVert
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.searchFieldWeight
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.searchTextSize
import com.mobiledeveloper.vktube.ui.screens.subscriptions.SubscriptionListParameters.spaceBetween
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionCellModel
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListAction
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListEvent
import com.mobiledeveloper.vktube.ui.screens.subscriptions.models.SubscriptionsListState
import com.mobiledeveloper.vktube.ui.theme.Fronton
import java.util.*

private object SubscriptionListParameters{
    const val spaceBetween = 8

    const val countGreyCells = 30

    const val searchFieldPaddingVert = 4
    const val searchFieldPaddingHor = 16
    const val searchFieldWeight = 1f

    const val nothingFoundFieldSize = 30
    const val nothingFoundTextSize = 20

    const val ignoreAllIconPaddingVert = 16
    const val ignoreIconPaddingHor = 16
    const val eyeIconSize = 18
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
    var itemsLocal by remember { mutableStateOf( viewState.items ) }
    var oldItems by remember { mutableStateOf( viewState.items ) }

    if (viewState.items != oldItems) {
        itemsLocal = viewState.items
        oldItems = viewState.items
    }

    val eyeAlpha = when (viewState.ignoreAll) {
        true -> ignoreAllAlpha
        else -> notAllIgnoredAlpha
    }

    val groupClick = fun (item: SubscriptionCellModel) {
        viewModel.obtainEvent(SubscriptionsListEvent.GroupClick(item))
    }

    val toggleAll = fun () {
        viewModel.obtainEvent(SubscriptionsListEvent.ToggleAll)
    }

    val search = fun (searchBy: String) {
        text = searchBy
        itemsLocal = viewState.items.filter {
            it.groupName.lowercase(Locale.getDefault())
                .contains(searchBy.lowercase(Locale.getDefault()))
        }
    }

    BackHandler(enabled = true){
        viewModel.obtainEvent(SubscriptionsListEvent.Back)
    }

    Column() {
        Row(modifier = Modifier
            .padding(vertical = spaceBetween.dp)
            .fillMaxWidth()
            .background(color = Fronton.color.backgroundPrimary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            OutlinedTextField(
                modifier = Modifier
                    .padding(
                        vertical = searchFieldPaddingVert.dp,
                        horizontal = searchFieldPaddingHor.dp
                    )
                    .weight(searchFieldWeight),
                label = { Text(LocalContext.current.resources.getString(R.string.search)) },
                value = text,
                onValueChange = { search(it) },
                textStyle = TextStyle(color = Fronton.color.textPrimary, fontSize = searchTextSize.sp),
            )
            Icon(
                modifier = Modifier
                    .padding(
                        horizontal = ignoreIconPaddingHor.dp,
                        vertical = ignoreAllIconPaddingVert.dp
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

        val nothingFoundFieldHeight = if (itemsLocal.isEmpty() && !viewState.loading) nothingFoundFieldSize else 0

        Text(
            text = LocalContext.current.resources.getString(R.string.nothing_found),
            modifier = Modifier
                .height(nothingFoundFieldHeight.dp)
                .fillMaxWidth(),
            style = TextStyle(color = Fronton.color.textPrimary, fontSize = nothingFoundTextSize.sp),
            textAlign = TextAlign.Center
        )

        Box(modifier = Modifier.background(color = Fronton.color.backgroundPrimary)) {
            SubscriptionsView(
                viewState = viewState.copy(items = itemsLocal),
                groupClick
            )}
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
         modifier = Modifier.padding(horizontal = ignoreIconPaddingHor.dp),
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