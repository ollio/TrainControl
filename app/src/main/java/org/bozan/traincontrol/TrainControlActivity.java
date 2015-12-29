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
import org.bozan.traincontrol.bt.BlueToothController;
import org.bozan.traincontrol.bt.NoDevice;
import org.bozan.traincontrol.bt.TrainControlDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TrainControlActivity extends Activity implements ActivityCallback {

  String TAG = TrainControlActivity.class.getName();

  private static final int ITEM_GROUP = 13158;

  private BlueToothController blueToothController;
  private final List<TrainControlDevice> controlDevices = new ArrayList<>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_train_control);

    showDeviceImage(new NoDevice());
    initTrainDirection();
    initTrainSpeed();
    initBlueTooth();
  }

  private void initBlueTooth() {
    blueToothController = new BlueToothController(this);
    blueToothController.init();
  }

  @Override
  protected void onDestroy() {
    blueToothController.onDestroy();
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
  public void onTrainControlDevicesUpdated(List<TrainControlDevice> controlDevices) {
    TrainControlActivity.this.controlDevices.clear();
    TrainControlActivity.this.controlDevices.addAll(controlDevices);
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
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ImageView iv = (ImageView) findViewById(R.id.trainConnected);
        iv.setImageResource(connected ? R.drawable.led_green : R.drawable.led_red);
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_rescan) {
      Log.d(TAG, "Rescan");
      return true;
    }

    for (TrainControlDevice device : controlDevices) {
      if (device.id == id) {
        try {
          blueToothController.activateControl(device);
          Log.d(TAG, "Device " + device + " active.");
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
    ImageView lokView = (ImageView) findViewById(R.id.trainImageView);
    if (StringUtils.containsIgnoreCase(device.toString(), "gelb")) {
      lokView.setImageResource(R.drawable.gelbe_lok);
      return;
    }
    if (StringUtils.containsIgnoreCase(device.toString(), "horizon")) {
      lokView.setImageResource(R.drawable.horizon_express);
      return;
    }
    if (StringUtils.containsIgnoreCase(device.toString(), "blau")) {
      lokView.setImageResource(R.drawable.blaue_lok);
      return;
    }
    lokView.setImageResource(R.drawable.keine_lok);
  }

  private void initTrainDirection() {
    RadioGroup radioDirection = (RadioGroup) findViewById(R.id.radioDirection);
    radioDirection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
          case R.id.radioForward:
            blueToothController.send("S000");
            blueToothController.send("D0");
            break;
          case R.id.radioReverse:
            blueToothController.send("S000");
            blueToothController.send("D1");
            break;
        }
      }
    });
  }

  private void initTrainSpeed() {
    SeekBar trainSpeed = (SeekBar) findViewById(R.id.trainSpeed);
    trainSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      String speed;

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        speed = SpeedValues.getSpeedForProgress(progress);
        blueToothController.send(speed);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(getApplicationContext(), "progress " + speed, Toast.LENGTH_SHORT).show();
        blueToothController.send(speed);
      }
    });
  }
}

