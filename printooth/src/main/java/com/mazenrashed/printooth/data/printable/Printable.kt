package com.mazenrashed.printooth.data.printable

import com.mazenrashed.printooth.data.printer.Printer

interface Printable {
    fun getPrintableByteArray(printer: Printer): List<ByteArray>
}
