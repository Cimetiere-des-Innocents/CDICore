package xyz.cimetieredesinnocents.cdicore.loaders

import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.item.CDIDebugStickItem
import xyz.cimetieredesinnocents.cdilib.loaders.ItemLoaderFactory

object ItemLoader : ItemLoaderFactory(CDICore.ID) {
    val CDI_LOGO by simpleItemHidden("cdi_logo")
    val CDI_DEBUG_STICK by register("cdi_debug_stick", ::CDIDebugStickItem)
}