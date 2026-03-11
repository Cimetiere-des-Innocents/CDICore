package xyz.cimetieredesinnocents.cdicore.loaders

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.tech.TechNode
import xyz.cimetieredesinnocents.cdilib.loaders.DataRegistryLoaderFactory

object DataRegistryLoader : DataRegistryLoaderFactory(CDICore.ID) {
    val TECH_NODE: ResourceKey<Registry<TechNode>> = register("tech_node", TechNode.CODEC)
}