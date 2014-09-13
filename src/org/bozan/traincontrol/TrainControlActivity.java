package org.bozan.traincontrol;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.bozan.traincontrol.bt.BlueToothController;
import org.bozan.traincontrol.bt.TrainControlWorkerThread;
import org.bozan.traincontrol.bt.TrainControlDevice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import static org.apache.commons.lang.StringUtils.leftPad;

public class TrainControlActivity extends Activity {

  private BlueToothController blueToothController;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);


    initTrainSelector();
    initTrainDirection();
    initTrainSpeed();
    initBlueTooth();
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

  private void initBlueTooth() {
    blueToothController = new BlueToothController(this, new ActivityCallback() {
      @Override
      public void onTrainControlDevicesUpdated(List<TrainControlDevice> controlDevices) {
        ArrayAdapter<TrainControlDevice> adapter = new ArrayAdapter<TrainControlDevice>(getApplicationContext(), android.R.layout.simple_spinner_item);
        Spinner trainChooser = (Spinner) findViewById(R.id.spinnerTrainChooser);
        trainChooser.setAdapter(adapter);
        adapter.clear();
        adapter.addAll(controlDevices);
        adapter.notifyDataSetChanged();
      }
    });

    blueToothController.init();
//
//
//    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//    if (bluetoothAdapter == null) {
//      Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
//      return;
//    }
//
//    if (!bluetoothAdapter.isEnabled()) {
//      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//    }
//
//    Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
//    for (BluetoothDevice device : bondedDevices) {
//      addPotentialSPPDevice(device);
//    }
//
//    // Register the BroadcastReceiver
//    IntentFilter intent = new IntentFilter();
//    intent.addAction(BluetoothDevice.ACTION_FOUND);
//    intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//    intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//    intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//    registerReceiver(btBroadcastReceiver, intent);
  }

  private void initTrainSelector() {
    final Spinner trainChooser = (Spinner) findViewById(R.id.spinnerTrainChooser);
    trainChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
        TrainControlDevice device = (TrainControlDevice) trainChooser.getItemAtPosition(position);
        try {
          blueToothController.activateControl(device);
        } catch (IOException ex) {
          Log.e(getClass().getName(), ex.getMessage());
          Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void initTrainDirection() {
    RadioGroup radioDirection = (RadioGroup) findViewById(R.id.radioDirection);
    radioDirection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
          case R.id.radioForward:
            blueToothController.send("S000");
            blueToothController.send("D0");
            break;
          case R.id.radioReverse:
            blueToothController.send("S000");
            blueToothController.send("D1");
            break;
        }
//        RadioButton rb = (RadioButton) findViewById(checkedId);
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
//        this.progress = Math.max(0, progress - 10);
//        send("S" + leftPad(String.valueOf(progress * 2.5), 3, '0'));
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

/*
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_ENABLE_BT:
    }
  }
*/

/*
  private void addPotentialSPPDevice(BluetoothDevice device) {
    try {
      if (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.Major.UNCATEGORIZED) {
        // try get SPP socket
        BluetoothSocket btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
        if (btSocket != null) {
          switch (device.getBondState()) {
            case BluetoothDevice.BOND_NONE:
              bondDevice(device);
              return;
            case BluetoothDevice.BOND_BONDED:
              connect(device);
              return;
          }
        }
      }
    } catch (Exception ex) {
      Log.e(getClass().getName(), ex.getMessage());
    }
  }
*/

/*
  private void bondDevice(BluetoothDevice device) throws Exception {
    Method createBond = device.getClass().getMethod("createBond", (Class[]) null);
    createBond.invoke(device, (Object[]) null);
  }
*/

/*
  private void setPin(BluetoothDevice device, String pin) throws Exception {
    Thread.sleep(500);
    Method setPin = device.getClass().getMethod("setPin", new Class[]{byte[].class});
    setPin.invoke(device, pin.getBytes());
  }
*/

/*
  private void connect(BluetoothDevice device) throws Exception {
    TrainControlWorkerThread worker = new TrainControlWorkerThread(this, device);
    worker.start();
    Thread.sleep(1000);
    worker.send("N");
  }
*/


/*
  private void addWorkerThread(String name, WorkerThread thread) {
    workerThreads.put(name, thread);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item);
    Spinner trainChooser = (Spinner) findViewById(R.id.spinnerTrainChooser);
    trainChooser.setAdapter(adapter);
    adapter.clear();
    adapter.addAll(workerThreads.keySet());
    adapter.notifyDataSetChanged();
    if (activeTrainWorker == null) {
      activeTrainWorker = thread;
    }
  }
*/

/*
  private final BroadcastReceiver btBroadcastReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      // When discovery finds a device
      try {
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
          BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          addPotentialSPPDevice(device);

          Toast.makeText(context, "Bluetooth Device found: " + device.getName(), Toast.LENGTH_SHORT).show();

        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
          BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          switch (device.getBondState()) {
            case BluetoothDevice.BOND_BONDING:
              Log.d("BlueToothTestActivity", "it is pairing");
              setPin(device, "1234");
              break;
            case BluetoothDevice.BOND_BONDED:
              Log.d("BlueToothTestActivity", "finish");
              connect(device);
              break;
            default:
              break;
          }
        }
      } catch (Exception ex) {
        Log.e(getClass().getName(), ex.getMessage());
      }
    }
  };
*/

}
