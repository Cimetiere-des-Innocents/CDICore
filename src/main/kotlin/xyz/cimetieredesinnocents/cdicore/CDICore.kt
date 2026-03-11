package xyz.cimetieredesinnocents.cdicore

import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import xyz.cimetieredesinnocents.cdicore.loaders.DataAttachmentLoader
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import xyz.cimetieredesinnocents.cdicore.loaders.NetworkLoader
import xyz.cimetieredesinnocents.cdicore.loaders.PlayerCapabilityLoader

@Mod(CDICore.ID)
object CDICore {
    const val ID = "cdicore"

    init {
        DataRegistryLoader.bootstrap(MOD_BUS)
        DataAttachmentLoader.bootstrap(MOD_BUS)
        NetworkLoader.bootstrap(MOD_BUS)
        PlayerCapabilityLoader.bootstrap(MOD_BUS, FORGE_BUS)
    }
}