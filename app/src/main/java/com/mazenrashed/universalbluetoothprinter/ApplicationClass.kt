package com.mazenrashed.universalbluetoothprinter

import android.app.Application
import com.mazenrashed.universalbluethootprinter.BluetoothPrinter

class ApplicationClass : Application(){

    override fun onCreate() {
        super.onCreate()
        BluetoothPrinter.init(this)
    }
}