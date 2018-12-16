package com.mazenrashed.printooth.data

import android.graphics.Bitmap

interface PrintingImagesHelper {
    fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray
}