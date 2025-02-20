package com.example.waterbug.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterbug.R
import com.example.waterbug.appstate.Account
import com.example.waterbug.appstate.Asset
import com.example.waterbug.appstate.Network
import com.example.waterbug.appstate.accountList
import com.example.waterbug.appstate.assetList1
import com.example.waterbug.appstate.networkList
import com.example.waterbug.ui.theme.WaterbugTheme

@Composable
fun <T> ItemList(
    description: String,
    items: List<T>,
    onItemClick: (Int) -> Unit,
    activeItemIndex: Int = -1,
    actionIconResId: Int? = null,
    onActionClick: ((Int) -> Unit)? = null,
    content: @Composable ColumnScope.(item: T, index: Int, isActive: Boolean) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${items.size}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = description + if (items.size > 1) "s" else "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surface)
    ) {
        itemsIndexed(items) { index, item ->
            val isActive = index == activeItemIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.02f))
                    .clickable { onItemClick(index) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f) // Content takes the remaining width
                ) {
                    content(item, index, isActive)
                }
                if (actionIconResId != null) {
                    onActionClick?.let { editClick ->
                        IconButton(
                            onClick = { editClick(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = actionIconResId,
                                ),
                                modifier = Modifier.alpha(0.5F).size(20.dp),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "edit"
                            )
                        }
                    }
                }
            }
//            if (index < items.size - 1) {
//                HorizontalDivider(
//                    modifier = Modifier.padding(horizontal = 8.dp),
//                    thickness = 1.dp,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
//                )
//            }
        }
    }
}

@Composable
fun AccountList(
    accounts: List<Account>,
    onAccountClick: (Int) -> Unit,
    activeAccountIndex: Int,
    onEditClick: ((Int) -> Unit)? = null
) {
    Surface {
        Column(modifier = Modifier.padding(8.dp)) {
            ItemList(
                description = "available account",
                items = accounts,
                onItemClick = onAccountClick,
                activeItemIndex = activeAccountIndex,
                actionIconResId = R.drawable.sharp_edit_note_24,
                onActionClick = onEditClick
            ) { account, index, isActive ->
                AccountCard(
                    account = account,
                    active = isActive,
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun NetworkList(
    networks: List<Network>,
    onNetworkClick: (Int) -> Unit,
    activeNetworkIndex: Int,
    onEditClick: ((Int) -> Unit)? = null
) {
    Surface {
        Column(modifier = Modifier.padding(8.dp)) {
//            Text(
//                text = "Configured networks:",
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
            ItemList(
                description = "network configuration",
                items = networks,
                onItemClick = onNetworkClick,
                activeItemIndex = activeNetworkIndex,
                actionIconResId = R.drawable.sharp_edit_note_24,
                onActionClick = onEditClick
            ) { network, index, isActive ->
                NetworkCard(
                    network = network,
                    active = isActive,
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AssetList(
    assets: List<Asset>,
    onAssetClick: (Int) -> Unit,
    activeAssetIndex: Int,
//    mode: Mode
) {
    Surface {
        Column(modifier = Modifier.padding(8.dp)) {
            ItemList(
                description = "available balance",
                items = assets,
                onItemClick = onAssetClick,
                activeItemIndex = activeAssetIndex,
                actionIconResId = R.drawable.rounded_info_24,
                onActionClick = { null },
            ) { asset, index, isActive ->
                AssetCard(
                    asset = asset,
                    active = isActive,
//                mode = mode,
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountListPreview() {
    WaterbugTheme(darkTheme = true) {
        AccountList(accounts = accountList,
            onAccountClick = { null },
            activeAccountIndex = 0,
            onEditClick = { null })
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkListPreview() {
    WaterbugTheme(darkTheme = true) {
        NetworkList(
            networks = networkList,
            onNetworkClick = { null },
            activeNetworkIndex = 0,
            onEditClick = { null })
    }
}

@Preview(showBackground = true)
@Composable
fun AssetListPreview() {
    WaterbugTheme(darkTheme = true) {
        AssetList(assets = assetList1, onAssetClick = { null }, activeAssetIndex = 0)
    }
}