package com.mazenrashed.printooth.data.converter

/**
 * Base class to convert a string into a byte array
 */
abstract class Converter {
    fun toByteArray(input: String): ByteArray {
        val text = convert(input)
        return try {
            var i = 0
            val bytes = ByteArray(text.length)
            for (c in text.toCharArray()) {
                bytes[i++] = convert(c)
            }
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            byteArrayOf()
        }
    }

    protected open fun convert(input: String): String {
        return input
    }

    protected open fun convert(input: Char): Byte {
        return input.toByte()
    }
}