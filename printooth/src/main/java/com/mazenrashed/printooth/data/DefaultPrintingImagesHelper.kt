package com.mazenrashed.printooth.data

import android.graphics.Bitmap
import com.mazenrashed.printooth.utilities.ImageUtils

class DefaultPrintingImagesHelper:PrintingImagesHelper{
    override fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        return ImageUtils.decodeBitmap(bitmap)!!
    }

}