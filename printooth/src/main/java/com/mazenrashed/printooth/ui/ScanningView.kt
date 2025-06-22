package com.mazenrashed.printooth.ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.R
import com.mazenrashed.printooth.data.DiscoveryCallback
import com.mazenrashed.printooth.databinding.ViewScanningBinding
import com.mazenrashed.printooth.utilities.Bluetooth

@SuppressLint("MissingPermission", "NotifyDataSetChanged")
class ScanningView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    private val binding: ViewScanningBinding = ViewScanningBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var bluetooth: Bluetooth

    private lateinit var adapter: DevicesAdapter

    private var invokeDeviceBonded: (() -> Unit)? = null

    private val printerClickListener = OnClickListener {
        val device = adapter.getItem(it.tag as Int)

        if (device.bondState == BluetoothDevice.BOND_BONDED) {
            Printooth.setPrinter(device.name, device.address)

            this.invokeDeviceBonded?.invoke()
        } else if (device.bondState == BluetoothDevice.BOND_NONE) {
            bluetooth.pair(device)
        }

        adapter.notifyDataSetChanged()
    }

    private val bluetoothDiscoveryCallback = object : DiscoveryCallback {
        override fun onDiscoveryStarted() {
            binding.refreshLayout.isRefreshing = true
            binding.toolbar.text = context.getString(R.string.printooth_scanning)

            adapter.setItems(bluetooth.pairedDevices)
            adapter.notifyDataSetChanged()
        }

        override fun onDiscoveryFinished() {
            binding.toolbar.text = context.getString(if (adapter.isNotEmpty()) R.string.printooth_select_printer else R.string.printooth_no_devices)
            binding.refreshLayout.isRefreshing = false
        }

        override fun onDeviceFound(device: BluetoothDevice) {
            if (adapter.addItem(device)) {
                adapter.notifyDataSetChanged()
            }
        }

        override fun onDevicePaired(device: BluetoothDevice) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                Printooth.setPrinter(device.name, device.address)

                Toast.makeText(context, R.string.printooth_device_paired, Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()

                invokeDeviceBonded?.invoke()
            }
        }

        override fun onDeviceUnpaired(device: BluetoothDevice) {
            Toast.makeText(context, R.string.printooth_device_unpaired, Toast.LENGTH_SHORT).show()
            val pairedPrinter = Printooth.getPairedPrinter()

            if (pairedPrinter != null && pairedPrinter.address == device.address) {
                Printooth.removeCurrentPrinter()
            }

            if (adapter.removeItem(device)) {
                adapter.notifyDataSetChanged()
            }

            bluetooth.startScanning()
        }

        override fun onError(message: String?) {
            Toast.makeText(context, R.string.printooth_error_while_pairing, Toast.LENGTH_SHORT).show()
            adapter.notifyDataSetChanged()
        }
    }

    init {
        adapter = DevicesAdapter(printerClickListener)
        binding.printers.adapter = adapter

        bluetooth = Bluetooth(context)
        bluetooth.setDiscoveryCallback(bluetoothDiscoveryCallback)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        bluetooth.onStart()

        if (!bluetooth.isEnabled) {
            bluetooth.enable()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            bluetooth.startScanning()
        }, 1000)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        bluetooth.onStop()
    }

    fun establishDeviceBondedCallback(callback: (() -> Unit)?) {
        this.invokeDeviceBonded = callback
    }
}