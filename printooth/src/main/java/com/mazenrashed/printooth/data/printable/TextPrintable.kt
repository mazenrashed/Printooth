package com.mazenrashed.printooth.data.printable

import com.mazenrashed.printooth.data.converter.Converter
import com.mazenrashed.printooth.data.printer.DefaultPrinter
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

        printer.justificationCommand?.let {
            operations.add(it.plus(alignment))
        }

        printer.justificationCommand?.let {
            operations.add(it.plus(alignment))
        }

        printer.fontSizeCommand?.let {
            operations.add(it.plus(fontSize))
        }

        printer.emphasizedModeCommand?.let {
            operations.add(it.plus(bold))
        }

        printer.underlineModeCommand?.let {
            operations.add(it.plus(underlined))
        }

        printer.characterCodeCommand?.let {
            operations.add(it.plus(characterCode))
        }

        printer.lineSpacingCommand?.let {
            operations.add(it.plus(lineSpacing))
        }

        customConverter?.let {
            operations.add(it.toByteArray(text))
        } ?: run {
            operations.add(printer.converter.toByteArray(text))
        }

        printer.feedLineCommand?.let { c ->
            for (i in 0 until newLinesAfter) {
                operations.add(c)
            }
        }

        return operations
    }

    class Builder {
        private var text = ""
        private var fontSize = DefaultPrinter.FONT_SIZE_NORMAL
        private var alignment: Byte = DefaultPrinter.ALIGNMENT_LEFT
        private var newLinesAfter = 0
        private var bold: Byte = DefaultPrinter.EMPHASIZED_MODE_NORMAL
        private var underlined: Byte = DefaultPrinter.UNDERLINED_MODE_OFF
        private var characterCode: Byte = DefaultPrinter.CHARCODE_PC437
        private var lineSpacing: Byte = DefaultPrinter.LINE_SPACING_30
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
