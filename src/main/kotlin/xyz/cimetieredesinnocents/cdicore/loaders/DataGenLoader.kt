package xyz.cimetieredesinnocents.cdicore.loaders

import xyz.cimetieredesinnocents.cdicore.datagen.ModItemModelProvider
import xyz.cimetieredesinnocents.cdicore.loaders.datagen.ResearchTableTierLoader
import xyz.cimetieredesinnocents.cdilib.loaders.DataGenLoaderFactory

object DataGenLoader : DataGenLoaderFactory() {
    init {
        ResearchTableTierLoader.bootstrap(this)
        client(::ModItemModelProvider)
    }
}