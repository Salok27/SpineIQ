package noshtek.back_pain_prototype.core.data.gamification

enum class AvatarCategory { HAIR, TOPS, BOTTOMS, ACCESSORIES }

/**
 * @param isDefault free starter item, implicitly owned (no DB row needed).
 *                  Hair/tops/bottoms each have exactly one default; accessories
 *                  have none (the avatar starts bare-wristed).
 */
data class AvatarCatalogItem(
    val id: String,
    val name: String,
    val category: AvatarCategory,
    val priceCoins: Int,
    val isDefault: Boolean = false,
)

/**
 * Static cosmetic catalog. The DB stores only owned/equipped state keyed by
 * these ids; the avatar renderer maps the same ids to draw layers. Cosmetics
 * only — nothing here may ever gate health functionality.
 */
object AvatarCatalog {
    val ALL: List<AvatarCatalogItem> = listOf(
        // Hair
        AvatarCatalogItem("hair_short", "Short Hair", AvatarCategory.HAIR, 0, isDefault = true),
        AvatarCatalogItem("hair_long", "Long Hair", AvatarCategory.HAIR, 40),
        AvatarCatalogItem("hair_curly", "Curly Hair", AvatarCategory.HAIR, 60),
        AvatarCatalogItem("hair_athletic", "Athletic Hair", AvatarCategory.HAIR, 80),
        // Tops
        AvatarCatalogItem("top_tshirt", "T-Shirt", AvatarCategory.TOPS, 0, isDefault = true),
        AvatarCatalogItem("top_athletic", "Athletic Shirt", AvatarCategory.TOPS, 60),
        AvatarCatalogItem("top_hoodie", "Hoodie", AvatarCategory.TOPS, 80),
        AvatarCatalogItem("top_wellness_jacket", "Wellness Jacket", AvatarCategory.TOPS, 120),
        AvatarCatalogItem("top_neon_hoodie", "Neon Hoodie", AvatarCategory.TOPS, 200),
        AvatarCatalogItem("top_champion_jacket", "Champion Jacket", AvatarCategory.TOPS, 250),
        AvatarCatalogItem("top_recovery_outfit", "Recovery Master Outfit", AvatarCategory.TOPS, 400),
        // Bottoms
        AvatarCatalogItem("bottom_joggers", "Joggers", AvatarCategory.BOTTOMS, 0, isDefault = true),
        AvatarCatalogItem("bottom_shorts", "Athletic Shorts", AvatarCategory.BOTTOMS, 50),
        AvatarCatalogItem("bottom_fitness", "Fitness Pants", AvatarCategory.BOTTOMS, 70),
        // Accessories
        AvatarCatalogItem("acc_glasses", "Glasses", AvatarCategory.ACCESSORIES, 40),
        AvatarCatalogItem("acc_fitness_band", "Fitness Band", AvatarCategory.ACCESSORIES, 80),
        AvatarCatalogItem("acc_backpack", "Backpack", AvatarCategory.ACCESSORIES, 90),
        AvatarCatalogItem("acc_headphones", "Headphones", AvatarCategory.ACCESSORIES, 110),
        AvatarCatalogItem("acc_smart_watch", "Smart Watch", AvatarCategory.ACCESSORIES, 150),
        AvatarCatalogItem("acc_gold_band", "Gold Fitness Band", AvatarCategory.ACCESSORIES, 300),
        AvatarCatalogItem("acc_crown", "Wellness Crown", AvatarCategory.ACCESSORIES, 500),
    )

    fun byId(id: String): AvatarCatalogItem? = ALL.firstOrNull { it.id == id }

    /** The free starter item per category (categories without a default are absent). */
    val DEFAULTS: Map<AvatarCategory, AvatarCatalogItem> =
        ALL.filter { it.isDefault }.associateBy { it.category }
}
