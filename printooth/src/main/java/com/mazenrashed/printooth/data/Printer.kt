package com.mazenrashed.printooth.data

abstract class Printer {
    var initPrinterCommand = initInitPrinterCommand()
    var justificationCommand = initJustificationCommand()
    var fontSizeCommand = initFontSizeCommand()
    var emphasizedModeCommand = initEmphasizedModeCommand()
    var underlineModeCommand = initUnderlineModeCommand()
    var characterCodeCommand = initCharacterCodeCommand()
    var feedLineCommand = initFeedLineCommand()
    var lineSpacingCommand = initLineSpacingCommand()
    var printingImagesHelper:PrintingImagesHelper = initPrintingImagesHelper()

    abstract fun initInitPrinterCommand(): ByteArray
    abstract fun initJustificationCommand(): ByteArray
    abstract fun initFontSizeCommand(): ByteArray
    abstract fun initEmphasizedModeCommand(): ByteArray
    abstract fun initUnderlineModeCommand(): ByteArray
    abstract fun initCharacterCodeCommand(): ByteArray
    abstract fun initFeedLineCommand(): ByteArray
    abstract fun initLineSpacingCommand(): ByteArray
    abstract fun initPrintingImagesHelper(): PrintingImagesHelper

}