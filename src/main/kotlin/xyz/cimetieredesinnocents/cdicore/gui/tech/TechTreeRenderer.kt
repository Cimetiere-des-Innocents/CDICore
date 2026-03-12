package xyz.cimetieredesinnocents.cdicore.gui.tech

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.util.ColorRGBA
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import xyz.cimetieredesinnocents.cdicore.tech.TechNode
import xyz.cimetieredesinnocents.cdicore.utils.ColorUtil
import kotlin.math.abs

open class TechTreeRenderer(
    x: Int, // 视窗左上角相对于屏幕左上角的坐标
    y: Int,
    width: Int, // 视窗尺寸
    height: Int
) : AbstractWidget(x, y, width, height, Component.empty()) {
    var viewportX = 0 // 视窗左上角相对于实际左上角的坐标
    var viewportY = 0
    private var draggingViewport = false
    private var draggedSinceClick = false
    private var dragStartMouseX = 0.0
    private var dragStartMouseY = 0.0
    private var dragLastMouseX = 0.0
    private var dragLastMouseY = 0.0
    private var lastMouseX = 0.0
    private var lastMouseY = 0.0

    data class NodeWithPos(
        val node: TechNode,
        val x: Int,
        val y: Int,
        val key: ResourceKey<TechNode>
    )

    data class NodeList(
        val byPos: List<List<NodeWithPos>>,
        val byKey: Map<ResourceKey<TechNode>, NodeWithPos>
    )

    protected open fun borderColor(node: ResourceKey<TechNode>): ColorRGBA {
        return ColorUtil.rgb(0x909090)
    }

    protected open fun borderColorHover(node: ResourceKey<TechNode>): ColorRGBA {
        return ColorUtil.rgb(0xf0f0f0)
    }

    protected open fun borderColorHoverPrerequisite(node: ResourceKey<TechNode>): ColorRGBA {
        return ColorUtil.rgb(0xc0c0c0)
    }

    protected open fun fullNodeTooltip(node: ResourceKey<TechNode>): Component {
        return nodeTooltip(node)
    }
    
    protected open fun onNodeClick(node: ResourceKey<TechNode>) {}

    private data class LayoutMetrics(
        val iconSize: Int,
        val borderWidth: Int,
        val borderPadding: Int,
        val distance: Int,
        val nodeHeight: Int,
        val columnWidths: List<Int>,
        val columnStartX: List<Int>
    )

    private fun layoutMetrics(): LayoutMetrics {
        val font = Minecraft.getInstance().font
        val iconSize = 16
        val borderWidth = 1
        val borderPadding = 2
        val distance = 4
        val textWidths = nodeWidths()

        val nodeHeight = borderWidth * 2 + borderPadding * 2 + maxOf(iconSize, font.lineHeight)
        val columnWidths = List(NODES.byPos.size) { index ->
            borderWidth * 2 + borderPadding * 3 + iconSize + textWidths.getOrElse(index) { 0 }
        }
        val columnStartX = MutableList(columnWidths.size) { 0 }
        for (index in 1 until columnWidths.size) {
            columnStartX[index] = columnStartX[index - 1] + columnWidths[index - 1] + distance * 2 + 1
        }

        return LayoutMetrics(iconSize, borderWidth, borderPadding, distance, nodeHeight, columnWidths, columnStartX)
    }

    private fun clampViewport(nodes: NodeList = NODES, layout: LayoutMetrics = layoutMetrics()) {
        val contentWidth = if (layout.columnWidths.isEmpty()) {
            0
        } else {
            layout.columnWidths.sum() + (layout.columnWidths.size - 1) * (layout.distance * 2 + 1)
        }
        val maxRows = nodes.byPos.maxOfOrNull { it.size } ?: 0
        val contentHeight = if (maxRows == 0) {
            0
        } else {
            maxRows * layout.nodeHeight + (maxRows - 1) * layout.distance
        }

        val maxViewportX = (contentWidth - width).coerceAtLeast(0)
        val maxViewportY = (contentHeight - height).coerceAtLeast(0)
        viewportX = viewportX.coerceIn(0, maxViewportX)
        viewportY = viewportY.coerceIn(0, maxViewportY)
    }

    private fun findNodeAt(mouseX: Int, mouseY: Int, nodes: NodeList = NODES, layout: LayoutMetrics = layoutMetrics()): ResourceKey<TechNode>? {
        val viewportRight = x + width
        val viewportBottom = y + height
        if (mouseX !in x until viewportRight || mouseY !in y until viewportBottom) {
            return null
        }

        fun isVisible(left: Int, top: Int, right: Int, bottom: Int): Boolean {
            return right > x && left < viewportRight && bottom > y && top < viewportBottom
        }

        for ((columnIndex, column) in nodes.byPos.withIndex()) {
            val nodeWidth = layout.columnWidths.getOrElse(columnIndex) { 0 }
            val worldLeft = layout.columnStartX[columnIndex]
            val screenLeft = x + worldLeft - viewportX
            val screenRight = screenLeft + nodeWidth

            for ((rowIndex, node) in column.withIndex()) {
                val worldTop = rowIndex * (layout.nodeHeight + layout.distance)
                val screenTop = y + worldTop - viewportY
                val screenBottom = screenTop + layout.nodeHeight
                if (!isVisible(screenLeft, screenTop, screenRight, screenBottom)) continue

                if (mouseX in screenLeft until screenRight && mouseY in screenTop until screenBottom) {
                    return node.key
                }
            }
        }

        return null
    }

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        // Track last mouse position
        lastMouseX = mouseX.toDouble()
        lastMouseY = mouseY.toDouble()
        
        guiGraphics.enableScissor(x, y, x + width, y + height)
        try {
            val font = Minecraft.getInstance().font
            val nodes = NODES
            val layout = layoutMetrics()
            clampViewport(nodes, layout)
            val iconSize = layout.iconSize // 节点图标大小
            val borderWidth = layout.borderWidth // 节点边框宽度
            val borderPadding = layout.borderPadding // 节点边框内边距
            val distance = layout.distance // 节点间隙宽度
            val nodeHeight = layout.nodeHeight
            val columnWidths = layout.columnWidths
            val columnStartX = layout.columnStartX

            val viewportRight = x + width
            val viewportBottom = y + height
            fun isVisible(left: Int, top: Int, right: Int, bottom: Int): Boolean {
                return right > x && left < viewportRight && bottom > y && top < viewportBottom
            }

            val hoveredNode = findNodeAt(mouseX, mouseY, nodes, layout)

            // Update tooltip
            if (hoveredNode != hoveredNodeKey) {
                hoveredNodeKey = hoveredNode
                if (hoveredNode != null) {
                    this.tooltip = Tooltip.create(fullNodeTooltip(hoveredNode))
                } else {
                    this.tooltip = null
                }
            }

            val hoveredPrerequisites = hoveredNode?.let { key ->
                nodes.byKey[key]?.node?.prerequisites?.toSet().orEmpty()
            } ?: emptySet()

            val separatorColor = ColorUtil.rgb(0x505050).rgba()
            for (columnIndex in 0 until columnWidths.lastIndex) {
                val separatorWorldX = columnStartX[columnIndex] + columnWidths[columnIndex] + distance
                val separatorScreenX = x + separatorWorldX - viewportX
                if (separatorScreenX in x until viewportRight) {
                    guiGraphics.vLine(separatorScreenX, y, viewportBottom - 1, separatorColor)
                }
            }

            for ((columnIndex, column) in nodes.byPos.withIndex()) {
                val nodeWidth = columnWidths.getOrElse(columnIndex) { 0 }
                val worldLeft = columnStartX[columnIndex]
                val screenLeft = x + worldLeft - viewportX
                val screenRight = screenLeft + nodeWidth

                for ((rowIndex, node) in column.withIndex()) {
                    val worldTop = rowIndex * (nodeHeight + distance)
                    val screenTop = y + worldTop - viewportY
                    val screenBottom = screenTop + nodeHeight
                    if (!isVisible(screenLeft, screenTop, screenRight, screenBottom)) continue

                    val outlineColor = when (node.key) {
                        hoveredNode -> borderColorHover(node.key)
                        in hoveredPrerequisites -> borderColorHoverPrerequisite(node.key)
                        else -> borderColor(node.key)
                    }.rgba()

                    guiGraphics.fill(screenLeft, screenTop, screenRight, screenBottom, outlineColor)

                    val innerLeft = screenLeft + borderWidth
                    val innerTop = screenTop + borderWidth
                    val innerRight = screenRight - borderWidth
                    val innerBottom = screenBottom - borderWidth
                    guiGraphics.fill(innerLeft, innerTop, innerRight, innerBottom, ColorUtil.rgb(0x202020).rgba())

                    val contentLeft = innerLeft + borderPadding
                    val contentTop = innerTop + borderPadding
                    val contentHeight = innerBottom - innerTop - borderPadding * 2

                    val iconX = contentLeft
                    val iconY = contentTop + (contentHeight - iconSize) / 2
                    guiGraphics.renderItem(node.node.icon, iconX, iconY)

                    val textX = iconX + iconSize + borderPadding
                    val textY = contentTop + (contentHeight - font.lineHeight) / 2
                    guiGraphics.drawString(font, nodeName(node.key), textX, textY, 0xffffff, false)
                }
            }
        } finally {
            guiGraphics.disableScissor()
        }
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {}

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0 || !active || !visible || !isMouseOver(mouseX, mouseY)) {
            return false
        }

        draggingViewport = true
        draggedSinceClick = false
        dragStartMouseX = mouseX
        dragStartMouseY = mouseY
        dragLastMouseX = mouseX
        dragLastMouseY = mouseY
        return true
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (button != 0 || !draggingViewport) {
            return false
        }

        val deltaX = mouseX - dragLastMouseX
        val deltaY = mouseY - dragLastMouseY
        if (deltaX != 0.0 || deltaY != 0.0) {
            viewportX = (viewportX - deltaX).toInt()
            viewportY = (viewportY - deltaY).toInt()
            clampViewport()
            if (abs(mouseX - dragStartMouseX) > 2.0 || abs(mouseY - dragStartMouseY) > 2.0) {
                draggedSinceClick = true
            }
        }

        dragLastMouseX = mouseX
        dragLastMouseY = mouseY
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0 || !draggingViewport) {
            return false
        }

        draggingViewport = false
        val shouldClick = !draggedSinceClick
        draggedSinceClick = false

        if (shouldClick) {
            findNodeAt(mouseX.toInt(), mouseY.toInt())?.let { onNodeClick(it) }
        }

        return true
    }

    private var hoveredNodeKey: ResourceKey<TechNode>? = null

    override fun getRectangle(): ScreenRectangle {
        val nodes = NODES
        val layout = layoutMetrics()
        val hoveredNode = findNodeAt(lastMouseX.toInt(), lastMouseY.toInt(), nodes, layout)
        
        if (hoveredNode != null) {
            val nodePos = nodes.byKey[hoveredNode] ?: return super.getRectangle()
            val columnIndex = nodePos.x
            val rowIndex = nodePos.y
            val nodeWidth = layout.columnWidths.getOrElse(columnIndex) { 0 }
            val nodeHeight = layout.nodeHeight
            val worldLeft = layout.columnStartX[columnIndex]
            val worldTop = rowIndex * (nodeHeight + layout.distance)
            val screenLeft = x + worldLeft - viewportX
            val screenTop = y + worldTop - viewportY
            return ScreenRectangle(screenLeft, screenTop, nodeWidth, nodeHeight)
        }
        
        return super.getRectangle()
    }
    
    companion object {
        private var cachedNodes: NodeList? = null
        private var cachedSaveIdentity: String? = null
        private var cachedNodeWidths: List<Int>? = null

        val NODES: NodeList
            get() {
                val mc = Minecraft.getInstance()
                val level = mc.level
                if (level == null) {
                    cachedNodes = null
                    cachedSaveIdentity = null
                    cachedNodeWidths = null
                    throw RuntimeException("Trying to render tech tree without a level")
                }

                val currentSaveIdentity = buildSaveIdentity(level)
                val currentCache = cachedNodes
                if (currentCache != null && cachedSaveIdentity == currentSaveIdentity) {
                    return currentCache
                }

                cachedNodeWidths = null
                val rebuilt = buildNodes(level)
                cachedNodes = rebuilt
                cachedSaveIdentity = currentSaveIdentity
                return rebuilt
            }

        private fun buildSaveIdentity(level: ClientLevel): String {
            // Use server identity when possible; fallback to loaded level instance identity.
            val mc = Minecraft.getInstance()
            val integrated = mc.singleplayerServer
            if (integrated != null) {
                return "singleplayer:${integrated.worldData.levelName}"
            }

            val remote = mc.currentServer
            if (remote != null) {
                return "multiplayer:${remote.ip}"
            }

            return "level:${System.identityHashCode(level)}"
        }

        private fun buildNodes(level: ClientLevel): NodeList {
            val registry = level.registryAccess().registryOrThrow(DataRegistryLoader.TECH_NODE)

            val entries = registry.entrySet().sortedBy { it.key.location().toString() }
            val nodesByKey = entries.associate { it.key to it.value }
            val dependentsByKey = mutableMapOf<ResourceKey<TechNode>, MutableList<ResourceKey<TechNode>>>()

            for ((key, node) in nodesByKey) {
                dependentsByKey.putIfAbsent(key, mutableListOf())
                for (prerequisite in node.prerequisites) {
                    if (!nodesByKey.containsKey(prerequisite)) {
                        throw IllegalStateException(
                            "Tech node ${key.location()} depends on missing prerequisite ${prerequisite.location()}"
                        )
                    }
                    dependentsByKey.getOrPut(prerequisite) { mutableListOf() }.add(key)
                }
            }

            val xByKey = mutableMapOf<ResourceKey<TechNode>, Int>()
            val visitState = mutableMapOf<ResourceKey<TechNode>, Int>()
            val stack = mutableListOf<ResourceKey<TechNode>>()

            fun computeX(key: ResourceKey<TechNode>): Int {
                when (visitState[key]) {
                    1 -> {
                        val start = stack.indexOf(key).coerceAtLeast(0)
                        val cycle = (stack.subList(start, stack.size) + key).joinToString(" -> ") { it.location().toString() }
                        throw IllegalStateException("Cycle dependency detected in tech tree: $cycle")
                    }

                    2 -> return xByKey.getValue(key)
                }

                visitState[key] = 1
                stack.add(key)

                val node = nodesByKey.getValue(key)
                val x = if (node.prerequisites.isEmpty()) {
                    0
                } else {
                    node.prerequisites.maxOf { prerequisite -> computeX(prerequisite) } + 1
                }

                stack.removeAt(stack.lastIndex)
                visitState[key] = 2
                xByKey[key] = x
                return x
            }

            val sortedKeys = nodesByKey.keys.sortedBy { it.location().toString() }
            for (key in sortedKeys) {
                computeX(key)
            }

            val layers = mutableMapOf<Int, MutableList<ResourceKey<TechNode>>>()
            for (key in sortedKeys) {
                layers.getOrPut(xByKey.getValue(key)) { mutableListOf() }.add(key)
            }

            val yByKey = mutableMapOf<ResourceKey<TechNode>, Int>()
            fun refreshLayerY() {
                for ((_, keys) in layers) {
                    keys.forEachIndexed { index, key -> yByKey[key] = index }
                }
            }
            refreshLayerY()

            val maxX = xByKey.values.maxOrNull() ?: 0
            repeat(8) {
                for (x in 0..maxX) {
                    val keys = layers[x] ?: continue
                    val ranked = keys.mapIndexed { index, key ->
                        val prerequisites = nodesByKey.getValue(key).prerequisites
                        val anchors = if (prerequisites.isNotEmpty()) {
                            prerequisites.map { yByKey.getValue(it) }
                        } else {
                            dependentsByKey[key].orEmpty().map { yByKey.getValue(it) }
                        }
                        val target = if (anchors.isEmpty()) index.toDouble() else anchors.average()
                        Triple(key, target, index)
                    }.sortedWith(compareBy<Triple<ResourceKey<TechNode>, Double, Int>> { it.second }
                        .thenBy { it.first.location().toString() })

                    keys.clear()
                    keys.addAll(ranked.map { it.first })
                    refreshLayerY()
                }

                for (x in maxX downTo 0) {
                    val keys = layers[x] ?: continue
                    val ranked = keys.mapIndexed { index, key ->
                        val dependents = dependentsByKey[key].orEmpty()
                        val anchors = if (dependents.isNotEmpty()) {
                            dependents.map { yByKey.getValue(it) }
                        } else {
                            nodesByKey.getValue(key).prerequisites.map { yByKey.getValue(it) }
                        }
                        val target = if (anchors.isEmpty()) index.toDouble() else anchors.average()
                        Triple(key, target, index)
                    }.sortedWith(compareBy<Triple<ResourceKey<TechNode>, Double, Int>> { it.second }
                        .thenBy { it.first.location().toString() })

                    keys.clear()
                    keys.addAll(ranked.map { it.first })
                    refreshLayerY()
                }
            }

            fun totalDistance(): Long {
                var sum = 0L
                for ((key, node) in nodesByKey) {
                    val x = xByKey.getValue(key)
                    val y = yByKey.getValue(key)
                    for (prerequisite in node.prerequisites) {
                        val prerequisiteX = xByKey.getValue(prerequisite)
                        val prerequisiteY = yByKey.getValue(prerequisite)
                        sum += abs(x - prerequisiteX).toLong() + abs(y - prerequisiteY).toLong()
                    }
                }
                return sum
            }

            for (x in 0..maxX) {
                val keys = layers[x] ?: continue
                if (keys.size < 2) continue

                var improved = true
                var guard = 0
                while (improved && guard++ < keys.size * keys.size) {
                    improved = false
                    var i = 0
                    while (i < keys.lastIndex) {
                        val before = totalDistance()
                        keys[i] = keys[i + 1].also { keys[i + 1] = keys[i] }
                        refreshLayerY()
                        val after = totalDistance()
                        if (after < before) {
                            improved = true
                            i++
                        } else {
                            keys[i] = keys[i + 1].also { keys[i + 1] = keys[i] }
                            refreshLayerY()
                        }
                        i++
                    }
                }
            }

            refreshLayerY()

            val byPos = mutableListOf<List<NodeWithPos>>()
            val byKey = mutableMapOf<ResourceKey<TechNode>, NodeWithPos>()
            for (x in 0..maxX) {
                val keys = layers[x].orEmpty()
                val row = keys.mapIndexed { y, key ->
                    NodeWithPos(nodesByKey.getValue(key), x, y, key).also { byKey[key] = it }
                }
                byPos.add(row)
            }

            return NodeList(byPos, byKey)
        }

        fun nodeName(node: ResourceKey<TechNode>): Component {
            val loc = node.location()
            return Component.translatable("tech_node.${loc.namespace}.${loc.path}.name")
        }

        fun nodeTooltip(node: ResourceKey<TechNode>): Component {
            val loc = node.location()
            return Component.translatable("tech_node.${loc.namespace}.${loc.path}.tooltip")
        }

        fun nodeWidths(): List<Int> {
            if (cachedNodeWidths == null) {
                cachedNodeWidths = NODES.byPos.map { column ->
                    var maxValue = 0
                    for (node in column) {
                        val name = nodeName(node.key)
                        val renderedWidth = Minecraft.getInstance().font.width(name.string)
                        if (renderedWidth > maxValue) maxValue = renderedWidth
                    }
                    maxValue
                }
            }
            return cachedNodeWidths!!
        }
    }
}