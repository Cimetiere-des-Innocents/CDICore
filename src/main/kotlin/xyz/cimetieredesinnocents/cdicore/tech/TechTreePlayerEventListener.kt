package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent
import net.neoforged.neoforge.network.PacketDistributor
import xyz.cimetieredesinnocents.cdicore.config.Config
import xyz.cimetieredesinnocents.cdicore.loaders.DataAttachmentLoader
import xyz.cimetieredesinnocents.cdicore.loaders.GameRuleLoader
import xyz.cimetieredesinnocents.cdicore.loaders.NetworkLoader
import xyz.cimetieredesinnocents.cdicore.loaders.PlayerCapabilityLoader

@EventBusSubscriber
object TechTreePlayerEventListener {
    private fun sync(player: Player) {
        if (player.level().isClientSide) return

        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        PacketDistributor.sendToPlayer(
            player as ServerPlayer,
            NetworkLoader.TECH_SYNC_INIT.packet {
                it.holder = player.getData(DataAttachmentLoader.PLAYER_TECH)!!
            },
            NetworkLoader.TECH_SYNC_INSIGHT.packet {
                it.value = player.getData(DataAttachmentLoader.INSIGHT_TECH_POINTS)!!
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

    @SubscribeEvent
    fun playerWakeUp(event: PlayerWakeUpEvent) {
        val player = event.entity
        if (player.level().isClientSide) return
        val cap = player.getCapability(PlayerCapabilityLoader.TECH_TREE) ?: return
        if (player.level().gameRules.getRule(GameRuleLoader.ENABLE_FORGETTING).get()) {
            cap.forgetOne()
        }
        if (cap.insightTechPoints < Config.insightTechPointsLimit) {
            cap.insightTechPoints++
        }
    }
}