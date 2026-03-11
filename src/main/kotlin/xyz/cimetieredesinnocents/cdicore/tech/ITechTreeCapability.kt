package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import xyz.cimetieredesinnocents.cdilib.player.PlayerCapabilityBase

interface ITechTreeCapability : PlayerCapabilityBase {
    val researching: MutableMap<ResourceKey<TechNode>, PlayerTechHolder.ResearchingTech>
    val researched: MutableSet<ResourceKey<TechNode>>
    var forgetCounter: Int
    var insightTechPoints: Int
    fun tick()
    fun forgetOne()
    fun research(
        tech: ResourceKey<TechNode>,
        theoryPoints: Int,
        appliedMaterials: List<ItemStack>,
        reverseEngineeringPoints: Int
    )
    fun memorize(tech: ResourceKey<TechNode>)
}