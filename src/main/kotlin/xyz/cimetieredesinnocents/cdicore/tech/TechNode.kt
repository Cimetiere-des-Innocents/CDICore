package xyz.cimetieredesinnocents.cdicore.tech

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader

data class TechNode(
    val icon: ItemStack,
    val dataSize: Int,
    val researchTable: List<ResourceKey<ResearchTableTier>>,
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

                val STREAM_CODEC = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    Material::ingredient,
                    ByteBufCodecs.INT,
                    Material::count,
                    ::Material
                )
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

            val STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                TechPoints::theory,
                ByteBufCodecs.list<RegistryFriendlyByteBuf, Material>().apply(Material.STREAM_CODEC),
                TechPoints::materials,
                ByteBufCodecs.INT,
                TechPoints::reverseEngineering,
                ::TechPoints
            )
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.create {
            it.group(
                ItemStack.CODEC
                    .fieldOf("icon")
                    .forGetter(TechNode::icon),
                Codec.INT
                    .fieldOf("dataSize")
                    .forGetter(TechNode::dataSize),
                Codec.list(ResourceKey.codec(DataRegistryLoader.RESEARCH_TABLE_TIER))
                    .fieldOf("researchTable")
                    .forGetter(TechNode::researchTable),
                TechPoints.CODEC
                    .fieldOf("techPoints")
                    .forGetter(TechNode::techPoints),
                Codec.list(ResourceKey.codec(DataRegistryLoader.TECH_NODE))
                    .fieldOf("prerequisites")
                    .forGetter(TechNode::prerequisites)
            ).apply(it, ::TechNode)
        }

        fun ofKey(key: ResourceKey<TechNode>, level: Level): TechNode? {
            return level.registryAccess()
                .registryOrThrow(DataRegistryLoader.TECH_NODE)
                .get(key)
        }
    }
}
