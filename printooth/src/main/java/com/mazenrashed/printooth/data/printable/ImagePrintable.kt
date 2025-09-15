package com.mazenrashed.printooth.data.printable

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mazenrashed.printooth.data.printer.Printer

data class ImagePrintable private constructor(val image: Bitmap,
                                              val alignment: Byte,
                                              val newLinesAfter: Int) : Printable {

    override fun getPrintableByteArray(printer: Printer): List<ByteArray> {
        val operations = mutableListOf<ByteArray>()

        printer.getJustificationCommand(alignment)?.let {
            operations.add(it)
        }

        printer.getPrintingImagesHelper().getBitmapAsByteArray(image)?.forEach {
            operations.add(it)
        }

        if (newLinesAfter > 0) {
            printer.getFeedLinesCommand(newLinesAfter.toByte())?.let {
                operations.add(it)
            }
        }

        return operations
    }

    class Builder() {
        private var alignment: Byte = Printer.ALIGNMENT_LEFT
        private var newLinesAfter = 0
        private lateinit var image: Bitmap

        constructor(src: Int, resources: Resources) : this() {
            image = BitmapFactory.decodeResource(resources, src)
        }

        constructor(image: Bitmap) : this() {
            this.image = image
        }

        fun setAlignment(alignment: Byte): Builder {
            this.alignment = alignment
            return this
        }

        fun setNewLinesAfter(lines: Int): Builder {
            this.newLinesAfter = lines
            return this
        }

        fun build(): ImagePrintable {
            return ImagePrintable(image, alignment, newLinesAfter)
        }
    }
}
