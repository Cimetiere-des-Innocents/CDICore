package xyz.cimetieredesinnocents.cdicore.item

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import xyz.cimetieredesinnocents.cdicore.gui.tech.PlayerTechTreeRenderer

class CDIDebugStickItem : Item(Properties()) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        if (!level.isClientSide) {
            return super.use(level, player, usedHand)
        }

        Minecraft.getInstance().setScreen(object : Screen(Component.empty()) {
            val techTreeRenderer = object : PlayerTechTreeRenderer(10, 10, 200, 200, player) {
            }
            init {
                addRenderableWidget(techTreeRenderer)
            }
        })

        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }
}