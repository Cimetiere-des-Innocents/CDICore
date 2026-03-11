package xyz.cimetieredesinnocents.cdicore.loaders

import net.minecraft.world.item.ItemStack
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdilib.loaders.CreativeModeTabLoaderFactory

object CreativeModeTabLoader : CreativeModeTabLoaderFactory(CDICore.ID) {
    val TAB by register("tab", ItemLoader) { ItemStack(ItemLoader.CDI_LOGO) }
}