package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.resources.ResourceKey
import xyz.cimetieredesinnocents.cdilib.player.PlayerCapabilityBase

interface ITechTreeCapability : PlayerCapabilityBase {
    val researching: MutableMap<ResourceKey<TechNode>, PlayerTechHolder.ResearchingTech>
    val researched: MutableSet<ResourceKey<TechNode>>
    var forgetCounter: Int
}