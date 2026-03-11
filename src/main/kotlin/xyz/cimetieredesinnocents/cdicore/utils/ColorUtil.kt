package xyz.cimetieredesinnocents.cdicore.utils

import net.minecraft.util.ColorRGBA

object ColorUtil {
    private fun rgbaToArgb(color: Long): Int {
        val colorRGB = (color shr 8) and 0xffffff
        val colorA = color and 0xff
        val colorARGB = (colorA shl 24) or colorRGB
        return if (colorARGB > 0x7fffffff) (colorARGB - 0x100000000).toInt() else colorARGB.toInt()
    }

    fun rgba(r: Int, g: Int, b: Int, a: Int): ColorRGBA {
        return ColorRGBA(rgbaToArgb((r.toLong() shl 24) + (g.toLong() shl 16) + (b.toLong() shl 8) + a.toLong()))
    }

    fun rgba(rgba: Long): ColorRGBA {
        return ColorRGBA(rgbaToArgb(rgba))
    }

    fun rgba(rgb: Int, a: Int): ColorRGBA {
        return ColorRGBA(rgbaToArgb((rgb.toLong() shl 8) + a.toLong()))
    }

    fun rgb(r: Int, g: Int, b: Int): ColorRGBA {
        return rgba(r, g, b, 0xff)
    }

    fun rgb(rgb: Int): ColorRGBA {
        return rgba(rgb, 0xff)
    }
}