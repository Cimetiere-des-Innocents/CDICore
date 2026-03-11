package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.network.PacketDistributor
import xyz.cimetieredesinnocents.cdicore.loaders.DataAttachmentLoader
import xyz.cimetieredesinnocents.cdicore.loaders.NetworkLoader

@EventBusSubscriber
object TechTreePlayerEventListener {
    private fun sync(player: Player) {
        if (player.level().isClientSide) return

        PacketDistributor.sendToPlayer(
            player as ServerPlayer,
            NetworkLoader.TECH_SYNC_INIT.packet {
                @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
                it.holder = player.getData(DataAttachmentLoader.PLAYER_TECH)!!
            }
        )
    }

    @SubscribeEvent
    fun playerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        sync(event.entity)
    }

    @SubscribeEvent
    fun playerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        sync(event.entity)
    }
}