package com.mazenrashed.universalbluetoothprinter

import android.app.Application
import com.mazenrashed.universalbluethootprinter.Printooth

class ApplicationClass : Application(){

    override fun onCreate() {
        super.onCreate()
        Printooth.init(this)
    }
}