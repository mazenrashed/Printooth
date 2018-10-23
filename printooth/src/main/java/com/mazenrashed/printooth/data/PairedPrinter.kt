package com.mazenrashed.printooth.data

import io.paperdb.Paper
import java.io.Serializable

open class PairedPrinter(name: String?, address: String) : Serializable {
    var name: String? = name
    var address: String = address

    companion object {
        private const val PAIRED_PRINTER = "paired printer"

        fun getPairedPrinter(): PairedPrinter? {
            return Paper.book().read(PAIRED_PRINTER, null)
        }

        fun setPairedPrinter(printer: PairedPrinter) {
            Paper.book().write(PAIRED_PRINTER, printer)
        }

        fun removePairedPrinter() {
            Paper.book().delete(PAIRED_PRINTER)
        }
    }

}