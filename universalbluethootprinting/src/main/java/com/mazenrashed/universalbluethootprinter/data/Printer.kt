package com.mazenrashed.universalbluethootprinter.data

abstract class Printer {
    var initPrinterCommand = initInitPrinterCommand()
    var justificationCommand = initJustificationCommand()
    var fontSizeCommand = initFontSizeCommand()
    var emphasizedModeCommand = initEmphasizedModeCommand()
    var underlineModeCommand = initUnderlineModeCommand()
    var characterCodeCommand = initCharacterCodeCommand()
    var feedLineCommand = initFeedLineCommand()
    var lineSpacingCommand = initLineSpacingCommand()

    abstract fun initInitPrinterCommand(): ByteArray
    abstract fun initJustificationCommand(): ByteArray
    abstract fun initFontSizeCommand(): ByteArray
    abstract fun initEmphasizedModeCommand(): ByteArray
    abstract fun initUnderlineModeCommand(): ByteArray
    abstract fun initCharacterCodeCommand(): ByteArray
    abstract fun initFeedLineCommand(): ByteArray
    abstract fun initLineSpacingCommand(): ByteArray

}