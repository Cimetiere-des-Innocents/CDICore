package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.core.Holder
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import xyz.cimetieredesinnocents.cdilib.loaders.DataGenLoaderFactory

open class TechNodeLoaderFactory(private val modid: String) {
    class RegistryItem(
        modid: String,
        name: String,
        private val node: () -> TechNode
    ) {
        val key = ResourceKey.create(
            DataRegistryLoader.TECH_NODE,
            ResourceLocation.fromNamespaceAndPath(modid, name)
        )

        val getter: (Level) -> Holder.Reference<TechNode> get() {
            return { it.registryAccess().registryOrThrow(DataRegistryLoader.TECH_NODE).getHolderOrThrow(key) }
        }

        fun register(context: BootstrapContext<TechNode>) {
            context.register(key, node())
        }

    }

    private val registry = mutableListOf<RegistryItem>()

    fun register(name: String, node: () -> TechNode): RegistryItem {
        val item = RegistryItem(modid, name, node)
        registry.add(item)
        return item
    }

    fun bootstrap(loader: DataGenLoaderFactory) {
        loader.datapack(DataRegistryLoader.TECH_NODE) {
            for (item in registry) {
                item.register(it)
            }
        }
    }
}