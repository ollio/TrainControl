package org.bozan.traincontrol.bt;

import android.bluetooth.BluetoothDevice;

import java.util.Random;

public class TrainControlDevice {

  protected String trainName;
  public final BluetoothDevice device;
  public final int id;
  private boolean connected;
  private ConnectionListener connectionListener;

  public TrainControlDevice(BluetoothDevice device) {
    this.id = new Random().nextInt();
    this.device = device;
  }

  public String getTrainName() {
    return trainName;
  }

  public String getBTName() {
    return device.getName();
  }

  public void setTrainName(String trainName) {
    this.trainName = trainName;
  }

  @Override
  public String toString() {
    if (trainName != null) {
      return trainName;
    }
    return device.getName();
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
    if(connectionListener != null) {
      connectionListener.onConnectionStateChanged(connected);
    }
  }

  public boolean isConnected() {
    return connected;
  }

  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }
}
