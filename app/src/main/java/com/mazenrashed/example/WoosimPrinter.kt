package com.mazenrashed.example

import com.mazenrashed.printooth.data.PrintingImagesHelper
import com.mazenrashed.printooth.data.converter.ArabicConverter
import com.mazenrashed.printooth.data.converter.Converter
import com.mazenrashed.printooth.data.printer.Printer

class WoosimPrinter : Printer() {
    override fun useConverter(): Converter = ArabicConverter()

    override fun initLineSpacingCommand(): ByteArray = byteArrayOf(0x1B, 0x33)

    override fun initInitPrinterCommand(): ByteArray = byteArrayOf(0x1b, 0x40)

    override fun initJustificationCommand(): ByteArray = byteArrayOf(27, 97)

    override fun initFontSizeCommand(): ByteArray = byteArrayOf(29, 33)

    override fun initEmphasizedModeCommand(): ByteArray = byteArrayOf(27, 69) //1 on , 0 off

    override fun initUnderlineModeCommand(): ByteArray = byteArrayOf(27, 45) //1 on , 0 off

    override fun initCharacterCodeCommand(): ByteArray = byteArrayOf(27, 116)

    override fun initFeedLineCommand(): ByteArray = byteArrayOf(27, 100)

    override fun initPrintingImagesHelper(): PrintingImagesHelper = WoosimPrintingImagesHelper()

}