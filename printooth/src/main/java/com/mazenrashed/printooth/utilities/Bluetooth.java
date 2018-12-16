package com.mazenrashed.printooth.utilities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;

import com.mazenrashed.printooth.data.BluetoothCallback;
import com.mazenrashed.printooth.data.DeviceCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mazenrashed.printooth.data.DiscoveryCallback;

public class Bluetooth {
    private static final int REQUEST_ENABLE_BT = 1111;

    private Context context;
    private UUID uuid;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice device, devicePair;
    private BufferedReader input;
    private OutputStream out;

    private DeviceCallback deviceCallback;
    private DiscoveryCallback discoveryCallback;
    private BluetoothCallback bluetoothCallback;
    private boolean connected;

    public Bluetooth(Context context) {
        initialize(context, UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
    }

    public Bluetooth(Context context, UUID uuid) {
        initialize(context, uuid);
    }

    private void initialize(Context context, UUID uuid) {
        this.context = context;
        this.uuid = uuid;
        this.deviceCallback = null;
        this.discoveryCallback = null;
        this.bluetoothCallback = null;
        this.connected = false;
    }

    private final BroadcastReceiver pairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    context.unregisterReceiver(pairReceiver);
                    if (discoveryCallback != null) {
                        discoveryCallback.onDevicePaired(devicePair);
                    }
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    context.unregisterReceiver(pairReceiver);
                    if (discoveryCallback != null) {
                        discoveryCallback.onDeviceUnpaired(devicePair);
                    }
                }
            }
        }
    };

    public void onStop() {
        context.unregisterReceiver(bluetoothReceiver);
    }

    public void enable() {
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (bluetoothCallback != null) {
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            bluetoothCallback.onBluetoothOff();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            bluetoothCallback.onBluetoothTurningOff();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            bluetoothCallback.onBluetoothOn();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            bluetoothCallback.onBluetoothTurningOn();
                            break;
                    }
                }
            }
        }
    };

    public void connectToAddress(String address, boolean insecureConnection) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        connectToDevice(device, insecureConnection);
    }

    public void connectToAddress(String address) {
        connectToAddress(address, false);
    }

    public void connectToDevice(BluetoothDevice device, boolean insecureConnection) {
        new ConnectThread(device, insecureConnection).start();
    }

    public boolean isConnected() {
        return connected;
    }

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        if (state == BluetoothAdapter.STATE_OFF)
                            if (discoveryCallback != null)
                                discoveryCallback.onError("Bluetooth turned off");
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        if (discoveryCallback != null)
                            discoveryCallback.onDiscoveryStarted();
                        break;

                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        context.unregisterReceiver(scanReceiver);
                        if (discoveryCallback != null)
                            discoveryCallback.onDiscoveryFinished();
                        break;

                    case BluetoothDevice.ACTION_FOUND:
                        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (discoveryCallback != null)
                            discoveryCallback.onDeviceFound(device);
                        break;
                }
            }
        }
    };

    public void send(byte[] msg) {
        sendMessage(msg);
    }

    public void sendImage(byte[] byteArray){
        try {
            out.write(byteArray);
            out.write(new byte[]{0x0b, 0x0c});
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<BluetoothDevice> getPairedDevices() {
        return new ArrayList<>(bluetoothAdapter.getBondedDevices());
    }

    public void startScanning() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(scanReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    public void onStart() {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null)
            bluetoothAdapter = bluetoothManager.getAdapter();
        context.registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    public boolean isEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void onActivityResult(int requestCode, final int resultCode) {
        if (bluetoothCallback != null) {
            if (requestCode == REQUEST_ENABLE_BT) {
                if (resultCode == Activity.RESULT_CANCELED)
                    bluetoothCallback.onUserDeniedActivation();
            }
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (final IOException e) {
            if (deviceCallback != null)
                deviceCallback.onError(e.getMessage());
        }
    }

    public void sendMessage(byte[] msg) {
        try {
            out.write(msg);
        } catch (final IOException e) {
            connected = false;
            if (deviceCallback != null)
                deviceCallback.onDeviceDisconnected(device, e.getMessage());
        }
    }

    public void pair(BluetoothDevice device) {
        context.registerReceiver(pairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        devicePair = device;
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (final Exception e) {
            if (discoveryCallback != null)
                discoveryCallback.onError(e.getMessage());
        }
    }

    public void unPair(BluetoothDevice device) {
        context.registerReceiver(pairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        devicePair = device;
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (final Exception e) {
            if (discoveryCallback != null) {
                discoveryCallback.onError(e.getMessage());
            }
        }
    }

    public void setDeviceCallback(DeviceCallback deviceCallback) {
        this.deviceCallback = deviceCallback;
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
    }

    private class ReceiveThread extends Thread implements Runnable {
        public void run() {
            String msg;
            try {
                while ((msg = input.readLine()) != null) {
                    if (deviceCallback != null) {
                        final String msgCopy = msg;
                        new android.os.Handler(Looper.getMainLooper()).post(() -> deviceCallback.onMessage(msgCopy));
                    }
                }
            } catch (final IOException e) {
                connected = false;
                if (deviceCallback != null)
                    new android.os.Handler(Looper.getMainLooper()).post(() -> deviceCallback.onDeviceDisconnected(device, e.getMessage()));
            }
        }
    }

    public void setBluetoothCallback(BluetoothCallback bluetoothCallback){
        this.bluetoothCallback = bluetoothCallback;
    }

    private class ConnectThread extends Thread {
        ConnectThread(BluetoothDevice device, boolean insecureConnection) {
            Bluetooth.this.device = device;
            try {
                if (insecureConnection) {
                    Bluetooth.this.socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                } else {
                    Bluetooth.this.socket = device.createRfcommSocketToServiceRecord(uuid);
                }
            } catch (IOException e) {
                if (deviceCallback != null) {
                    deviceCallback.onError(e.getMessage());
                }
            }
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
                out = socket.getOutputStream();
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;

                new ReceiveThread().start();

                if (deviceCallback != null)
                    new android.os.Handler(Looper.getMainLooper()).post(() -> deviceCallback.onDeviceConnected(device));

            } catch (final IOException e) {
                if (deviceCallback != null)
                    new android.os.Handler(Looper.getMainLooper()).post(() -> deviceCallback.onConnectError(device, e.getMessage()));

                try {
                    socket.close();
                } catch (final IOException closeException) {
                    if (deviceCallback != null)
                        new android.os.Handler(Looper.getMainLooper()).post(() -> deviceCallback.onError(closeException.getMessage()));
                }
            }
        }
    }
}