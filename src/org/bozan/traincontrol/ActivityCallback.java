package org.bozan.traincontrol;

import org.bozan.traincontrol.bt.TrainControlDevice;

import java.util.List;

public interface ActivityCallback {

  void onTrainControlDevicesUpdated(List<TrainControlDevice> controlDevices);
}
