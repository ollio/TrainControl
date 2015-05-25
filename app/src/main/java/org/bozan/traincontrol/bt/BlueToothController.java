package org.bozan.traincontrol.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import org.bozan.traincontrol.ActivityCallback;

import java.io.IOException;
import java.util.*;

import static org.bozan.traincontrol.bt.TrainControlWorkerThread.readNameOnly;

public class BlueToothController {

  static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private static final int REQUEST_ENABLE_BT = 100;
  private final BluetoothAdapter bluetoothAdapter;

  private final Activity activity;
  private final List<TrainControlDevice> controlDevices = new ArrayList<>();
  private final ActivityCallback callback;
  private TrainControlWorkerThread activeDeviceWorker;
  private Timer nameLookupTimer = new Timer();

  public BlueToothController(Activity activity) {
    this.activity = activity;
    this.callback = (ActivityCallback) activity;
    this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  }

  public void init() {

    if (bluetoothAdapter == null) {
      Toast.makeText(activity.getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
      return;
    }

    if (!bluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    controlDevices.add(new NoDevice());

    Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
    for (BluetoothDevice device : bondedDevices) {
      addPotentialSPPDevice(device);
    }
  }

  private void addPotentialSPPDevice(BluetoothDevice device) {
    try {
      if (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.Major.UNCATEGORIZED) {
        // try get SPP socket
        BluetoothSocket btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
        if (btSocket != null && device.getBondState() == BluetoothDevice.BOND_BONDED){
          TrainControlDevice controlDevice = new TrainControlDevice(device);
          controlDevices.add(controlDevice);
          callback.onTrainControlDevicesUpdated(controlDevices);
          readName(controlDevice);
        }
      }
    } catch (Exception ex) {
      Log.e(getClass().getName(), ex.getMessage());
    }
  }

  private void readName(TrainControlDevice controlDevice) {
    try {
      readNameOnly(controlDevice);
    } catch (IOException e) {}
  }

  public void send(String command) {
    if(this.activeDeviceWorker != null) {
      this.activeDeviceWorker.send(command);
    }
  }

  public void activateControl(TrainControlDevice activeDevice) throws IOException {
    closeActiveDeviceWorker();
    if(!(activeDevice instanceof NoDevice)) {
      this.activeDeviceWorker = TrainControlWorkerThread.create(activeDevice);
      activeDevice.setConnectionListener(callback);
    }
  }

  public void onDestroy() {
    closeActiveDeviceWorker();
    if (bluetoothAdapter.isDiscovering()) {
      bluetoothAdapter.cancelDiscovery();
    }
  }

  private void closeActiveDeviceWorker() {
    if(this.activeDeviceWorker != null) {
      this.activeDeviceWorker.close();
    }
  }
}
