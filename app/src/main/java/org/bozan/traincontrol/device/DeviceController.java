package org.bozan.traincontrol.device;

import org.bozan.traincontrol.device.bt.TrainControlBluetoothDevice;

import java.io.IOException;

public interface DeviceController {
    void init();

    void onDestroy();

    void activateControl(TrainControlDevice device) throws IOException;

    void send(String command);
}
