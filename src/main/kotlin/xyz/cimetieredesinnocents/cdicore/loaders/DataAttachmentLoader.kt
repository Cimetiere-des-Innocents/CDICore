package xyz.cimetieredesinnocents.cdicore.loaders

import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdicore.tech.PlayerTechHolder
import xyz.cimetieredesinnocents.cdilib.loaders.DataAttachmentLoaderFactory

object DataAttachmentLoader : DataAttachmentLoaderFactory(CDICore.ID) {
    val PLAYER_TECH by registerRecord("player_tech", PlayerTechHolder.CODEC, true) { PlayerTechHolder(hashMapOf(), hashSetOf()) }
    val FORGET_COUNTER by registerInt("forget_counter")
    val INSIGHT_TECH_POINTS by registerInt("insight_tech_points")
}