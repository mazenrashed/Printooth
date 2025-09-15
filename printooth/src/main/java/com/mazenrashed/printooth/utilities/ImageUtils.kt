package com.mazenrashed.printooth.utilities

object ImageUtils {

    fun printPixel(pixel: Int): Boolean {
        val red = pixel shr 16 and 0xff
        val green = pixel shr 8 and 0xff
        val blue = pixel and 0xff

        return red <= 160 || green <= 160 || blue <= 160
    }

    fun buildDot(pixel: Int): Char {
        return if (printPixel(pixel)) '1' else '0'
    }
}
