package xyz.cimetieredesinnocents.cdicore.loaders

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.tech.ResearchTableTier
import xyz.cimetieredesinnocents.cdicore.tech.TechNode
import xyz.cimetieredesinnocents.cdilib.loaders.DataRegistryLoaderFactory

typealias K<T> = ResourceKey<Registry<T>>

object DataRegistryLoader : DataRegistryLoaderFactory(CDICore.ID) {
    val TECH_NODE: K<TechNode> = register("tech_node") { TechNode.CODEC }
    val RESEARCH_TABLE_TIER: K<ResearchTableTier> = register("research_table_tier") { ResearchTableTier.CODEC }
}