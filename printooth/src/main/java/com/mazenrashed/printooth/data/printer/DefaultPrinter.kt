package com.mazenrashed.printooth.data.printer

import com.mazenrashed.printooth.data.DefaultPrintingImagesHelper
import com.mazenrashed.printooth.data.PrintingImagesHelper
import com.mazenrashed.printooth.data.converter.Converter
import com.mazenrashed.printooth.data.converter.DefaultConverter

open class DefaultPrinter : Printer() {
    override fun getLineSpacingCommand(spacing: Byte): ByteArray? = byteArrayOf(0x1B, 0x33, spacing)

    override fun getInitPrinterCommand(): ByteArray? = byteArrayOf(0x1b, 0x40)

    override fun getJustificationCommand(alignment: Byte): ByteArray? = byteArrayOf(27, 97, alignment)

    override fun getFontSizeCommand(size: Byte): ByteArray? = byteArrayOf(29, 33, size)

    override fun getEmphasizedModeCommand(emphasized: Byte): ByteArray? = byteArrayOf(27, 69, emphasized)

    override fun getUnderlineModeCommand(mode: Byte): ByteArray? = byteArrayOf(27, 45, mode)

    override fun getCharacterCodeCommand(code: Byte): ByteArray? = byteArrayOf(27, 116, code)

    override fun getFlushCommand(): ByteArray? = null

    override fun getFeedLinesCommand(lines: Byte): ByteArray? = byteArrayOf(27, 100, lines)

    override fun getPrintingImagesHelper(): PrintingImagesHelper = DefaultPrintingImagesHelper()

    override fun getConverter(): Converter = DefaultConverter()
}