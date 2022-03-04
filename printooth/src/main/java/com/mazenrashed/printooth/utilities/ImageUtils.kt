@file:Suppress("NAME_SHADOWING")

package com.mazenrashed.printooth.utilities

import android.graphics.Bitmap
import android.util.Log

import java.util.ArrayList

object ImageUtils {
    private const val hexStr = "0123456789ABCDEF"
    private val binaryArray = arrayOf("0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111")

    fun decodeBitmap(bmp: Bitmap): ByteArray? {
        val bmpWidth = bmp.width
        val bmpHeight = bmp.height

        val list = ArrayList<String>() //binaryString list
        var sb: StringBuffer

        val zeroCount = bmpWidth % 8

        var zeroStr = ""
        if (zeroCount > 0) {
            for (i in 0 until 8 - zeroCount) {
                zeroStr += "0"
            }
        }

        for (i in 0 until bmpHeight) {
            sb = StringBuffer()
            for (j in 0 until bmpWidth) {
                val color = bmp.getPixel(j, i)

                val r = color shr 16 and 0xff
                val g = color shr 8 and 0xff
                val b = color and 0xff

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0")
                else
                    sb.append("1")
            }
            if (zeroCount > 0) {
                sb.append(zeroStr)
            }
            list.add(sb.toString())
        }

        val bmpHexList = binaryListToHexStringList(list)
        val commandHexString = "1D763000"        

        val printWidth = if (bmpWidth % 8 == 0) bmpWidth / 8 else bmpWidth / 8 + 1
        var printLowWidthHex = Integer.toHexString(getLowValue(printWidth))
        var printHighWidthHex = Integer.toHexString(getHighValue(printWidth))
        if (printHighWidthHex.length > 2) {
            Log.e("decodeBitmap error", " width is too large")
            return null
        } else if (printHighWidthHex.length == 1) {
            printHighWidthHex = "0$printHighWidthHex"
        }
        if (printLowWidthHex.length == 1) {
            printLowWidthHex = "0$printLowWidthHex"
        }

        var printLowHeightHex = Integer.toHexString(getLowValue(bmpHeight))
        var printHighHeightHex = Integer.toHexString(getHighValue(bmpHeight))
        if (printHighHeightHex.length > 2) {
            Log.e("decodeBitmap error", " height is too large")
            return null
        } else if (printHighHeightHex.length == 1) {
            printHighHeightHex = "0$printHighHeightHex"
        }
        if (printLowHeightHex.length == 1) {
            printLowHeightHex = "0$printLowHeightHex"
        }

        val commandList = ArrayList<String>()
        commandList.add(commandHexString + printLowWidthHex + printHighWidthHex + printLowHeightHex + printHighHeightHex)
        commandList.addAll(bmpHexList)

        return hexList2Byte(commandList)
    }

    private fun binaryListToHexStringList(list: List<String>): List<String> {
        val hexList = ArrayList<String>()
        for (binaryStr in list) {
            val sb = StringBuffer()
            var i = 0
            while (i < binaryStr.length) {
                val str = binaryStr.substring(i, i + 8)

                val hexString = myBinaryStrToHexString(str)
                sb.append(hexString)
                i += 8
            }
            hexList.add(sb.toString())
        }
        return hexList

    }

    private fun myBinaryStrToHexString(binaryStr: String): String {
        var hex = ""
        val f4 = binaryStr.substring(0, 4)
        val b4 = binaryStr.substring(4, 8)
        for (i in 0 until binaryArray.size) {
            if (f4 == binaryArray[i])
                hex += hexStr.substring(i, i + 1)
        }
        for (i in 0 until binaryArray.size) {
            if (b4 == binaryArray[i])
                hex += hexStr.substring(i, i + 1)
        }

        return hex
    }

    private fun hexList2Byte(list: List<String>): ByteArray {
        val commandList = ArrayList<ByteArray>()

        for (hexStr in list) {
            commandList.add(hexStringToBytes(hexStr)!!)
        }
        return sysCopy(commandList)
    }

    private fun hexStringToBytes(hexString: String?): ByteArray? {
        var hexString = hexString
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    fun sysCopy(srcArrays: List<ByteArray>): ByteArray {
        var len = 0
        for (srcArray in srcArrays) {
            len += srcArray.size
        }
        val destArray = ByteArray(len)
        var destLen = 0
        for (srcArray in srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.size)
            destLen += srcArray.size
        }
        return destArray
    }

    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
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
