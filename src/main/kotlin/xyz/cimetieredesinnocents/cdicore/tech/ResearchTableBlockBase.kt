package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock

abstract class ResearchTableBlockBase(
    properties: Properties,
    val extends: List<ResourceKey<out ResearchTableBlockBase>> = listOf()
) : Block(properties), EntityBlock {

}