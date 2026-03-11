package xyz.cimetieredesinnocents.cdicore.loaders.datagen

import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.tech.ResearchTableTierLoaderFactory
import xyz.cimetieredesinnocents.cdicore.utils.ColorUtil

object ResearchTableTierLoader : ResearchTableTierLoaderFactory(CDICore.ID) {
    val NONE by register("none", ColorUtil.rgb(0x808080))
}