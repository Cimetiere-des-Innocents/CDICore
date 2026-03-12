package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.core.Holder
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ColorRGBA
import net.minecraft.world.level.Level
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import xyz.cimetieredesinnocents.cdilib.loaders.DataGenLoaderFactory
import kotlin.reflect.KProperty

open class ResearchTableTierLoaderFactory(private val modid: String) {
    class RegistryItem(
        modid: String,
        name: String,
        private val color: ColorRGBA
    ) {
        private val resourceKey = ResourceKey.create(
            DataRegistryLoader.RESEARCH_TABLE_TIER,
            ResourceLocation.fromNamespaceAndPath(modid, name)
        )

        operator fun getValue(thisRef: Any?, propertyKey: KProperty<*>): (Level) -> Holder.Reference<ResearchTableTier> {
            return { it.registryAccess().registryOrThrow(DataRegistryLoader.RESEARCH_TABLE_TIER).getHolderOrThrow(resourceKey) }
        }

        fun register(context: BootstrapContext<ResearchTableTier>) {
            context.register(resourceKey, ResearchTableTier(color))
        }
    }

    private val registry = mutableListOf<RegistryItem>()

    fun register(name: String, color: ColorRGBA): RegistryItem {
        val result = RegistryItem(modid, name, color)
        registry.add(result)
        return result
    }

    fun bootstrap(loader: DataGenLoaderFactory) {
        loader.datapack(DataRegistryLoader.RESEARCH_TABLE_TIER) {
            for (item in registry) {
                item.register(it)
            }
        }
    }
}