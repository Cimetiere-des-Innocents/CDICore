package xyz.cimetieredesinnocents.cdicore

import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import xyz.cimetieredesinnocents.cdicore.config.Config
import xyz.cimetieredesinnocents.cdicore.loaders.*

@Mod(CDICore.ID)
object CDICore {
    const val ID = "cdicore"

    init {
        LOADING_CONTEXT.activeContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "cdimods/cdicore-common.toml")
        DataRegistryLoader.bootstrap(MOD_BUS)
        DataAttachmentLoader.bootstrap(MOD_BUS)
        NetworkLoader.bootstrap(MOD_BUS)
        PlayerCapabilityLoader.bootstrap(MOD_BUS, FORGE_BUS)
        GameRuleLoader.bootstrap(MOD_BUS)
        DataGenLoader.bootstrap(MOD_BUS)
        ItemLoader.bootstrap(MOD_BUS)
        CreativeModeTabLoader.bootstrap(MOD_BUS)
    }
}