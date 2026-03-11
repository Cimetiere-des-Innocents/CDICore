package xyz.cimetieredesinnocents.cdicore.loaders

import net.minecraft.world.level.GameRules
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdilib.loaders.GameRuleLoaderFactory

object GameRuleLoader : GameRuleLoaderFactory(CDICore.ID) {
    val ENABLE_FORGETTING by registerBool("enableForgetting", GameRules.Category.PLAYER, true)
}