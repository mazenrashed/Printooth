package com.mazenrashed.printooth.utilities

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.mazenrashed.printooth.data.*


class Printing(private var printer: Printer,private var pairedPrinter: PairedPrinter, context: Context) {
    private lateinit var printables: ArrayList<Printable>
    private var bluetooth = Bluetooth(context)
    var printingCallback: PrintingCallback? = null

    init {
        bluetooth.onStart()
        if (!bluetooth.isEnabled) bluetooth.enable()
        initDeviceCallback()
    }

    private fun initDeviceCallback() {
        bluetooth.setDeviceCallback(object : DeviceCallback {
            override fun onDeviceConnected(device: BluetoothDevice) {
                printPrintables()
                printingCallback?.printingOrderSentSuccessfully()
            }

            override fun onDeviceDisconnected(device: BluetoothDevice, message: String) {}

            override fun onMessage(message: String) {
                printingCallback?.onMessage(message)
            }

            override fun onError(message: String) {
                printingCallback?.onError(message)
            }

            override fun onConnectError(device: BluetoothDevice, message: String) {
                printingCallback?.connectionFailed(message)
            }
        })
    }

    private fun printPrintables() {
        //init printer
        bluetooth.send(printer.initPrinterCommand)
        this.printables.forEach {
            bluetooth.send(printer.justificationCommand.plus(it.alignment))
            bluetooth.send(printer.fontSizeCommand.plus(it.fontSize))
            bluetooth.send(printer.emphasizedModeCommand.plus(it.bold))
            bluetooth.send(printer.underlineModeCommand.plus(it.underlined))
            bluetooth.send(printer.characterCodeCommand.plus(it.characterCode))
            bluetooth.send(printer.lineSpacingCommand.plus(it.lineSpacing))
            bluetooth.send(StringUtils.getStringAsByteArray(it.text))
            if (it.newLinesAfter > 0)
                bluetooth.send(printer.feedLineCommand.plus(it.newLinesAfter.toByte()))
        }
        //Feed 2 lines to cut the paper
        bluetooth.send(printer.feedLineCommand.plus(2))

        Handler(Looper.getMainLooper()).postDelayed({
            bluetooth.disconnect()
        }, 2000)
    }

    fun print(printables: ArrayList<Printable>) {
        this.printables = printables
        printingCallback?.connectingWithPrinter()
        bluetooth.connectToAddress(pairedPrinter.address)
    }

}