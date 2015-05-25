package org.bozan.traincontrol;

import org.bozan.traincontrol.bt.ConnectionListener;
import org.bozan.traincontrol.bt.TrainControlDevice;

import java.util.List;

public interface ActivityCallback extends ConnectionListener {

  void onTrainControlDevicesUpdated(List<TrainControlDevice> controlDevices);

}
