package com.mazenrashed.printooth.ui

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.assent.Permission
import com.afollestad.assent.runWithPermissions
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.R
import com.mazenrashed.printooth.data.DiscoveryCallback
import com.mazenrashed.printooth.utilities.Bluetooth
import kotlinx.android.synthetic.main.activity_scanning.*

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
                toolbar.title = "Scanning.."
                devices.clear()
                devices.addAll(bluetooth.pairedDevices)
                adapter.notifyDataSetChanged()
            }

            override fun onDiscoveryFinished() {
                toolbar.title = if (devices.isNotEmpty()) "Select a Printer" else "No devices"
                refreshLayout.isRefreshing = false
            }

            override fun onDeviceFound(device: BluetoothDevice) {
                if (!devices.contains(device)) {
                    devices.add(device)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onDevicePaired(device: BluetoothDevice) {
                Printooth.setPrinter(device.name, device.address)
                Toast.makeText(this@ScanningActivity, "Device Paired", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
                setResult(Activity.RESULT_OK)
                this@ScanningActivity.finish()
            }

            override fun onDeviceUnpaired(device: BluetoothDevice) {
                Toast.makeText(this@ScanningActivity, "Device unpaired", Toast.LENGTH_SHORT).show()
                val pairedPrinter = Printooth.getPairedPrinter()
                if (pairedPrinter != null && pairedPrinter.address == device.address)
                    Printooth.removeCurrentPrinter()
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
            val device = devices[i]
            if (device.bondState == BluetoothDevice.BOND_BONDED) {
                Printooth.setPrinter(device.name, device.address)
                setResult(Activity.RESULT_OK)
                this@ScanningActivity.finish()
            } else if (device.bondState == BluetoothDevice.BOND_NONE)
                bluetooth.pair(devices[i])
            adapter.notifyDataSetChanged()
        }
    }

    private fun initViews() {
        printers.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        runWithPermissions(Permission.ACCESS_FINE_LOCATION) {
            bluetooth.onStart()
            if (!bluetooth.isEnabled)
                bluetooth.enable()
            Handler().postDelayed({
                bluetooth.startScanning()
            }, 1000)
        }

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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return LayoutInflater.from(context)
                    .inflate(R.layout.bluetooth_device_row, parent, false).apply {
                        findViewById<TextView>(R.id.name).text = if (devices[position].name.isNullOrEmpty()) devices[position].address else devices[position].name
                        findViewById<TextView>(R.id.pairStatus).visibility = if (devices[position].bondState != BluetoothDevice.BOND_NONE) View.VISIBLE else View.INVISIBLE
                        findViewById<TextView>(R.id.pairStatus).text = when (devices[position].bondState) {
                            BluetoothDevice.BOND_BONDED -> "Paired"
                            BluetoothDevice.BOND_BONDING -> "Pairing.."
                            else -> ""
                        }
                        findViewById<ImageView>(R.id.pairedPrinter).visibility = if (Printooth.getPairedPrinter()?.address == devices[position].address) View.VISIBLE else View.GONE
                    }
        }
    }
}
