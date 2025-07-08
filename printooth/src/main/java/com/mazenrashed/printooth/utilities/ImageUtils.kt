package com.mazenrashed.printooth.utilities

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log

import java.util.Locale

object ImageUtils {
    private const val hexStr = "0123456789ABCDEF"
    private val binaryArray = arrayOf("0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111")

    fun decodeBitmap(bmp: Bitmap): ByteArray? {
        val bmpWidth = bmp.width
        val bmpHeight = bmp.height

        val printWidth = if (bmpWidth % 8 == 0) bmpWidth / 8 else bmpWidth / 8 + 1

        var printHighWidthHex = Integer.toHexString(getHighValue(printWidth))

        if (printHighWidthHex.length > 2) {
            Log.e("decodeBitmap error", " width is too large")
            return null
        } else if (printHighWidthHex.length == 1) {
            printHighWidthHex = "0$printHighWidthHex"
        }

        var printLowWidthHex = Integer.toHexString(getLowValue(printWidth))

        if (printLowWidthHex.length == 1) {
            printLowWidthHex = "0$printLowWidthHex"
        }

        var printHighHeightHex = Integer.toHexString(getHighValue(bmpHeight))

        if (printHighHeightHex.length > 2) {
            Log.e("decodeBitmap error", " height is too large")
            return null
        } else if (printHighHeightHex.length == 1) {
            printHighHeightHex = "0$printHighHeightHex"
        }

        var printLowHeightHex = Integer.toHexString(getLowValue(bmpHeight))

        if (printLowHeightHex.length == 1) {
            printLowHeightHex = "0$printLowHeightHex"
        }

        val commandHexString = "1D763000"

        val commandsBuilder = mutableListOf<Byte>()

        hexStringToBytes(commandHexString + printLowWidthHex + printHighWidthHex + printLowHeightHex + printHighHeightHex)?.let {
            commandsBuilder.addAll(it)
        }

        val sb = StringBuilder()

        val zeroCount = bmpWidth % 8
        val zeroStr = if (zeroCount > 0) "0".repeat(8 - zeroCount) else ""

        for (i in 0 until bmpHeight) {
            sb.clear()

            for (j in 0 until bmpWidth) {
                sb.append(buildColor(bmp.getPixel(j, i)))
            }

            if (zeroCount > 0) {
                sb.append(zeroStr)
            }

            hexStringToBytes(bitsToHexStringList(sb.toString()))?.let {
                commandsBuilder.addAll(it)
            }
        }

        return commandsBuilder.toByteArray()
    }

    private fun buildColor(pixel: Int): Char {
        val red = pixel shr 16 and 0xff
        val green = pixel shr 8 and 0xff
        val blue = pixel and 0xff

        return if (red > 160 && green > 160 && blue > 160) '0' else '1'
    }

    private fun bitsToHexStringList(value: String): String {
        val sb = StringBuilder()

        value.chunked(4).map { word ->
            binaryArray.indexOfFirst { it == word }.let {
                sb.append(hexStr.substring(it, it + 1))
            }
        }

        return sb.toString()
    }

    private fun hexStringToBytes(hexString: String?): List<Byte>? {
        var result: List<Byte>? = null

        if (!TextUtils.isEmpty(hexString)) {
            result = mutableListOf()

            hexString?.uppercase(Locale.getDefault())?.chunked(2)?.map {
                result.add((charToByte(it[0]) shl 4 or charToByte(it[1])).toByte())
            }
        }

        return result
    }

    private fun charToByte(c: Char): Int {
        return hexStr.indexOf(c)
    }

    private fun getLowValue(rawValue: Int): Int {
        var value = rawValue
        while (value > 255) {
            value -= 256
        }
        return value
    }

    private fun getHighValue(rawValue: Int): Int {
        var value = rawValue
        var res = 0
        while (value > 255) {
            value -= 256
            res++
        }
        return res
    }
}
