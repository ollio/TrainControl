package org.bozan.traincontrol.bt;

import android.bluetooth.BluetoothDevice;

public class TrainControlDevice {

  protected String trainName;
  public final BluetoothDevice device;

  public TrainControlDevice(BluetoothDevice device) {
    this.device = device;
  }

  public String getTrainName() {
    return trainName;
  }

  public void setTrainName(String trainName) {
    this.trainName = trainName;
  }

  @Override
  public String toString() {
    if(trainName != null) {
      return trainName;
    }
    return device.getName();
  }
}
