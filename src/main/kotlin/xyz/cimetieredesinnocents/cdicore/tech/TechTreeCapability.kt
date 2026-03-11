package xyz.cimetieredesinnocents.cdicore.tech

import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor
import xyz.cimetieredesinnocents.cdicore.config.Config
import xyz.cimetieredesinnocents.cdicore.loaders.DataAttachmentLoader
import xyz.cimetieredesinnocents.cdicore.loaders.DataRegistryLoader
import xyz.cimetieredesinnocents.cdicore.loaders.GameRuleLoader
import xyz.cimetieredesinnocents.cdicore.loaders.NetworkLoader
import kotlin.jvm.optionals.getOrNull

class TechTreeCapability(override var player: Player) : ITechTreeCapability {
    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    private val techHolder: PlayerTechHolder get() = player.getData(DataAttachmentLoader.PLAYER_TECH)!!
    private val isClient get() = player.level().isClientSide

    override val researching = object : MutableMap<ResourceKey<TechNode>, PlayerTechHolder.ResearchingTech> by techHolder.researchingTech {
        override fun clear() {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            for (entry in techHolder.researchingTech) {
                PacketDistributor.sendToPlayer(player as ServerPlayer, NetworkLoader.TECH_SYNC_REMOVE_RESEARCHING.packet {
                    it.key = entry.key
                })
            }

            techHolder.researchingTech.clear()
        }

        override fun put(
            key: ResourceKey<TechNode>,
            value: PlayerTechHolder.ResearchingTech
        ): PlayerTechHolder.ResearchingTech? {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            PacketDistributor.sendToPlayer(player as ServerPlayer, NetworkLoader.TECH_SYNC_SET_RESEARCHING.packet {
                it.key = key
                it.value = value
            })

            return techHolder.researchingTech.put(key, value)
        }

        override fun putAll(from: Map<out ResourceKey<TechNode>, PlayerTechHolder.ResearchingTech>) {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            for (entry in from) {
                PacketDistributor.sendToPlayer(player as ServerPlayer, NetworkLoader.TECH_SYNC_SET_RESEARCHING.packet {
                    it.key = entry.key
                    it.value = entry.value
                })
            }

            techHolder.researchingTech.putAll(from)
        }

        override fun remove(key: ResourceKey<TechNode>): PlayerTechHolder.ResearchingTech? {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            if (key in techHolder.researchingTech) {
                PacketDistributor.sendToPlayer(
                    player as ServerPlayer,
                    NetworkLoader.TECH_SYNC_REMOVE_RESEARCHING.packet {
                        it.key = key
                    }
                )
            }

            return techHolder.researchingTech.remove(key)
        }
    }

    override val researched = object : MutableSet<ResourceKey<TechNode>> by techHolder.researchedTech {
        override fun add(element: ResourceKey<TechNode>): Boolean {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            PacketDistributor.sendToPlayer(player as ServerPlayer, NetworkLoader.TECH_SYNC_ADD_RESEARCHED.packet {
                it.key = element
            })

            return techHolder.researchedTech.add(element)
        }

        override fun addAll(elements: Collection<ResourceKey<TechNode>>): Boolean {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            for (key in elements) {
                PacketDistributor.sendToPlayer(player as ServerPlayer, NetworkLoader.TECH_SYNC_ADD_RESEARCHED.packet {
                    it.key = key
                })
            }

            return techHolder.researchedTech.addAll(elements)
        }

        override fun clear() {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            for (key in techHolder.researchedTech) {
                PacketDistributor.sendToPlayer(player as ServerPlayer, NetworkLoader.TECH_SYNC_REMOVE_RESEARCHED.packet {
                    it.key = key
                })
            }

            techHolder.researchedTech.clear()
        }

        override fun remove(element: ResourceKey<TechNode>): Boolean {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            if (element in techHolder.researchedTech) {
                PacketDistributor.sendToPlayer(
                    player as ServerPlayer,
                    NetworkLoader.TECH_SYNC_REMOVE_RESEARCHED.packet {
                        it.key = element
                    }
                )
            }

            return techHolder.researchedTech.remove(element)
        }

        override fun removeAll(elements: Collection<ResourceKey<TechNode>>): Boolean {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            for (key in elements) {
                if (key in techHolder.researchedTech) {
                    PacketDistributor.sendToPlayer(
                        player as ServerPlayer,
                        NetworkLoader.TECH_SYNC_REMOVE_RESEARCHED.packet {
                            it.key = key
                        }
                    )
                }
            }

            return techHolder.researchedTech.removeAll(elements.toSet())
        }

        override fun retainAll(elements: Collection<ResourceKey<TechNode>>): Boolean {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech tree")
            }

            val keys = elements.toSet()
            for (key in techHolder.researchedTech) {
                if (key !in keys) {
                    PacketDistributor.sendToPlayer(
                        player as ServerPlayer,
                        NetworkLoader.TECH_SYNC_REMOVE_RESEARCHED.packet {
                            it.key = key
                        }
                    )
                }
            }

            return techHolder.researchedTech.retainAll(keys)
        }
    }

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    override var forgetCounter: Int
        get() = player.getData(DataAttachmentLoader.FORGET_COUNTER)!!
        set(value) { player.setData(DataAttachmentLoader.FORGET_COUNTER, value) }

    override var insightTechPoints: Int
        get() = player.getData(DataAttachmentLoader.INSIGHT_TECH_POINTS)!!
        set(value) {
            if (isClient) {
                throw RuntimeException("Trying to modify client tech points")
            }

            player.setData(DataAttachmentLoader.INSIGHT_TECH_POINTS, value)
            PacketDistributor.sendToPlayer(
                player as ServerPlayer,
                NetworkLoader.TECH_SYNC_INSIGHT.packet {
                it.value = value
            })
        }

    private fun handleForgetting() {
        if (!player.level().gameRules.getRule(GameRuleLoader.ENABLE_FORGETTING).get()) return
        forgetCounter++
        if (forgetCounter == 20) {
            forgetCounter = 0
            val randomValue = player.random.nextInt(Config.averageForgetTime)
            if (randomValue == 0) {
                forgetOne()
            }
        }
    }

    override fun tick() {
        handleForgetting()
    }

    override fun afterRespawn() {
        if (player.level().isClientSide) return

        if (player.level().gameRules.getRule(GameRuleLoader.ENABLE_FORGETTING).get()) {
            researching.clear()
            researched.clear()
        }
    }

    override fun forgetOne() {
        if (researched.isEmpty()) return
        researched.remove(researched.random())
    }

    override fun research(
        tech: ResourceKey<TechNode>,
        theoryPoints: Int,
        appliedMaterials: List<ItemStack>,
        reverseEngineeringPoints: Int
    ) {
        if (isClient) {
            throw RuntimeException("Trying to modify client tech tree")
        }
        if (tech in researching) {
            val researchingTech = researching[tech]!!
            val newTech = researchingTech.research(theoryPoints, appliedMaterials, reverseEngineeringPoints)
            if (newTech.finished) {
                researching.remove(tech)
                researched.add(tech)
            } else {
                researching[tech] = newTech
            }
        } else {
            val lp = player.level().holderLookup(DataRegistryLoader.TECH_NODE)
            val node = lp.get(tech).getOrNull()?.value() ?: return
            val researchingTech = PlayerTechHolder.ResearchingTech(node.techPoints)
            val newTech = researchingTech.research(theoryPoints, appliedMaterials, reverseEngineeringPoints)
            if (newTech.finished) {
                researched.add(tech)
            } else {
                researching[tech] = newTech
            }
        }
    }

    override fun memorize(tech: ResourceKey<TechNode>) {
        researched.add(tech)
    }
}