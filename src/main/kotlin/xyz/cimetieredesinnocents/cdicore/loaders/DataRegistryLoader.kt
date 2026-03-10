package xyz.cimetieredesinnocents.cdicore.loaders

import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.DataPackRegistryEvent
import xyz.cimetieredesinnocents.cdicore.tech.TechNode
import xyz.cimetieredesinnocents.cdicore.utils.RLUtil

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object DataRegistryLoader {
    val TECH_NODE = ResourceKey.createRegistryKey<TechNode>(RLUtil.of("tech_node"))

    @SubscribeEvent
    fun register(event: DataPackRegistryEvent.NewRegistry) {
        event.dataPackRegistry(TECH_NODE, TechNode.CODEC, TechNode.CODEC)
    }
}