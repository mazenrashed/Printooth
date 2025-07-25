package com.mazenrashed.printooth.data

import android.graphics.Bitmap
import android.util.Log
import com.mazenrashed.printooth.utilities.ImageUtils

class DefaultPrintingImagesHelper : PrintingImagesHelper {

    override fun getBitmapAsByteArray(bitmap: Bitmap): List<ByteArray>? {
        return decodeBitmap(bitmap)
    }

    private fun decodeBitmap(bmp: Bitmap): List<ByteArray>? {
        val bmpWidth = bmp.width
        val bmpHeight = bmp.height

        val printWidth = if (bmpWidth % 8 == 0) bmpWidth / 8 else bmpWidth / 8 + 1

        val printHighWidth = printWidth / 256
        val printLowWidth = printWidth % 256

        val printHighHeight = bmpHeight / 256
        val printLowHeight = bmpHeight % 256

        // Note: consider limits according printer
        if (printHighWidth >= 2) {
            Log.e("decodeBitmap error", " width is too large")
            return null
        }

        // Note: consider limits according printer
        if (printHighHeight >= 4) {
            Log.e("decodeBitmap error", " height is too large")
            return null
        }

        val commandsBuilder = mutableListOf<Byte>().apply {
            addAll("1D763000".chunked(2).map { it.toInt(16).toByte() })
            add(printLowWidth.toByte())
            add(printHighWidth.toByte())
            add(printLowHeight.toByte())
            add(printHighHeight.toByte())
        }

        val sb = StringBuilder()

        val zeroCount = bmpWidth % 8
        val zeroStr = if (zeroCount > 0) "0".repeat(8 - zeroCount) else ""

        for (i in 0 until bmpHeight) {
            sb.clear()

            for (j in 0 until bmpWidth) {
                sb.append(ImageUtils.buildDot(bmp.getPixel(j, i)))
            }

            if (zeroCount > 0) {
                sb.append(zeroStr)
            }

            sb.chunked(8).forEach {
                commandsBuilder.add(it.toInt(2).toByte())
            }
        }

        return listOf(commandsBuilder.toByteArray())
    }
}