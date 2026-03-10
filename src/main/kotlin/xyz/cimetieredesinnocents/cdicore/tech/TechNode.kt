package xyz.cimetieredesinnocents.cdicore.tech

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Block
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import java.util.*
import kotlin.jvm.optionals.getOrNull

data class TechNode(
    val researchTable: ResourceKey<Block>?,
    val techPoints: TechPoints,
    val prerequisites: List<ResourceKey<TechNode>>
) {
    data class TechPoints(
        val theory: Int,
        val materials: List<Material>,
        val reverseEngineering: Int
    ) {
        data class Material(
            val ingredient: Ingredient,
            val count: Int
        ) {
            companion object {
                val CODEC = RecordCodecBuilder.create {
                    it.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(Material::ingredient),
                        Codec.INT.fieldOf("count").forGetter(Material::count)
                    ).apply(it, ::Material)
                }
            }
        }

        companion object {
            val CODEC = RecordCodecBuilder.create {
                it.group(
                    Codec.INT.fieldOf("theory").forGetter(TechPoints::theory),
                    Codec.list(Material.CODEC).fieldOf("materials").forGetter(TechPoints::materials),
                    Codec.INT.fieldOf("reverseEngineering").forGetter(TechPoints::reverseEngineering)
                ).apply(it, ::TechPoints)
            }
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.create {
            it.group(
                ResourceKey.codec(Registries.BLOCK)
                    .optionalFieldOf("researchTable")
                    .forGetter { Optional.ofNullable(it.researchTable) },
                TechPoints.CODEC
                    .fieldOf("techPoints")
                    .forGetter(TechNode::techPoints),
                Codec.list(ResourceKey.codec(DataRegistryLoader.TECH_NODE))
                    .fieldOf("prerequisites")
                    .forGetter(TechNode::prerequisites)
            ).apply(it) { researchTable, techPoints, prerequisites ->
                TechNode(researchTable.getOrNull(), techPoints, prerequisites)
            }
        }
    }
}