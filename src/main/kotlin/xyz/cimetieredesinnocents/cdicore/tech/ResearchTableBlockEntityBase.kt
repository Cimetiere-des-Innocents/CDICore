package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class ResearchTableBlockEntityBase(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    BlockEntity(type, pos, blockState) {
    var user: ServerPlayer? = null


}