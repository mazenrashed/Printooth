package com.mazenrashed.printooth.data.printable

import com.mazenrashed.printooth.data.converter.Converter
import com.mazenrashed.printooth.data.printer.Printer

data class TextPrintable private constructor(val text: String,
                                             val fontSize: Byte,
                                             val alignment: Byte,
                                             val newLinesAfter: Int,
                                             val bold: Byte,
                                             val underlined: Byte,
                                             val characterCode: Byte,
                                             val lineSpacing: Byte,
                                             val customConverter: Converter?) : Printable {

    override fun getPrintableByteArray(printer: Printer): List<ByteArray> {
        val operations = mutableListOf<ByteArray>()

        printer.getJustificationCommand(alignment)?.let {
            operations.add(it)
        }

        printer.getFontSizeCommand(fontSize)?.let {
            operations.add(it)
        }

        printer.getEmphasizedModeCommand(bold)?.let {
            operations.add(it)
        }

        printer.getUnderlineModeCommand(underlined)?.let {
            operations.add(it)
        }

        printer.getCharacterCodeCommand(characterCode)?.let {
            operations.add(it)
        }

        printer.getLineSpacingCommand(lineSpacing)?.let {
            operations.add(it)
        }

        customConverter?.let {
            operations.add(it.toByteArray(text))
        } ?: run {
            operations.add(printer.getConverter().toByteArray(text))
        }

        if (newLinesAfter > 0) {
            printer.getFeedLinesCommand(newLinesAfter.toByte())?.let {
                operations.add(it)
            }
        }

        return operations
    }

    class Builder {
        private var text = ""
        private var fontSize = Printer.FONT_SIZE_NORMAL
        private var alignment: Byte = Printer.ALIGNMENT_LEFT
        private var newLinesAfter = 0
        private var bold: Byte = Printer.EMPHASIZED_MODE_NORMAL
        private var underlined: Byte = Printer.UNDERLINED_MODE_OFF
        private var characterCode: Byte = Printer.CHARCODE_PC437
        private var lineSpacing: Byte = Printer.LINE_SPACING_30
        private var customConverter: Converter? = null

        fun setText(text: String): Builder {
            this.text = text
            return this
        }

        fun setFontSize(fontSize: Byte): Builder {
            this.fontSize = fontSize
            return this
        }

        fun setAlignment(alignment: Byte): Builder {
            this.alignment = alignment
            return this
        }

        fun setNewLinesAfter(lines: Int): Builder {
            this.newLinesAfter = lines
            return this
        }

        fun setEmphasizedMode(mode: Byte): Builder {
            this.bold = mode
            return this
        }

        fun setUnderlined(mode: Byte): Builder {
            this.underlined = mode
            return this
        }

        fun setCharacterCode(characterCode: Byte): Builder {
            this.characterCode = characterCode
            return this
        }

        fun setLineSpacing(lineSpacing: Byte): Builder {
            this.lineSpacing = lineSpacing
            return this
        }

        fun setCustomConverter(converter: Converter): Builder {
            this.customConverter = converter
            return this
        }

        fun build(): Printable {
            return TextPrintable(text, fontSize, alignment, newLinesAfter, bold, underlined, characterCode, lineSpacing, customConverter)
        }
    }
}
