package com.mazenrashed.printooth.utilities

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.mazenrashed.printooth.data.BluetoothCallback
import com.mazenrashed.printooth.data.DeviceCallback
import com.mazenrashed.printooth.data.PairedPrinter
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printer.Printer

class Printing(private var printer: Printer, private var pairedPrinter: PairedPrinter, context: Context) {

    private var printableList = mutableListOf<Printable>()

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

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
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
                print()
                printingCallback?.printingOrderSentSuccessfully()
            }

            override fun onDeviceDisconnected(device: BluetoothDevice, message: String?) {
                printingCallback?.disconnected()
            }

            override fun onMessage(message: String) {
                printingCallback?.onMessage(message)
            }

            override fun onError(message: String?) {
                printingCallback?.onError(message)
            }

            override fun onConnectError(device: BluetoothDevice, message: String?) {
                printingCallback?.connectionFailed(message)
            }
        })
    }

    // Note: probably this needs to be revised and moved into Printer to support
    // customized flows
    private fun print() {
        sendCommand(printer.initPrinterCommand)

        this.printableList
            .map { p -> p.getPrintableByteArray(printer) }
            .flatten()
            .forEach { c -> sendCommand(c) }

        printer.feedLineCommand?.let { c ->
            for (i in 0 until extraLinesAtEnd) {
                sendCommand(c)
            }
        }

        sendCommand(printer.flushCommand)

        Handler(Looper.getMainLooper()).postDelayed({
            bluetooth.disconnect()
        }, 2000)
    }

    private fun sendCommand(command: ByteArray?) {
        if (command?.isNotEmpty() == true) {
            bluetooth.send(command)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun print(printableItems: ArrayList<Printable>) {
        this.printableList.clear()
        this.printableList.addAll(printableItems)

        printingCallback?.connectingWithPrinter()

        bluetooth.onStart()

        if (!bluetooth.isEnabled) bluetooth.enable() else bluetooth.connectToAddress(pairedPrinter.address)
    }
}