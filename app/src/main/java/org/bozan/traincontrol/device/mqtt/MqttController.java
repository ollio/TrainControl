package org.bozan.traincontrol.device.mqtt;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.bozan.traincontrol.TrainControlActivity;
import org.bozan.traincontrol.device.DeviceController;
import org.bozan.traincontrol.device.TrainControlDevice;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.function.Predicate;

import static java.lang.Integer.valueOf;
import static java.util.UUID.randomUUID;

public class MqttController implements DeviceController {

  private static final String TOPIC = "train";
  private static final String TOPIC_CONTROLLER = "train/controller";
  private MqttAndroidClient client;
  private TrainControlActivity activity;
  private TrainControlDevice device;

  public MqttController(TrainControlActivity activity) {
    this.activity = activity;
  }

  @Override
  public void init() {
    try {
      String clientId = randomUUID().toString();
      client = new MqttAndroidClient(activity.getApplicationContext(), "tcp://iot.eclipse.org:1883", clientId);
      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
          device.setConnected(false);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
          int id = valueOf(StringUtils.substringAfterLast(topic, "/"));
          String payload = StringUtils.trim(new String(message.getPayload()));

          Log.i(getClass().getName(), "Received ["+id+"] -> " + payload);

          handleMessage(id, payload);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
      });

      MqttConnectOptions options = new MqttConnectOptions();
      options.setAutomaticReconnect(true);
      options.setCleanSession(true);
      options.setConnectionTimeout(10);

      client.connect(options, null, new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
          disconnectedBufferOptions.setBufferEnabled(true);
          disconnectedBufferOptions.setBufferSize(100);
          disconnectedBufferOptions.setPersistBuffer(false);
          disconnectedBufferOptions.setDeleteOldestMessages(false);
          client.setBufferOpts(disconnectedBufferOptions);
          subscribeToTopic();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          Log.e(getClass().getName(), "Failed to connect: " + exception.getMessage());
        }
      });
    } catch (MqttException ex) {
      Log.e(getClass().getName(), ex.getMessage());
    }
  }

  private void subscribeToTopic() {
    try {
      client.subscribe(TOPIC + "/#", 0, null, new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          Log.i(getClass().getName(), "Subscribed!");
          send("N");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable ex) {
          Log.e(getClass().getName(), "Failed to subscribe: " + ex.getMessage());
        }
      });
    } catch (MqttException ex) {
      System.err.println("Exception whilst subscribing");
      ex.printStackTrace();
    }
  }

  private void handleMessage(int id, String message) {
    // TODO check id == device.id
    if (message.startsWith("N:")) {
      String trainName = message.substring(2);

      TrainControlDevice existing = null;
      for (TrainControlDevice d : activity.getControlDevices()) {
        if (d.id == id) {
          existing = d;
          break;
        }
      }

      if (existing == null) {
        this.device = new MqttDevice(id);
        this.device.setConnectionListener(activity);
        this.device.setTrainName(trainName);
        activity.getControlDevices().add(device);
        this.device.setConnected(true);
      } else {
        existing.setTrainName(trainName);
        existing.setConnected(true);
      }
    }
  }

  @Override
  public void onDestroy() {
    try {
      client.disconnect();
    } catch (MqttException ex) {
      Log.e(getClass().getName(), ex.getMessage());
    }
  }

  @Override
  public void activateControl(TrainControlDevice device) throws IOException {
    this.device = device;
    device.setConnectionListener(activity);
  }

  @Override
  public void send(String command) {
    try {
      MqttMessage message = new MqttMessage(command.getBytes());
      message.setQos(1);
      message.setRetained(false);
      if (command.startsWith("N")) {
        client.publish(TOPIC_CONTROLLER, message);
      } else {
        client.publish(TOPIC + "/" + device.id, message);
      }
    } catch (MqttException ex) {
      Log.e(getClass().getName(), ex.getMessage());
    }
  }
}
