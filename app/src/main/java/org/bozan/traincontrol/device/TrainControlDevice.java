package org.bozan.traincontrol.device;

import org.bozan.traincontrol.ActivityCallback;

import java.util.Random;

public abstract class TrainControlDevice {
  public final int id;
  protected String trainName;
  protected boolean connected;
  protected ActivityCallback activityCallback;

  public TrainControlDevice(int id) {
    this.id = id;
  }

  public TrainControlDevice() {
    this(new Random().nextInt());
  }

  public String getTrainName() {
    return trainName;
  }

  public void setTrainName(String trainName) {
    this.trainName = trainName;
  }


  public void setConnectionListener(ActivityCallback callback) {
    this.activityCallback = callback;
  }

  public boolean isConnected() {
    return connected;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
    if (activityCallback != null) {
      activityCallback.onConnectionStateChanged(connected);
    }
  }

  @Override
  public String toString() {
    return trainName;
  }
}
