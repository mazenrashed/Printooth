package com.mazenrashed.printooth.data.converter

import com.mazenrashed.printooth.utilities.UniCode864Mapping

/**
 * Default converter
 */
class ArabicConverter : Converter() {

    override fun convert(input: String): String {
        return UniCode864Mapping().getArabicString(input)
    }

    private fun isProbablyArabic(s: String): Boolean {
        var i = 0
        while (i < s.length) {
            val c = s.codePointAt(i)
            if (c in 0x0600..0x06E0)
                return true
            i += Character.charCount(c)
        }
        return false
    }
}