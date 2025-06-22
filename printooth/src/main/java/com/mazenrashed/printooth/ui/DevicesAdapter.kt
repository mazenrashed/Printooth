package com.mazenrashed.printooth.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.databinding.BluetoothDeviceRowBinding

class DevicesAdapter(private val itemClickListener: OnClickListener) : RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>() {

    private var devices = ArrayList<BluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        val binding = BluetoothDeviceRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener(itemClickListener)

        return DevicesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        devices[position].let { holder.bind(position, it) }
    }

    fun setItems(items: List<BluetoothDevice>?) {
        devices.clear()

        if (items?.isNotEmpty() == true) {
            devices.addAll(items)
        }
    }

    fun addItem(device: BluetoothDevice): Boolean {
        var result = false

        if (!devices.contains(device)) {
            devices.add(device)
            result = true
        }

        return result
    }

    fun removeItem(device: BluetoothDevice): Boolean {
        var result = false

        if (devices.contains(device)) {
            devices.remove(device)
            result = true
        }

        return result
    }

    fun getItem(position: Int): BluetoothDevice {
        return devices[position]
    }

    fun isNotEmpty(): Boolean {
        return devices.isNotEmpty()
    }

    inner class DevicesViewHolder(private val binding: BluetoothDeviceRowBinding): RecyclerView.ViewHolder(binding.root) {
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