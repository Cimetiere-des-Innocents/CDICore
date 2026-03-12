package xyz.cimetieredesinnocents.cdicore.loaders.datagen

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.tech.TechNode
import xyz.cimetieredesinnocents.cdicore.tech.TechNodeLoaderFactory

object TechNodeLoader : TechNodeLoaderFactory(CDICore.ID) {
    val TEST1 = register("test1") { TechNode(
        ItemStack(Items.APPLE),
        1,
        listOf(),
        TechNode.TechPoints(
            0,
            listOf(),
            0
        ),
        listOf()
    )}

    val TEST2 = register("test2") { TechNode(
        ItemStack(Items.APPLE),
        1,
        listOf(),
        TechNode.TechPoints(
            0,
            listOf(),
            0
        ),
        listOf()
    )}

    val TEST3 = register("test3") { TechNode(
        ItemStack(Items.APPLE),
        1,
        listOf(),
        TechNode.TechPoints(
            0,
            listOf(),
            0
        ),
        listOf()
    )}

    val TEST4 = register("test4") { TechNode(
        ItemStack(Items.APPLE),
        1,
        listOf(),
        TechNode.TechPoints(
            0,
            listOf(),
            0
        ),
        listOf(TEST1.key, TEST3.key)
    )}

    val TEST5 = register("test5") { TechNode(
        ItemStack(Items.APPLE),
        1,
        listOf(),
        TechNode.TechPoints(
            0,
            listOf(),
            0
        ),
        listOf(TEST2.key)
    )}
}