package com.mazenrashed.example

import android.graphics.Bitmap
import com.mazenrashed.printooth.data.PrintingImagesHelper
import com.woosim.printer.WoosimImage

class WoosimPrintingImagesHelper : PrintingImagesHelper{
    override fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        return WoosimImage.printRGBbitmap(0, 0, 384, bitmap.height, bitmap)
    }

}