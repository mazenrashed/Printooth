package com.mazenrashed.printooth.data.converter

/**
 * Default converter
 */
class DefaultConverter : Converter() {
    override fun convert(input: Char): Byte {
        if (input == '€') {
            return (0x80).toByte()
        }
        return input.toByte()
    }
}