package com.mazenrashed.universalbluethootprinter.common

import android.app.Application
import io.paperdb.Paper

class UniversalBluetoothApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
    }
}