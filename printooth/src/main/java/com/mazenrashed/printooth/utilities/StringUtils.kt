@file:Suppress("NAME_SHADOWING")

package com.mazenrashed.printooth.utilities

object StringUtils {
    fun isProbablyArabic(s: String): Boolean {
        var i = 0
        while (i < s.length) {
            val c = s.codePointAt(i)
            if (c in 0x0600..0x06E0)
                return true
            i += Character.charCount(c)
        }
        return false
    }

    fun getStringAsByteArray(text: String): ByteArray {
        var text = UniCode864Mapping().getArabicString(text)
        return try {
            var i = 0
            val bytes = ByteArray(text.length)
            val chars = text.toCharArray()
            for (c in chars)
                bytes[i++] = c.toByte()
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            byteArrayOf()
        }
    }
}