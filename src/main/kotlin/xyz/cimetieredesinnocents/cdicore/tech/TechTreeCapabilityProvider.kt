package xyz.cimetieredesinnocents.cdicore.tech

import xyz.cimetieredesinnocents.cdicore.loaders.PlayerCapabilityLoader
import xyz.cimetieredesinnocents.cdilib.player.PlayerCapabilityProvider

object TechTreeCapabilityProvider : PlayerCapabilityProvider<ITechTreeCapability, TechTreeCapability>(
    ITechTreeCapability::class.java,
    ::TechTreeCapability,
    { PlayerCapabilityLoader.TECH_TREE }
)