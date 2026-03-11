package xyz.cimetieredesinnocents.cdicore.tech

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.ColorRGBA

data class ResearchTableTier(val color: ColorRGBA) {
    companion object {
        val CODEC = RecordCodecBuilder.create { it.group(
            ColorRGBA.CODEC.fieldOf("color").forGetter(ResearchTableTier::color)
        ).apply(it, ::ResearchTableTier) }
    }
}