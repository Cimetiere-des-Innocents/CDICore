package xyz.cimetieredesinnocents.cdicore.tech

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import kotlin.math.max
import kotlin.math.min

data class PlayerTechHolder(
    val researchingTech: HashMap<ResourceKey<TechNode>, ResearchingTech>,
    val researchedTech: List<ResourceKey<TechNode>>
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

        companion object {
            val CODEC = RecordCodecBuilder.create { it.group(
                TechNode.TechPoints.CODEC.fieldOf("pointsRemaining").forGetter(ResearchingTech::pointsRemaining)
            ).apply(it, ::ResearchingTech) }
        }
    }
}