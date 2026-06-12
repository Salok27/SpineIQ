package noshtek.back_pain_prototype.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import noshtek.back_pain_prototype.core.data.gamification.AvatarCatalog
import noshtek.back_pain_prototype.core.data.gamification.AvatarCatalogItem
import noshtek.back_pain_prototype.core.data.gamification.AvatarCategory
import noshtek.back_pain_prototype.core.data.gamification.GamificationManager
import noshtek.back_pain_prototype.core.data.gamification.PurchaseResult
import noshtek.back_pain_prototype.ui.avatar.AvatarSpec
import javax.inject.Inject

data class ShopItemUi(
    val item: AvatarCatalogItem,
    val owned: Boolean,
    val equipped: Boolean,
    val affordable: Boolean,
)

data class ShopUiState(
    val isLoading: Boolean = true,
    val coins: Int = 0,
    val equippedSpec: AvatarSpec = AvatarSpec.Default,
    val previewItemId: String? = null,
    val selectedCategory: AvatarCategory? = null,
    val items: List<ShopItemUi> = emptyList(),
) {
    /** What the preview mannequin wears: equipped outfit with the previewed item swapped in. */
    val previewSpec: AvatarSpec
        get() {
            val preview = previewItemId?.let(AvatarCatalog::byId) ?: return equippedSpec
            return AvatarSpec(equippedSpec.equipped + (preview.category to preview.id))
        }
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val manager: GamificationManager,
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<AvatarCategory?>(null)
    private val previewItemId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ShopUiState> = combine(
        manager.snapshot, manager.ownedItems, selectedCategory, previewItemId,
    ) { snapshot, owned, category, preview ->
        val ownedIds = owned.mapTo(mutableSetOf()) { it.itemId }
        val equipped = owned.filter { it.equipped }.associate { it.category to it.itemId }
        val items = AvatarCatalog.ALL
            .filter { category == null || it.category == category }
            .map { item ->
                ShopItemUi(
                    item = item,
                    owned = item.isDefault || item.id in ownedIds,
                    equipped = equipped[item.category] == item.id ||
                        (item.isDefault && equipped[item.category] == null),
                    affordable = snapshot.coins >= item.priceCoins,
                )
            }
        ShopUiState(
            isLoading = false,
            coins = snapshot.coins,
            equippedSpec = AvatarSpec(equipped),
            previewItemId = preview,
            selectedCategory = category,
            items = items,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShopUiState())

    fun selectCategory(category: AvatarCategory?) {
        selectedCategory.value = category
    }

    fun preview(itemId: String?) {
        previewItemId.value = itemId
    }

    fun equip(itemId: String) {
        previewItemId.value = null
        viewModelScope.launch { runCatching { manager.equipItem(itemId) } }
    }

    /** Buys and immediately equips on success. */
    fun purchase(itemId: String, onResult: (PurchaseResult) -> Unit) {
        viewModelScope.launch {
            val result = runCatching { manager.purchaseItem(itemId) }
                .getOrDefault(PurchaseResult.UnknownItem)
            if (result == PurchaseResult.Success) {
                previewItemId.value = null
                runCatching { manager.equipItem(itemId) }
            }
            onResult(result)
        }
    }
}
