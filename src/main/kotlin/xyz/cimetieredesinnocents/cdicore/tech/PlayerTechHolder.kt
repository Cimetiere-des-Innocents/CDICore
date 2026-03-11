package xyz.cimetieredesinnocents.cdicore.tech

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import kotlin.math.max
import kotlin.math.min

data class PlayerTechHolder(
    val researchingTech: HashMap<ResourceKey<TechNode>, ResearchingTech>,
    val researchedTech: HashSet<ResourceKey<TechNode>>
) {
    data class ResearchingTech(
        val pointsRemaining: TechNode.TechPoints
    ) {
        fun research(theoryPoints: Int, appliedMaterials: List<ItemStack>, reverseEngineeringPoints: Int): ResearchingTech {
            val theory = max(pointsRemaining.theory - theoryPoints, 0)
            val materials = pointsRemaining.materials.map {
                var newCount = it.count
                for (item in appliedMaterials) {
                    if (item.count > 0 && it.ingredient.test(item)) {
                        val countToReduce = min(item.count, it.count)
                        item.count -= countToReduce
                        newCount -= countToReduce
                    }
                }
                TechNode.TechPoints.Material(it.ingredient, newCount)
            }.filter { it.count > 0 }
            val reverseEngineering = max(pointsRemaining.reverseEngineering - reverseEngineeringPoints, 0)
            return ResearchingTech(TechNode.TechPoints(theory, materials, reverseEngineering))
        }

        val finished: Boolean get() {
            if (pointsRemaining.theory > 0) {
                return false
            }

            if (pointsRemaining.reverseEngineering > 0) {
                return false
            }

            if (pointsRemaining.materials.isEmpty()) {
                return true
            }
            for (material in pointsRemaining.materials) {
                if (material.count > 0) {
                    return false
                }
            }
            return true
        }

        companion object {
            val CODEC = RecordCodecBuilder.create { it.group(
                TechNode.TechPoints.CODEC.fieldOf("pointsRemaining").forGetter(ResearchingTech::pointsRemaining)
            ).apply(it, ::ResearchingTech) }

            val STREAM_CODEC = StreamCodec.composite(
                TechNode.TechPoints.STREAM_CODEC,
                ResearchingTech::pointsRemaining,
                ::ResearchingTech
            )
        }
    }

    companion object {
        val CODEC = RecordCodecBuilder.create {
            it.group(
                Codec.unboundedMap(
                    ResourceKey.codec(DataRegistryLoader.TECH_NODE),
                    ResearchingTech.CODEC
                ).fieldOf("researchingTech").forGetter(PlayerTechHolder::researchingTech),
                Codec.list(ResourceKey.codec(DataRegistryLoader.TECH_NODE))
                    .fieldOf("researchedTech")
                    .forGetter { v -> v.researchedTech.toList() },
            ).apply(it) { researching, researched ->
                PlayerTechHolder(HashMap(researching), researched.toHashSet())
            }
        }

        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                { it: Int -> HashMap<ResourceKey<TechNode>, ResearchingTech>(it) },
                ResourceKey.streamCodec(DataRegistryLoader.TECH_NODE),
                ResearchingTech.STREAM_CODEC
            ),
            PlayerTechHolder::researchingTech,
            ByteBufCodecs
                .list<ByteBuf, ResourceKey<TechNode>>()
                .apply(ResourceKey.streamCodec(DataRegistryLoader.TECH_NODE)),
            { it.researchedTech.toList() },
            { researching, researched ->
                PlayerTechHolder(HashMap(researching), HashSet(researched))
            }
        )
    }
}