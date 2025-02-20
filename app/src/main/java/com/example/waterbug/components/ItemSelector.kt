package com.example.waterbug.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.waterbug.R
import com.example.waterbug.appstate.Account
import com.example.waterbug.appstate.Asset
import com.example.waterbug.appstate.Network
import com.example.waterbug.appstate.accountList
import com.example.waterbug.appstate.assetList1
import com.example.waterbug.appstate.networkList
import com.example.waterbug.ui.theme.WaterbugTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ItemSelector(
    items: List<T>,
    onItemClick: (Int) -> Unit,
    activeItemIndex: Int,
//    navController: NavController? = null, // Nullable for cases without navigation
    onManageClick: (() -> Unit)? = null, // Nullable for cases without "Manage" button
    itemContent: @Composable (T, Boolean) -> Unit,
    triggerContent: @Composable (T?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val activeItem = items.getOrNull(activeItemIndex)

    LaunchedEffect(Unit) {
        showSheet = false
        sheetState.hide()
    }

    LaunchedEffect(showSheet) {
        if (showSheet) sheetState.show() else sheetState.hide()
    }

    Column {
        // Trigger Content (e.g., a Card, IconButton, etc.)
        Box(
            modifier = modifier
                .clickable { showSheet = true }
                .padding(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                    shape = MaterialTheme.shapes.extraSmall
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.extraSmall
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)) {
                Column(
                    modifier = Modifier.weight(1f, fill = false) // Content takes the remaining width
                ) {
                    triggerContent(activeItem)
                }
                Icon(
                    painter = painterResource(
                        id = R.drawable.baseline_arrow_drop_down_24,
                    ),
                    modifier = Modifier.alpha(0.5F).size(30.dp),
                    contentDescription = "arrow down"
                )
            }
        }

        // Modal Bottom Sheet
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                shape = MaterialTheme.shapes.large.copy(topStart = CornerSize(16.dp), topEnd = CornerSize(16.dp)) // Rounded top corners
            ) {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    itemsIndexed(items) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp) // Vertical spacing between items
                                .background(
                                    color = if (index == activeItemIndex) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable {
                                    onItemClick(index)
                                    scope.launch {
                                        sheetState.hide()
                                        showSheet = false
                                    }
                                }
                                .padding(12.dp), // Inner padding for the item
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemContent(item, index == activeItemIndex)
                        }
                    }
                }
                // Optional Manage Button
                if (onManageClick != null) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    TextButton(
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                showSheet = false
                            }
                            onManageClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Manage ${items.firstOrNull()?.let { it::class.simpleName } ?: "Item"}s",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountModalSelector(
    accounts: List<Account>,
    onAccountClick: (Int) -> Unit,
    activeAccountIndex: Int,
    navController: NavController
) {
    ItemSelector(
        items = accounts,
        onItemClick = onAccountClick,
        activeItemIndex = activeAccountIndex,
//        navController = navController,
        onManageClick = { navController.navigate("accounts") },
        itemContent = { account, isActive ->
            AccountCard(account = account, active = isActive)
        },
        triggerContent = { activeAccount ->
            AccountCardMini(account = activeAccount, active = true, modifier = Modifier.alpha(0.75f))
        },
        modifier = Modifier.height(50.dp)
    )
}

@Composable
fun AssetModalSelector(
    assets: List<Asset>,
    onAssetClick: (Int) -> Unit,
    activeAssetIndex: Int,
) {
    ItemSelector(
        items = assets,
        onItemClick = onAssetClick,
        activeItemIndex = activeAssetIndex,
        itemContent = { asset, isActive ->
            AssetCard(asset = asset, active = isActive)
        },
        triggerContent = { activeAsset ->
            AssetCard(asset = activeAsset, active = true)
        }
    )
}

@Composable
fun NetworkModalSelector(
    networks: List<Network>,
    onNetworkClick: (Int) -> Unit,
    activeNetworkIndex: Int,
    navController: NavController
) {
    ItemSelector(
        items = networks,
        onItemClick = onNetworkClick,
        activeItemIndex = activeNetworkIndex,
//        navController = navController,
        onManageClick = { navController.navigate("networks") },
        itemContent = { network, isActive ->
            NetworkCard(network = network, active = isActive)
        },
        triggerContent = { activeNetwork ->
            Icon(
                painter = painterResource(
                    id = R.drawable.network,
                ),
                modifier = if (activeNetwork == null) Modifier.alpha(0.2F).size(30.dp) else Modifier.alpha(0.75f).size(30.dp),
                contentDescription = if (activeNetwork == null)
                    "No active network" else "Active network",
            )
        },
        modifier = Modifier.height(50.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AccountModalSelectorPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            AccountModalSelector(
                accounts = accountList,
                onAccountClick = { null },
                activeAccountIndex = 0,
                rememberNavController()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssetModalSelectorPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            AssetModalSelector(
                assets = assetList1,
                onAssetClick = { null },
                activeAssetIndex = 0,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkModalSelectorPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            NetworkModalSelector(
                networks = networkList,
                onNetworkClick = { null },
                activeNetworkIndex = 0,
                rememberNavController()
            )
        }
    }
}