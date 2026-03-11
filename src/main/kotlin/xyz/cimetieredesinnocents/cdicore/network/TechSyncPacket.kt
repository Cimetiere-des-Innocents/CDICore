package xyz.cimetieredesinnocents.cdicore.network

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.cimetieredesinnocents.cdicore.loaders.DataAttachmentLoader
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import xyz.cimetieredesinnocents.cdicore.tech.PlayerTechHolder
import xyz.cimetieredesinnocents.cdicore.tech.TechNode

object TechSyncPacket {
    class KeyVal {
        lateinit var key: ResourceKey<TechNode>
        lateinit var value: PlayerTechHolder.ResearchingTech
    }

    class KeyOnly {
        lateinit var key: ResourceKey<TechNode>
    }

    object SetResearching : BasePacket<KeyVal, RegistryFriendlyByteBuf>(
        "tech_sync_set_researching",
        Direction.TO_CLIENT,
        Phase.PLAY
    ) {
        override val factory = ::KeyVal
        override val codec = codec(
            custom(
                ResourceKey.streamCodec(DataRegistryLoader.TECH_NODE), KeyVal::key
            ).custom(PlayerTechHolder.ResearchingTech.STREAM_CODEC, KeyVal::value)
        )

        override fun onClientReceived(packet: KeyVal, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                val data = player.getData(DataAttachmentLoader.PLAYER_TECH) ?: return@enqueueWork
                data.researchingTech[packet.key] = packet.value
                player.setData(DataAttachmentLoader.PLAYER_TECH, data)
            }
        }
    }

    open class KeyOnlyPacket(name: String) : BasePacket<KeyOnly, RegistryFriendlyByteBuf>(
        name,
        Direction.TO_CLIENT,
        Phase.PLAY
    )  {
        override val factory = ::KeyOnly
        override val codec = codec(custom(
            ResourceKey.streamCodec(DataRegistryLoader.TECH_NODE),
            KeyOnly::key
        ))
    }

    object RemoveResearching : KeyOnlyPacket("tech_sync_remove_researching") {
        override fun onClientReceived(packet: KeyOnly, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                val data = player.getData(DataAttachmentLoader.PLAYER_TECH) ?: return@enqueueWork
                data.researchingTech.remove(packet.key)
            }
        }
    }

    object AddResearched : KeyOnlyPacket("tech_sync_add_researched") {
        override fun onClientReceived(packet: KeyOnly, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                val data = player.getData(DataAttachmentLoader.PLAYER_TECH) ?: return@enqueueWork
                data.researchedTech.add(packet.key)
            }
        }
    }

    object RemoveResearched : KeyOnlyPacket("tech_sync_remove_researched") {
        override fun onClientReceived(packet: KeyOnly, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                val data = player.getData(DataAttachmentLoader.PLAYER_TECH) ?: return@enqueueWork
                data.researchedTech.remove(packet.key)
            }
        }
    }

    object Init : BasePacket<Init.Data, RegistryFriendlyByteBuf>(
        "tech_sync_init",
        Direction.TO_CLIENT,
        Phase.PLAY
    ) {
        class Data {
            lateinit var holder: PlayerTechHolder
        }

        override val factory = ::Data
        override val codec = codec(custom(PlayerTechHolder.STREAM_CODEC, Data::holder))

        override fun onClientReceived(packet: Data, context: IPayloadContext) {
            context.enqueueWork {
                context.player().setData(DataAttachmentLoader.PLAYER_TECH, packet.holder)
            }
        }
    }
}