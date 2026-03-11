package xyz.cimetieredesinnocents.cdicore.network

import net.minecraft.network.FriendlyByteBuf
import xyz.cimetieredesinnocents.cdicore.CDICore
import xyz.cimetieredesinnocents.cdilib.network.LibBasePacket

abstract class BasePacket<T : Any, B : FriendlyByteBuf>(name: String, direction: Direction, phase: Phase<B>) :
    LibBasePacket<T, B>(CDICore.ID, name, direction, phase)