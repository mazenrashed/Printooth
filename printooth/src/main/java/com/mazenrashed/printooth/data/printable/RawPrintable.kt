package com.mazenrashed.printooth.data.printable

import com.mazenrashed.printooth.data.printer.Printer

data class RawPrintable private constructor(val command: ByteArray,
                                            val newLinesAfter: Int) : Printable {

    override fun getPrintableByteArray(printer: Printer): List<ByteArray> {
        val operations = mutableListOf(command)

        if (newLinesAfter > 0) {
            operations.add(printer.feedLineCommand.plus(newLinesAfter.toByte()))
        }

        return operations
    }

    class Builder(private var raw: ByteArray) {
        private var newLinesAfter = 0

        fun setNewLinesAfter(lines: Int): Builder {
            this.newLinesAfter = lines
            return this
        }

        fun build(): RawPrintable {
            return RawPrintable(raw, newLinesAfter)
        }
    }

}
