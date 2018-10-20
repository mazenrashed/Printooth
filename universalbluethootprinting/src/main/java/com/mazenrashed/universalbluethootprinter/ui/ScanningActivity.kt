package com.mazenrashed.universalbluethootprinter.ui

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_scanning.*
import com.mazenrashed.universalbluethootprinter.data.DiscoveryCallback
import com.mazenrashed.universalbluethootprinter.utilities.Bluetooth
import com.mazenrashed.universalbluethootprinter.BluetoothPrinter
import com.mazenrashed.universalbluethootprinter.R

class ScanningActivity : AppCompatActivity() {
    private lateinit var bluetooth: Bluetooth
    private var devices = ArrayList<BluetoothDevice>()
    private lateinit var adapter: BluetoothDevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        adapter = BluetoothDevicesAdapter(this)
        bluetooth = Bluetooth(this)
        setup()
    }

    private fun setup() {
        initViews()
        initListeners()
        initDeviceCallback()
    }

    private fun initDeviceCallback() {
        bluetooth.setDiscoveryCallback(object : DiscoveryCallback {
            override fun onDiscoveryStarted() {
                refreshLayout.isRefreshing = true
                (toolbar as Toolbar).title = "Scanning.."
                devices.clear()
                devices.addAll(bluetooth.pairedDevices)
                adapter.notifyDataSetChanged()
            }

            override fun onDiscoveryFinished() {
                (toolbar as Toolbar).title = if (devices.isNotEmpty()) "Select Printing" else "No devices"
                refreshLayout.isRefreshing = false
            }

            override fun onDeviceFound(device: BluetoothDevice) {
                if (!devices.contains(device)) {
                    devices.add(device)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onDevicePaired(device: BluetoothDevice) {
                BluetoothPrinter.setPrinter(device.name, device.address)
                Toast.makeText(this@ScanningActivity, "Device Paired", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
                setResult(Activity.RESULT_OK)
                this@ScanningActivity.finish()
            }

            override fun onDeviceUnpaired(device: BluetoothDevice) {
                Toast.makeText(this@ScanningActivity, "Device unpaired", Toast.LENGTH_SHORT).show()
                var pairedPrinter = BluetoothPrinter.getPairedPrinter()
                if (pairedPrinter != null && pairedPrinter.address == device.address)
                    BluetoothPrinter.removeCurrentPrinter()
                devices.remove(device)
                adapter.notifyDataSetChanged()
                bluetooth.startScanning()
            }

            override fun onError(message: String) {
                Toast.makeText(this@ScanningActivity, "Error while pairing", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initListeners() {
        refreshLayout.setOnRefreshListener { bluetooth.startScanning() }
        printers.setOnItemClickListener { _, _, i, _ ->
            var device = devices[i]
            if (device.bondState == BluetoothDevice.BOND_BONDED) {
                BluetoothPrinter.setPrinter(device.name, device.address)
                setResult(Activity.RESULT_OK)
                this@ScanningActivity.finish()
            }
            else if (device.bondState == BluetoothDevice.BOND_NONE)
                bluetooth.pair(devices[i])
            adapter.notifyDataSetChanged()
        }
    }

    private fun initViews() {
        printers.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        bluetooth.onStart()
        if (!bluetooth.isEnabled)
            bluetooth.enable()
        Handler().postDelayed({
            bluetooth.startScanning()
        }, 1000)
    }

    override fun onStop() {
        super.onStop()
        bluetooth.onStop()
    }

    companion object {
        const val SCANNING_FOR_PRINTER = 115
    }

    inner class BluetoothDevicesAdapter(context: Context) : ArrayAdapter<BluetoothDevice>(context, android.R.layout.simple_list_item_1) {
        override fun getCount(): Int {
            return devices.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return LayoutInflater.from(context)
                    .inflate(R.layout.bluetooth_device_row, parent, false).apply {
                        findViewById<TextView>(R.id.name).text = if (devices[position].name.isNullOrEmpty()) devices[position].address else devices[position].name
                        findViewById<TextView>(R.id.pairStatus).visibility = if (devices[position].bondState != BluetoothDevice.BOND_NONE) View.VISIBLE else View.INVISIBLE
                        findViewById<TextView>(R.id.pairStatus).text = when (devices[position].bondState) {
                            BluetoothDevice.BOND_BONDED -> "Paired"
                            BluetoothDevice.BOND_BONDING -> "Pairing.."
                            else -> ""
                        }
                        findViewById<ImageView>(R.id.pairedPrinter).visibility = if (BluetoothPrinter.getPairedPrinter()?.address == devices[position].address) View.VISIBLE else View.GONE
                    }
        }
    }
}
