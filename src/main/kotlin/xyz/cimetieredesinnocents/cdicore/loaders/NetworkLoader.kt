package xyz.cimetieredesinnocents.cdicore.loaders

import xyz.cimetieredesinnocents.cdicore.network.TechSyncPackets
import xyz.cimetieredesinnocents.cdilib.loaders.NetworkLoaderFactory

object NetworkLoader : NetworkLoaderFactory() {
    val TECH_SYNC_SET_RESEARCHING = register(TechSyncPackets.SetResearching)
    val TECH_SYNC_REMOVE_RESEARCHING = register(TechSyncPackets.RemoveResearching)
    val TECH_SYNC_ADD_RESEARCHED = register(TechSyncPackets.AddResearched)
    val TECH_SYNC_REMOVE_RESEARCHED = register(TechSyncPackets.RemoveResearched)
    val TECH_SYNC_INIT = register(TechSyncPackets.Init)
    val TECH_SYNC_INSIGHT = register(TechSyncPackets.SyncInsight)
}