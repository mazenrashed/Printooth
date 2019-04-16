package com.mazenrashed.printooth.data.printable

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.data.printer.Printer

data class ImagePrintable private constructor(val image: Bitmap,
                                              val alignment: Byte,
                                              val newLinesAfter: Int) : Printable {

    override fun getPrintableByteArray(printer: Printer): List<ByteArray> {
        val operations = mutableListOf(
                printer.justificationCommand.plus(alignment),
                printer.printingImagesHelper.getBitmapAsByteArray(image)
        )

        if (newLinesAfter > 0) {
            operations.add(printer.feedLineCommand.plus(newLinesAfter.toByte()))
        }

        return operations
    }

    class Builder() {
        private var alignment: Byte = DefaultPrinter.ALIGNMENT_LEFT
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
