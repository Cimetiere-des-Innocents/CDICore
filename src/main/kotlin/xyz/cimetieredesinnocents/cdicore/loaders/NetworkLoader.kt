package xyz.cimetieredesinnocents.cdicore.loaders

import xyz.cimetieredesinnocents.cdicore.network.TechSyncPacket
import xyz.cimetieredesinnocents.cdilib.loaders.NetworkLoaderFactory

object NetworkLoader : NetworkLoaderFactory() {
    val TECH_SYNC_SET_RESEARCHING = register(TechSyncPacket.SetResearching)
    val TECH_SYNC_REMOVE_RESEARCHING = register(TechSyncPacket.RemoveResearching)
    val TECH_SYNC_ADD_RESEARCHED = register(TechSyncPacket.AddResearched)
    val TECH_SYNC_REMOVE_RESEARCHED = register(TechSyncPacket.RemoveResearched)
    val TECH_SYNC_INIT = register(TechSyncPacket.Init)
}