package xyz.cimetieredesinnocents.cdicore.loaders

import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.datagen.ModItemModelProvider
import xyz.cimetieredesinnocents.cdicore.loaders.datagen.ResearchTableTierLoader
import xyz.cimetieredesinnocents.cdicore.loaders.datagen.TechNodeLoader
import xyz.cimetieredesinnocents.cdilib.loaders.DataGenLoaderFactory

object DataGenLoader : DataGenLoaderFactory(CDICore.ID) {
    init {
        ResearchTableTierLoader.bootstrap(this)
        TechNodeLoader.bootstrap(this)
        client(::ModItemModelProvider)
    }
}