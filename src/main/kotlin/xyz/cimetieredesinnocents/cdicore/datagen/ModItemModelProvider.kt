package xyz.cimetieredesinnocents.cdicore.datagen

import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.loaders.ItemLoader
import xyz.cimetieredesinnocents.cdilib.datagen.BaseItemModelProvider
import xyz.cimetieredesinnocents.cdilib.loaders.DataGenLoaderFactory

class ModItemModelProvider(context: DataGenLoaderFactory.Context) :
    BaseItemModelProvider(CDICore.ID, context) {
    override fun registerModels() {
        basicItem(ItemLoader.CDI_LOGO)
        handheld(ItemLoader.CDI_DEBUG_STICK)
    }
}