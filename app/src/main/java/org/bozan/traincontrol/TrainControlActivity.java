package org.bozan.traincontrol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.bozan.traincontrol.device.DeviceController;
import org.bozan.traincontrol.device.TrainControlDevice;
import org.bozan.traincontrol.device.bt.BlueToothController;
import org.bozan.traincontrol.device.NoDevice;
import org.bozan.traincontrol.device.bt.TrainControlBluetoothDevice;
import org.bozan.traincontrol.device.mqtt.MqttController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TrainControlActivity extends Activity implements ActivityCallback {

  String TAG = TrainControlActivity.class.getName();

  private static final int ITEM_GROUP = 13158;

  private DeviceController deviceController;
  private final List<TrainControlDevice> controlDevices = new ArrayList<>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_train_control);

    showDeviceImage(new NoDevice());
    initTrainDirection();
    initTrainSpeed();
    initLights();
//    initBlueTooth();
    initMqtt();
  }

  private void initMqtt() {
    deviceController = new MqttController(this);
    deviceController.init();
  }

  private void initBlueTooth() {
    deviceController = new BlueToothController(this);
    deviceController.init();
  }

  @Override
  protected void onDestroy() {
    deviceController.onDestroy();
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_train_control, menu);
    return true;
  }

  @Override
  public void onTrainControlDevicesUpdated(List<TrainControlBluetoothDevice> controlDevices) {
    TrainControlActivity.this.controlDevices.clear();
    TrainControlActivity.this.controlDevices.addAll(controlDevices);
  }

  public List<TrainControlDevice> getControlDevices() {
    return controlDevices;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    for (int i = 0; i < controlDevices.size(); i++) {
      if (!(controlDevices.get(i) instanceof NoDevice)) {
        MenuItem item = menu.findItem(controlDevices.get(i).id);
        if (item != null) {
          menu.removeItem(controlDevices.get(i).id);
        }
        menu.add(ITEM_GROUP, controlDevices.get(i).id, Menu.NONE, controlDevices.get(i).toString());
      }
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public void onConnectionStateChanged(final boolean connected) {
    runOnUiThread(() -> {
      ImageView iv = findViewById(R.id.trainConnected);
      iv.setImageResource(connected ? R.drawable.led_green : R.drawable.led_red);
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_rescan) {
      Log.d(TAG, "Rescan");
      deviceController.send("N");
      return true;
    }

    for (TrainControlDevice device : controlDevices) {
      if (device.id == id) {
        try {
          Log.d(TAG, "Device " + device + " active.");
          deviceController.activateControl(device);
          showDeviceImage(device);
          return true;
        } catch (IOException ex) {
          Log.e(TAG, ex.getMessage());
          Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
      }
    }

    return super.onOptionsItemSelected(item);
  }

  private void showDeviceImage(TrainControlDevice device) {
    ImageView lokView = findViewById(R.id.trainImageView);
    switch (device.id) {
      case 7939:
        lokView.setImageResource(R.drawable.img7939);
        return;
      case 60197:
        lokView.setImageResource(R.drawable.img60197);
        return;
      case 10233:
        lokView.setImageResource(R.drawable.img10233);
        return;
      case 60052:
        lokView.setImageResource(R.drawable.img60052);
        return;
      case 60198:
        lokView.setImageResource(R.drawable.img60198);
        return;
    }
    lokView.setImageResource(R.drawable.keine_lok);
  }

  private void initTrainDirection() {
    RadioGroup radioDirection = findViewById(R.id.radioDirection);
    radioDirection.setOnCheckedChangeListener((group, checkedId) -> {
      SeekBar trainSpeed = findViewById(R.id.trainSpeed);
      trainSpeed.setProgress(0);

      switch (checkedId) {
        case R.id.radioForward:
          deviceController.send("S000");
          deviceController.send("D0");
          break;
        case R.id.radioReverse:
          deviceController.send("S000");
          deviceController.send("D1");
          break;
      }
    });
  }

  private void initTrainSpeed() {
    SeekBar trainSpeed = findViewById(R.id.trainSpeed);
    trainSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      String speed;

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        speed = SpeedValues.getSpeedForProgress(progress);
        deviceController.send(speed);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(getApplicationContext(), "progress " + speed, Toast.LENGTH_SHORT).show();
//        deviceController.send(speed);
      }
    });
  }

  private void initLights() {
    RadioGroup radioLights = findViewById(R.id.radioLights);
    radioLights.setOnCheckedChangeListener((group, checkedId) -> {
      switch (checkedId) {
        case R.id.radioLightOn:
          deviceController.send("L1");
          break;
        case R.id.radioLightOff:
          deviceController.send("L0");
          break;
      }
    });

  }
}

