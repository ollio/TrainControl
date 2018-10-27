package org.bozan.traincontrol;


import org.bozan.traincontrol.device.bt.ConnectionListener;
import org.bozan.traincontrol.device.bt.TrainControlBluetoothDevice;

import java.util.List;

public interface ActivityCallback extends ConnectionListener {

  void onTrainControlDevicesUpdated(List<TrainControlBluetoothDevice> controlDevices);

}
