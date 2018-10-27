package org.bozan.traincontrol.device.bt;

import android.bluetooth.BluetoothDevice;

import org.bozan.traincontrol.device.TrainControlDevice;

public class TrainControlBluetoothDevice extends TrainControlDevice {

  public final BluetoothDevice device;

  public TrainControlBluetoothDevice(BluetoothDevice device) {
    this.device = device;
  }

  public String getBTName() {
    return device.getName();
  }

  @Override
  public String toString() {
    if (trainName != null) {
      return trainName;
    }
    return device.getName();
  }

  public boolean isConnected() {
    return connected;
  }
}
