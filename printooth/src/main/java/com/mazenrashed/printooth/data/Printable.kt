package com.mazenrashed.printooth.data

data class Printable private constructor(val text: String,
                                         val fontSize: Byte,
                                         val alignment: Byte,
                                         val newLinesAfter: Int,
                                         val bold: Byte,
                                         val underlined: Byte,
                                         val characterCode: Byte,
                                         val lineSpacing: Byte) {

    class PrintableBuilder {
        private var text = ""
        private var fontSize = DefaultPrinter.FONT_SIZE_NORMAL
        private var alignment: Byte = DefaultPrinter.ALLIGMENT_LEFT
        private var newLinesAfter = 0
        private var bold: Byte = DefaultPrinter.EMPHASISED_MODE_NORMAL
        private var underlined: Byte = DefaultPrinter.UNDELINED_MODE_OFF
        private var characterCode: Byte = DefaultPrinter.CHARACTER_CODE_USA_CP437
        private var lineSpacing: Byte = DefaultPrinter.LINE_SPACING_30

        fun setText(text: String): PrintableBuilder {
            this.text = text
            return this
        }

        fun setFontSize(fontSize: Byte): PrintableBuilder {
            this.fontSize = fontSize
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

        fun setLineSpacing(lineSpacing: Byte): PrintableBuilder {
            this.lineSpacing = lineSpacing
            return this
        }

        fun build(): Printable {
            return Printable(text, fontSize, alignment, newLinesAfter, bold, underlined, characterCode, lineSpacing)
        }
    }

}
