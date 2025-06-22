package com.mazenrashed.printooth.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.DiscoveryCallback
import com.mazenrashed.printooth.utilities.Bluetooth
import com.mazenrashed.printooth.databinding.ActivityScanningBinding
import com.mazenrashed.printooth.databinding.BluetoothDeviceRowBinding

class ScanningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanningBinding

    private lateinit var bluetooth: Bluetooth
    private var devices = ArrayList<BluetoothDevice>()
    private lateinit var adapter: BluetoothDevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BluetoothDevicesAdapter(printerClickListener)
        bluetooth = Bluetooth(this)
        setup()
    }

    // TODO
    @SuppressLint("MissingPermission")
    private fun setup() {
        initViews()
        initListeners()
        initDeviceCallback()
    }

    private fun initDeviceCallback() {
        bluetooth.setDiscoveryCallback(object : DiscoveryCallback {

            @SuppressLint("NotifyDataSetChanged")
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onDiscoveryStarted() {
                binding.refreshLayout.isRefreshing = true
                binding.toolbar.title = "Scanning.."
                devices.clear()
                devices.addAll(bluetooth.pairedDevices)
                adapter.notifyDataSetChanged()
            }

            override fun onDiscoveryFinished() {
                binding.toolbar.title = if (devices.isNotEmpty()) "Select a Printer" else "No devices"
                binding.refreshLayout.isRefreshing = false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDeviceFound(device: BluetoothDevice) {
                if (!devices.contains(device)) {
                    devices.add(device)
                    adapter.notifyDataSetChanged()
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDevicePaired(device: BluetoothDevice) {
                if (ContextCompat.checkSelfPermission(this@ScanningActivity, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                    Printooth.setPrinter(device.name, device.address)
                    Toast.makeText(this@ScanningActivity, "Device Paired", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                    setResult(Activity.RESULT_OK)
                    this@ScanningActivity.finish()
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
            override fun onDeviceUnpaired(device: BluetoothDevice) {
                Toast.makeText(this@ScanningActivity, "Device unpaired", Toast.LENGTH_SHORT).show()
                val pairedPrinter = Printooth.getPairedPrinter()
                if (pairedPrinter != null && pairedPrinter.address == device.address)
                    Printooth.removeCurrentPrinter()
                devices.remove(device)
                adapter.notifyDataSetChanged()
                bluetooth.startScanning()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onError(message: String?) {
                Toast.makeText(this@ScanningActivity, "Error while pairing", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
        })
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun initListeners() {
        binding.refreshLayout.setOnRefreshListener { bluetooth.startScanning() }
    }

    private fun initViews() {
        binding.printers.adapter = adapter
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        }
        else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (arePermissionsGranted(permissions)) {
            bluetooth.onStart()

            if (!bluetooth.isEnabled)
                bluetooth.enable()

            Handler(Looper.getMainLooper()).postDelayed({
                bluetooth.startScanning()
            }, 1000)
        }
        else {
            requestPermissionsLauncher.launch(permissions)
        }
    }

    override fun onStop() {
        super.onStop()
        bluetooth.onStop()
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
        if (permissionsResult.containsValue(false)) {
            // TODO: toast
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    @SuppressLint("NotifyDataSetChanged", "MissingPermission")
    private val printerClickListener = OnClickListener {
        if (ContextCompat.checkSelfPermission(this@ScanningActivity, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            val device = devices[it.tag as Int]

            if (device.bondState == BluetoothDevice.BOND_BONDED) {
                Printooth.setPrinter(device.name, device.address)
                setResult(Activity.RESULT_OK)
                this@ScanningActivity.finish()
            } else if (device.bondState == BluetoothDevice.BOND_NONE) {
                bluetooth.pair(device)
            }

            adapter.notifyDataSetChanged()
        }
    }

    inner class BluetoothDevicesAdapter(private val itemClickListener: OnClickListener) : RecyclerView.Adapter<BluetoothDevicesAdapter.BluetoothDevicesViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDevicesViewHolder {
            val binding = BluetoothDeviceRowBinding.inflate(layoutInflater, parent, false)
            binding.root.setOnClickListener(itemClickListener)

            return BluetoothDevicesViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return devices.size
        }

        override fun onBindViewHolder(holder: BluetoothDevicesViewHolder, position: Int) {
            devices[position].let { holder.bind(position, it) }
        }

        inner class BluetoothDevicesViewHolder(private val binding: BluetoothDeviceRowBinding): RecyclerView.ViewHolder(binding.root) {
            fun bind(position: Int, device: BluetoothDevice) {
                if (ContextCompat.checkSelfPermission(binding.root.context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                    binding.root.tag = position

                    binding.name.text = if (device.name.isNullOrEmpty()) device.address else device.name
                    binding.pairStatus.visibility = if (device.bondState != BluetoothDevice.BOND_NONE) View.VISIBLE else View.INVISIBLE
                    binding.pairStatus.text = when (device.bondState) {
                        BluetoothDevice.BOND_BONDED -> "Paired"
                        BluetoothDevice.BOND_BONDING -> "Pairing.."
                        else -> ""
                    }
                    binding.pairedPrinter.visibility = if (Printooth.getPairedPrinter()?.address == device.address) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
