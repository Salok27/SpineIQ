package noshtek.back_pain_prototype.ui.shop

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.gamification.AvatarCategory
import noshtek.back_pain_prototype.core.data.gamification.PurchaseResult
import noshtek.back_pain_prototype.ui.avatar.Avatar
import noshtek.back_pain_prototype.ui.avatar.AvatarSize
import noshtek.back_pain_prototype.ui.avatar.AvatarSpec
import noshtek.back_pain_prototype.ui.common.GlassCard
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.PressableCard
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.ScreenHeader
import noshtek.back_pain_prototype.ui.common.TextActionButton
import noshtek.back_pain_prototype.ui.gamification.CoinBalancePill
import noshtek.back_pain_prototype.ui.gamification.CoinGlyph
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SheetShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

private val CategoryLabels = mapOf<AvatarCategory?, String>(
    null to "All",
    AvatarCategory.HAIR to "Hair",
    AvatarCategory.TOPS to "Tops",
    AvatarCategory.BOTTOMS to "Bottoms",
    AvatarCategory.ACCESSORIES to "Accessories",
)

/**
 * Avatar Shop — spend Spine Coins on cosmetics. Cosmetics only: nothing here
 * gates any health feature.
 */
@Composable
fun ShopScreen(
    navController: NavController,
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmItem by remember { mutableStateOf<ShopItemUi?>(null) }
    var resultHint by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Shop",
            subtitle = "Style your spine buddy",
            trailing = { CoinBalancePill(coins = state.coins) },
        )

        PreviewPanel(
            spec = state.previewSpec,
            previewing = state.previewItemId != null,
            onReset = { viewModel.preview(null) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CategoryLabels.forEach { (category, label) ->
                val selected = state.selectedCategory == category
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SpineIQTheme.colors.rewardContainer,
                        selectedLabelColor = SpineIQTheme.colors.rewardText,
                    ),
                )
            }
        }

        resultHint?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelMedium,
                color = SpineIQTheme.colors.rewardText,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp),
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.items, key = { it.item.id }) { itemUi ->
                ShopItemCard(
                    itemUi = itemUi,
                    onClick = {
                        resultHint = null
                        when {
                            itemUi.owned -> viewModel.equip(itemUi.item.id)
                            else -> {
                                viewModel.preview(itemUi.item.id)
                                confirmItem = itemUi
                            }
                        }
                    },
                )
            }
        }
    }

    confirmItem?.let { itemUi ->
        PurchaseConfirmSheet(
            itemUi = itemUi,
            coins = state.coins,
            previewSpec = state.previewSpec,
            onConfirm = {
                viewModel.purchase(itemUi.item.id) { result ->
                    resultHint = when (result) {
                        PurchaseResult.Success -> "${itemUi.item.name} equipped!"
                        PurchaseResult.InsufficientCoins -> "Not enough coins — complete assessments to earn more."
                        PurchaseResult.AlreadyOwned -> "Already owned."
                        PurchaseResult.UnknownItem -> "Something went wrong. Try again."
                    }
                }
                confirmItem = null
            },
            onDismiss = {
                viewModel.preview(null)
                confirmItem = null
            },
        )
    }
}

@Composable
private fun PreviewPanel(
    spec: AvatarSpec,
    previewing: Boolean,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SpineIQTheme.colors
    GlassCard(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(112.dp)
                    .clip(PillShape)
                    .background(colors.rewardContainer.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(targetState = spec, animationSpec = tween(MotionTokens.DurationFast), label = "preview") {
                    Avatar(spec = it, size = AvatarSize.Medium)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (previewing) "Previewing" else "Your look",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    if (previewing) "Buy it to keep it on." else "Tap any item to preview or equip.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (previewing) {
                    TextActionButton(onClick = onReset, label = "Reset")
                }
            }
        }
    }
}

@Composable
private fun ShopItemCard(
    itemUi: ShopItemUi,
    onClick: () -> Unit,
) {
    val colors = SpineIQTheme.colors
    val item = itemUi.item
    PressableCard(
        onClick = onClick,
        contentPadding = PaddingValues(12.dp),
        modifier = if (itemUi.equipped) {
            Modifier.border(2.dp, colors.reward, noshtek.back_pain_prototype.ui.theme.CardShape)
        } else Modifier,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(96.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Focused preview: default mannequin wearing only this item.
            Avatar(
                spec = AvatarSpec(mapOf(item.category to item.id)),
                size = AvatarSize.Medium,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            item.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                itemUi.equipped -> {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = colors.rewardText,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Equipped",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.rewardText,
                        fontWeight = FontWeight.Bold,
                    )
                }
                itemUi.owned -> Text(
                    "Equip",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.rewardText,
                    fontWeight = FontWeight.Bold,
                )
                else -> {
                    CoinGlyph(size = 14.dp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${item.priceCoins}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (itemUi.affordable) colors.coinText
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseConfirmSheet(
    itemUi: ShopItemUi,
    coins: Int,
    previewSpec: AvatarSpec,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val item = itemUi.item
    val balanceAfter = coins - item.priceCoins
    ModalBottomSheet(onDismissRequest = onDismiss, shape = SheetShape) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Avatar(spec = previewSpec, size = AvatarSize.Medium)
            Spacer(Modifier.height(8.dp))
            Text(item.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CoinGlyph(size = 18.dp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "${item.priceCoins}",
                    style = MaterialTheme.typography.titleMedium,
                    color = SpineIQTheme.colors.coinText,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                if (itemUi.affordable) "Balance after: $balanceAfter coins"
                else "You need ${item.priceCoins - coins} more coins",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                onClick = onConfirm,
                label = "Buy for ${item.priceCoins} coins",
                enabled = itemUi.affordable,
            )
            if (!itemUi.affordable) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Earn coins by completing assessments and daily check-ins.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(4.dp))
            TextActionButton(onClick = onDismiss, label = "Cancel")
        }
    }
}
