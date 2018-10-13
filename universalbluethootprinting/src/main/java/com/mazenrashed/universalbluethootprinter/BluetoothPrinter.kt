package com.mazenrashed.universalbluethootprinter

import android.content.Context
import com.mazenrashed.universalbluethootprinter.data.DefaultPrinter
import com.mazenrashed.universalbluethootprinter.data.PairedPrinter
import com.mazenrashed.universalbluethootprinter.data.Printer
import com.mazenrashed.universalbluethootprinter.utilities.Printing

object BluetoothPrinter {

    fun printer(printer: Printer, pairedPrinter: PairedPrinter, context: Context): Printing = Printing(printer, pairedPrinter, context)

    fun printer(pairedPrinter: PairedPrinter, context: Context) = Printing(DefaultPrinter(), pairedPrinter, context)

    fun printer(context: Context) = Printing(DefaultPrinter(), getPairedPrinter()!!, context)

    fun setPrinter(name: String?, address: String) = PairedPrinter.setPairedPrinter(PairedPrinter(name, address))

    fun getPairedPrinter(): PairedPrinter? = PairedPrinter.getPairedPrinter()

    fun hasPairedPrinter(): Boolean = PairedPrinter.getPairedPrinter() != null

    fun removeCurrentPrinter() = PairedPrinter.removePairedPrinter()

}