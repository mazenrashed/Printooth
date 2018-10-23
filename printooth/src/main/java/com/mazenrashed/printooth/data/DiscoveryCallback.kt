package com.mazenrashed.printooth.data

import android.bluetooth.BluetoothDevice

interface DiscoveryCallback {
    fun onDiscoveryStarted()
    fun onDiscoveryFinished()
    fun onDeviceFound(device: BluetoothDevice)
    fun onDevicePaired(device: BluetoothDevice)
    fun onDeviceUnpaired(device: BluetoothDevice)
    fun onError(message: String)
}