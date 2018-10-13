package com.mazenrashed.universalbluethootprinter.data

open class DefaultPrinter : Printer() {

    override fun initInitPrinterCommand(): ByteArray = byteArrayOf(0x1b, 0x40)

    override fun initJustificationCommand(): ByteArray = byteArrayOf(27, 97)

    override fun initFontSizeCommand(): ByteArray = byteArrayOf(29, 33)

    override fun initEmphasizedModeCommand(): ByteArray = byteArrayOf(27, 69) //1 on , 0 off

    override fun initUnderlineModeCommand(): ByteArray = byteArrayOf(27, 45) //1 on , 0 off

    override fun initCharacterCodeCommand(): ByteArray = byteArrayOf(27, 116)

    override fun initFeedLineCommand(): ByteArray = byteArrayOf(27, 100)

    companion object {
        val ALLIGMENT_REGHT: Byte = 2
        val ALLIGMENT_LEFT: Byte = 0
        val ALLIGMENT_CENTER: Byte = 1
        val EMPHASISED_MODE_BOLD: Byte = 1
        val EMPHASISED_MODE_NORMAL: Byte = 0
        val UNDELINED_MODE_ON: Byte = 1
        val UNDELINED_MODE_OFF: Byte = 0
        val CHARACTER_CODE_USA_CP437: Byte = 0
        val CHARACTER_CODE_ARABIC_CP720: Byte = 40
        val CHARACTER_CODE_ARABIC_WIN_1256: Byte = 41
        val CHARACTER_CODE_ARABIC_FARISI: Byte = 42

    }
}