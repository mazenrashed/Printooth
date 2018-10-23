package com.mazenrashed.universalbluethootprinter

import android.content.Context
import com.mazenrashed.universalbluethootprinter.data.DefaultPrinter
import com.mazenrashed.universalbluethootprinter.data.PairedPrinter
import com.mazenrashed.universalbluethootprinter.data.Printer
import com.mazenrashed.universalbluethootprinter.utilities.Printing
import io.paperdb.Paper

object Printooth {

    fun init(context: Context) = Paper.init(context)

    fun printer(printer: Printer, pairedPrinter: PairedPrinter, context: Context): Printing = Printing(printer, pairedPrinter, context)

    fun printer(printer: Printer, context: Context): Printing = Printing(printer, getPairedPrinter()?: error("No paired printer saved, Save one and retry!!"), context)

    fun printer(pairedPrinter: PairedPrinter, context: Context) = Printing(DefaultPrinter(), pairedPrinter, context)

    fun printer(context: Context) = Printing(DefaultPrinter(), getPairedPrinter()?: error("No paired printer saved, Save one and retry!!"), context)

    fun setPrinter(name: String?, address: String) = PairedPrinter.setPairedPrinter(PairedPrinter(name, address))

    fun getPairedPrinter(): PairedPrinter? = PairedPrinter.getPairedPrinter()

    fun hasPairedPrinter(): Boolean = PairedPrinter.getPairedPrinter() != null

    fun removeCurrentPrinter() = PairedPrinter.removePairedPrinter()

}