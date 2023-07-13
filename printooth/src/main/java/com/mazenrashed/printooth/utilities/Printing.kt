package com.mazenrashed.printooth.utilities

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.mazenrashed.printooth.data.BluetoothCallback
import com.mazenrashed.printooth.data.DeviceCallback
import com.mazenrashed.printooth.data.PairedPrinter
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printer.Printer
import java.io.IOException

class Printing(private var printer: Printer, private var pairedPrinter: PairedPrinter, val context: Context) {
    private lateinit var printables: List<Printable>
    private var bluetooth = Bluetooth(context)
    var printingCallback: PrintingCallback? = null
    var extraLinesAtEnd: Byte = 0

    init {
        initBluetoothCallback()
        initDeviceCallback()
    }

    private fun initBluetoothCallback() {
        bluetooth.setBluetoothCallback(object : BluetoothCallback {
            override fun onBluetoothTurningOn() {}

            override fun onBluetoothOn() {
                bluetooth.connectToAddress(pairedPrinter.address)
            }

            override fun onBluetoothTurningOff() {}

            override fun onBluetoothOff() {}

            override fun onUserDeniedActivation() {}
        })
    }

    private fun initDeviceCallback() {
        bluetooth.setDeviceCallback(object : DeviceCallback {
            override fun onDeviceConnected(device: BluetoothDevice) {
                printPrintables()
            }

            override fun onDeviceDisconnected(device: BluetoothDevice, message: String) {
                printingCallback?.disconnected()
            }

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
        try {
            bluetooth.send(printer.initPrinterCommand) // init printer
            this.printables.forEach {
                it.getPrintableByteArray(printer).forEach { ops ->
                    bluetooth.send(ops)
                }
            }

            //Feed 2 lines to cut the paper
            if (extraLinesAtEnd > 0) {
                bluetooth.send(printer.feedLineCommand.plus(extraLinesAtEnd))
            }

            Handler(Looper.getMainLooper()).postDelayed({
                bluetooth.disconnect()
            }, 2000)

            printingCallback?.printingOrderSentSuccessfully()
        } catch (e: IOException) {
            printingCallback?.onError("Couldn't send data to the other device. Error: " + e.message)
        }


    }

    fun print(printables: ArrayList<Printable>) {
        this.printables = printables
        printingCallback?.connectingWithPrinter()
        bluetooth.onStart()
        if (!bluetooth.isEnabled) bluetooth.enable() else bluetooth.connectToAddress(pairedPrinter.address)
    }
}