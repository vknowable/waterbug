package com.example.waterbug.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.waterbug.R
import com.example.waterbug.appstate.Account
import com.example.waterbug.appstate.Asset
import com.example.waterbug.appstate.Mode
import com.example.waterbug.appstate.Network
import com.example.waterbug.appstate.account1
import com.example.waterbug.appstate.asset1
import com.example.waterbug.appstate.network1
import com.example.waterbug.ui.theme.WaterbugTheme
import com.example.waterbug.utils.truncateAddress

@Composable
fun <T> ItemCard(
    item: T,
    active: Boolean,
    iconResId: Int?,
    iconSize: Int = 40,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(T) -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = Color.Transparent,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconResId != null) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(iconSize.dp)
                )
                Spacer(modifier = Modifier.width(12.dp)) // Spacing between icon and text
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                content(item)
            }
        }
    }
}

@Composable
fun AccountCard(account: Account?, active: Boolean, modifier: Modifier = Modifier) {
    ItemCard(
        item = account,
        active = active,
        iconResId = R.drawable.outline_account_circle_24,
        iconSize = 32,
        modifier = modifier,
    ) { acc ->
        if (acc != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = acc.alias,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${truncateAddress(acc.address)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_visibility_24),
                            contentDescription = "Balance indicator",
                            modifier = Modifier
                                .size(16.dp)
                                .alpha(0.4F),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${truncateAddress(acc.defaultPayAddr)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_shield_24),
                            contentDescription = "Balance indicator",
                            modifier = Modifier
                                .size(16.dp)
                                .alpha(0.4F),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        } else {
            // Display fallback message
            Text(
                text = "No account selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun AccountCardMini(account: Account?, active: Boolean, modifier: Modifier = Modifier) {
    ItemCard(
        item = account,
        active = active,
        iconResId = null,
        modifier = modifier,
    ) { acc ->
        if (acc != null) {
            Text(
                text = acc.alias,
                style = MaterialTheme.typography.labelLarge
            )
        } else {
            // Display fallback message
            Text(
                text = "No account selected",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun NetworkCard(network: Network?, active: Boolean, modifier: Modifier = Modifier) {
    ItemCard(
        item = network,
        active = active,
        iconResId = R.drawable.outline_linked_services_24,
        iconSize = 32,
    ) { net ->
        if (net != null) {
            Text(
                text = net.name,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Chain ID: ${net.chainId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            Text(
                text = "Provider: ${net.rpcUrl}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
        } else {
            // Display fallback message
            Text(
                text = "No network selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun AssetCard(asset: Asset?, active: Boolean, modifier: Modifier = Modifier) {
    ItemCard(
        item = asset,
        active = active,
        iconResId = R.drawable.nam,
        iconSize = 32,
        modifier = modifier,
    ) { ast ->
        if (ast != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = ast.name,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = truncateAddress(ast.address),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format("%.2f", ast.balances.transparentBalance),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_visibility_24),
                            contentDescription = "Balance indicator",
                            modifier = Modifier
                                .size(16.dp)
                                .alpha(0.4F),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format("%.2f", ast.balances.shieldedBalance),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_shield_24),
                            contentDescription = "Balance indicator",
                            modifier = Modifier
                                .size(16.dp)
                                .alpha(0.4F),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

        } else {
            // Display fallback message
            Text(
                text = "No asset selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

//@Composable
//fun AssetCardMini(asset: Asset?, active: Boolean, modifier: Modifier = Modifier) {
//    ItemCard(
//        item = asset,
//        active = active,
//        iconResId = R.drawable.nam,
//        iconSize = 25,
//        modifier = modifier,
//    ) { ast ->
//        if (ast != null) {
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = ast.name,
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.labelMedium
//                )
////                val (text, @DrawableRes balanceIcon: Int) = when (mode) {
////                    Mode.TRANSPARENT -> "${ast.balances.transparentBalance}" to R.drawable.baseline_visibility_24
////                    Mode.SHIELDED -> "${ast.balances.shieldedBalance}" to R.drawable.baseline_shield_24
////                    else -> "" to 0
////                }
//                Column {
//                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
//                        Text(
//                            text = "${ast.balances.transparentBalance}",
//                            style = MaterialTheme.typography.labelSmall,
//                            color = MaterialTheme.colorScheme.tertiary
//                        )
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_visibility_24),
//                            contentDescription = "Balance indicator",
//                            modifier = Modifier
//                                .size(15.dp)
//                                .alpha(0.5F)
//                        )
//                    }
//
//                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
//                        Text(
//                            text = "${ast.balances.shieldedBalance}",
//                            style = MaterialTheme.typography.labelSmall,
//                            color = MaterialTheme.colorScheme.tertiary
//                        )
//                        Icon(
//                            painter = painterResource(id = R.drawable.baseline_shield_24),
//                            contentDescription = "Balance indicator",
//                            modifier = Modifier
//                                .size(15.dp)
//                                .alpha(0.5F)
//                        )
//                    }
//                }
//            }
//
//
//        } else {
//            // Display fallback message
//            Text(
//                text = "No asset selected",
//                style = MaterialTheme.typography.labelLarge,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(4.dp)
//            )
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun AccountCardPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            AccountCard(account1, true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountCardMiniPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            AccountCardMini(account1, true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NetworkCardPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            NetworkCard(network1, true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssetCardPreview() {
    WaterbugTheme(darkTheme = true) {
        Surface {
            AssetCard(asset1, true)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AssetCardMiniPreview() {
//    WaterbugTheme(darkTheme = true) {
//        Surface {
//            AssetCardMini(asset1, true)
//        }
//    }
//}