package com.mazenrashed.printooth.utilities

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.mazenrashed.printooth.data.*
import android.R.array
import java.nio.ByteBuffer


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
            if (it.image != null){
                bluetooth.send(getBitmapBytes(it.image))
            }else{
                bluetooth.send(printer.justificationCommand.plus(it.alignment))
                bluetooth.send(printer.fontSizeCommand.plus(it.fontSize))
                bluetooth.send(printer.emphasizedModeCommand.plus(it.bold))
                bluetooth.send(printer.underlineModeCommand.plus(it.underlined))
                bluetooth.send(printer.characterCodeCommand.plus(it.characterCode))
                bluetooth.send(printer.lineSpacingCommand.plus(it.lineSpacing))
                bluetooth.send(StringUtils.getStringAsByteArray(it.text))
            }
            if (it.newLinesAfter > 0)
                bluetooth.send(printer.feedLineCommand.plus(it.newLinesAfter.toByte()))

        }
        //Feed 2 lines to cut the paper
        bluetooth.send(printer.feedLineCommand.plus(2))

        Handler(Looper.getMainLooper()).postDelayed({
            bluetooth.disconnect()
        }, 2000)
    }


    private fun getBitmapBytes(bitmap: Bitmap): ByteArray {
        val chunkNumbers = 10
        val bitmapSize = bitmap.rowBytes * bitmap.height
        val imageBytes = ByteArray(bitmapSize)
        val rows: Int
        val cols: Int
        val chunkHeight: Int
        val chunkWidth: Int
        cols = Math.sqrt(chunkNumbers.toDouble()).toInt()
        rows = cols
        chunkHeight = bitmap.height / rows
        chunkWidth = bitmap.width / cols

        var yCoord = 0
        var bitmapsSizes = 0

        for (x in 0 until rows) {
            var xCoord = 0
            for (y in 0 until cols) {
                var bitmapChunk: Bitmap? = Bitmap.createBitmap(bitmap, xCoord, yCoord, chunkWidth, chunkHeight)
                val bitmapArray = getBytesFromBitmapChunk(bitmapChunk!!)
                System.arraycopy(bitmapArray, 0, imageBytes, bitmapsSizes, bitmapArray.size)
                bitmapsSizes = bitmapsSizes + bitmapArray.size
                xCoord += chunkWidth

                bitmapChunk.recycle()
                bitmapChunk = null
            }
            yCoord += chunkHeight
        }

        return imageBytes
    }

    private fun getBytesFromBitmapChunk(bitmap: Bitmap): ByteArray {
        val bitmapSize = bitmap.rowBytes * bitmap.height
        val byteBuffer = ByteBuffer.allocate(bitmapSize)
        bitmap.copyPixelsToBuffer(byteBuffer)
        byteBuffer.rewind()
        return byteBuffer.array()
    }

    fun print(printables: ArrayList<Printable>) {
        this.printables = printables
        printingCallback?.connectingWithPrinter()
        bluetooth.connectToAddress(pairedPrinter.address)
    }
}