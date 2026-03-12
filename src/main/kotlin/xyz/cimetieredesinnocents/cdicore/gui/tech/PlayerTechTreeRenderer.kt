package xyz.cimetieredesinnocents.cdicore.gui.tech

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.util.ColorRGBA
import net.minecraft.world.entity.player.Player
import xyz.cimetieredesinnocents.cdicore.loaders.PlayerCapabilityLoader
import xyz.cimetieredesinnocents.cdicore.tech.TechNode
import xyz.cimetieredesinnocents.cdicore.utils.ColorUtil

open class PlayerTechTreeRenderer(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    val player: Player
) : TechTreeRenderer(x, y, width, height) {
    val cap get() = player.getCapability(PlayerCapabilityLoader.TECH_TREE) ?: throw RuntimeException("Cannot get capability")

    override fun borderColor(node: ResourceKey<TechNode>): ColorRGBA {
        if (node in cap.researched) return DARK_BLUE
        if (node in cap.researching) return DARK_GREEN
        return DARK_GRAY
    }

    override fun borderColorHover(node: ResourceKey<TechNode>): ColorRGBA {
        if (node in cap.researched) return LIGHT_BLUE
        if (node in cap.researching) return LIGHT_GREEN

        val nodeValue = TechNode.ofKey(node, player.level()) ?: throw RuntimeException("Cannot find tech node")
        for (key in nodeValue.prerequisites) {
            if (key !in cap.researched) {
                return LIGHT_RED
            }
        }

        if (nodeValue.researchTable.isEmpty()) return LIGHT_GRAY
        return LIGHT_RED
    }

    override fun borderColorHoverPrerequisite(node: ResourceKey<TechNode>): ColorRGBA {
        if (node in cap.researched) return BLUE
        if (node in cap.researching) return GREEN
        return RED
    }

    override fun fullNodeTooltip(node: ResourceKey<TechNode>): Component {
        if (node in cap.researched) {
            return Component.empty()
                .append(Component.translatable("tooltip.cdicore.tech.researched").withColor(LIGHT_BLUE.rgba))
                .append("\n")
                .append(nodeTooltip(node))
        }
        if (node in cap.researching) {
            return Component.translatable("tooltip.cdicore.tech.researching").withColor(LIGHT_GREEN.rgba)
        }

        val nodeValue = TechNode.ofKey(node, player.level()) ?: throw RuntimeException("Cannot find tech node")
        val missingPrerequisites = nodeValue.prerequisites.filter { it !in cap.researched }
        if (missingPrerequisites.isEmpty() && nodeValue.researchTable.isEmpty()) {
            return Component.translatable("tooltip.cdicore.tech.researchable")
        }

        val result = Component.empty().append(Component.translatable("tooltip.cdicore.tech.notResearched").withColor(LIGHT_RED.rgba))
        for (prerequisite in missingPrerequisites) {
            result.append("\n")
            result.append(Component.translatable("tooltip.cdicore.tech.needResearch"))
            result.append(" ")
            result.append(nodeName(prerequisite))
        }
        if (!nodeValue.researchTable.isEmpty()) {
            result.append("\n")
            result.append(Component.translatable("tooltip.cdicore.tech.needResearchTable"))
        }
        return result
    }

    companion object {
        val DARK_GRAY = ColorUtil.hsv(0, 0f, 0.25f)
        val DARK_GREEN = ColorUtil.hsv(120, 0.75f, 0.5f)
        val DARK_BLUE = ColorUtil.hsv(200, 0.75f, 0.5f)
        val RED = ColorUtil.hsv(0, 0.75f, 0.5f)
        val GREEN = ColorUtil.hsv(120, 0.75f, 0.75f)
        val BLUE = ColorUtil.hsv(200, 0.75f, 0.75f)
        val LIGHT_RED = ColorUtil.hsv(0, 0.75f, 0.75f)
        val LIGHT_GRAY = ColorUtil.hsv(0, 0f, 0.75f)
        val LIGHT_GREEN = ColorUtil.hsv(120, 0.75f, 1f)
        val LIGHT_BLUE = ColorUtil.hsv(200, 0.75f, 1f)
    }
}