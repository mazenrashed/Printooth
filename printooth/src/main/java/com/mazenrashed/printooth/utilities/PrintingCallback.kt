package com.mazenrashed.printooth.utilities

interface PrintingCallback {
    fun connectingWithPrinter()
    fun printingOrderSentSuccessfully()
    fun connectionFailed(error: String)
    fun onError(error: String)
    fun onMessage(message: String)
    fun disconnected()
}