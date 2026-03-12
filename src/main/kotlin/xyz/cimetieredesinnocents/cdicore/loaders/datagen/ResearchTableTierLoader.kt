package xyz.cimetieredesinnocents.cdicore.loaders.datagen

import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.tech.ResearchTableTierLoaderFactory
import xyz.cimetieredesinnocents.cdicore.utils.ColorUtil

object ResearchTableTierLoader : ResearchTableTierLoaderFactory(CDICore.ID) {
    val BASIC by register("basic", ColorUtil.rgb(0x808080))
}