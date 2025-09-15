package com.mazenrashed.printooth.data.printer

import com.mazenrashed.printooth.data.PrintingImagesHelper
import com.mazenrashed.printooth.data.converter.Converter

abstract class Printer {

    // Each printer implementation must map selected option
    companion object {
        val ALIGNMENT_LEFT: Byte = 0
        val ALIGNMENT_CENTER: Byte = 1
        val ALIGNMENT_RIGHT: Byte = 2

        val EMPHASIZED_MODE_NORMAL: Byte = 0
        val EMPHASIZED_MODE_BOLD: Byte = 1

        val UNDERLINED_MODE_ON: Byte = 1
        val UNDERLINED_MODE_OFF: Byte = 0

        val LINE_SPACING_00: Byte = 0
        val LINE_SPACING_30: Byte = 30
        val LINE_SPACING_60: Byte = 60

        val FONT_SIZE_NORMAL: Byte = 0x00
        val FONT_SIZE_LARGE: Byte = 0x10

        val CHARCODE_PC437: Byte = 0x00         // USA / Standard Europe
        val CHARCODE_JIS: Byte = 0x01           // Japanese Katakana
        val CHARCODE_PC850: Byte = 0x02         // Multilingual
        val CHARCODE_PC860: Byte = 0x03         // Portuguese
        val CHARCODE_PC863: Byte = 0x04         // Canadian-French
        val CHARCODE_PC865: Byte = 0x05         // Nordic
        val CHARCODE_WEU: Byte = 0x06           // Simplified Kanji, Hirakana
        val CHARCODE_GREEK: Byte = 0x07         // Simplified Kanji
        val CHARCODE_HEBREW: Byte = 0x08        // Simplified Kanji
        val CHARCODE_ARABIC_CP864: Byte = 0x0E  // Arabic
        val CHARCODE_PC1252: Byte = 0x10        // Western European Windows Code Set
        val CHARCODE_PC866: Byte = 0x12         // Cirillic //2
        val CHARCODE_PC852: Byte = 0x13         // Latin 2
        val CHARCODE_PC858: Byte = 0x14         // Euro
        val CHARCODE_THAI42: Byte = 0x15        // Thai character code 42
        val CHARCODE_THAI11: Byte = 0x16        // Thai character code 11
        val CHARCODE_THAI13: Byte = 0x17        // Thai character code 13
        val CHARCODE_THAI14: Byte = 0x18        // Thai character code 14
        val CHARCODE_THAI16: Byte = 0x19        // Thai character code 16
        val CHARCODE_THAI17: Byte = 0x1a        // Thai character code 17
        val CHARCODE_THAI18: Byte = 0x1b        // Thai character code 18
        val CHARCODE_ARABIC_CP720: Byte = 40
        val CHARCODE_ARABIC_WIN_1256: Byte = 41
        val CHARCODE_ARABIC_FARISI: Byte = 42
    }

    abstract fun getInitPrinterCommand(): ByteArray?
    abstract fun getJustificationCommand(alignment: Byte): ByteArray?
    abstract fun getFontSizeCommand(size: Byte): ByteArray?
    abstract fun getEmphasizedModeCommand(emphasized: Byte): ByteArray?
    abstract fun getUnderlineModeCommand(mode: Byte): ByteArray?
    abstract fun getCharacterCodeCommand(code: Byte): ByteArray?
    abstract fun getFeedLinesCommand(lines: Byte): ByteArray?
    abstract fun getLineSpacingCommand(spacing: Byte): ByteArray?
    abstract fun getFlushCommand(): ByteArray?
    abstract fun getPrintingImagesHelper(): PrintingImagesHelper
    abstract fun getConverter(): Converter
}