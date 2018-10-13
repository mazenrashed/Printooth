package com.mazenrashed.universalbluethootprinter.data

class Printable private constructor(val text: String, val fontSize: Byte, val alignment: Byte, val newLinesAfter: Int, val bold: Byte, val underlined: Byte, val characterCode: Byte) {

    class PrintableBuilder {
        private var text = ""
        private var fontSize = 0.1.toByte()
        private var alignment: Byte = DefaultPrinter.ALLIGMENT_LEFT
        private var newLinesAfter = 0
        private var bold: Byte = DefaultPrinter.EMPHASISED_MODE_NORMAL
        private var underlined: Byte = DefaultPrinter.UNDELINED_MODE_OFF
        private var characterCode: Byte = DefaultPrinter.CHARACTER_CODE_USA_CP437

        fun setText(text: String): PrintableBuilder {
            this.text = text
            return this
        }

        fun setFontSize(fontSize: Double): PrintableBuilder {
            this.fontSize = fontSize.toByte()
            return this
        }

        fun setAlignment(alignment: Byte): PrintableBuilder {
            this.alignment = alignment
            return this
        }

        fun setNewLinesAfter(lines: Int): PrintableBuilder {
            this.newLinesAfter = lines
            return this
        }

        fun setEmphasizedMode(mode: Byte): PrintableBuilder {
            this.bold = mode
            return this
        }

        fun setUnderlined(mode: Byte): PrintableBuilder {
            this.underlined = mode
            return this
        }

        fun setCharacterCode(characterCode: Byte): PrintableBuilder {
            this.characterCode = characterCode
            return this
        }

        fun build(): Printable {
            return Printable(text, fontSize, alignment, newLinesAfter, bold, underlined, characterCode)
        }
    }

}
