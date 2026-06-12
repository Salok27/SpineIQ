package noshtek.back_pain_prototype.ui.avatar

/**
 * Pairs AvatarCatalog item ids with their draw layers. Adding a cosmetic =
 * one catalog entry + one layer function + one map entry here. Unknown ids
 * are skipped safely by the renderer, so catalog and registry can never
 * crash each other.
 */
object AvatarRegistry {
    val layers: Map<String, AvatarLayer> = mapOf(
        // Hair
        "hair_short" to HairShort,
        "hair_long" to HairLong,
        "hair_curly" to HairCurly,
        "hair_athletic" to HairAthletic,
        // Tops
        "top_tshirt" to TopTShirt,
        "top_athletic" to TopAthletic,
        "top_hoodie" to TopHoodie,
        "top_wellness_jacket" to TopWellnessJacket,
        "top_neon_hoodie" to TopNeonHoodie,
        "top_champion_jacket" to TopChampionJacket,
        "top_recovery_outfit" to TopRecoveryOutfit,
        // Bottoms
        "bottom_joggers" to BottomJoggers,
        "bottom_shorts" to BottomShorts,
        "bottom_fitness" to BottomFitnessPants,
        // Accessories
        "acc_glasses" to AccGlasses,
        "acc_smart_watch" to AccSmartWatch,
        "acc_fitness_band" to AccFitnessBand,
        "acc_gold_band" to AccGoldBand,
        "acc_backpack" to AccBackpack,
        "acc_headphones" to AccHeadphones,
        "acc_crown" to AccCrown,
    )
}
