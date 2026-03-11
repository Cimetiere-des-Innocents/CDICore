package xyz.cimetieredesinnocents.cdicore.loaders

import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.tech.TechTreeCapabilityProvider
import xyz.cimetieredesinnocents.cdilib.loaders.PlayerCapabilityLoaderFactory

object PlayerCapabilityLoader : PlayerCapabilityLoaderFactory(CDICore.ID) {
    val TECH_TREE = register("tech_tree", TechTreeCapabilityProvider)
}