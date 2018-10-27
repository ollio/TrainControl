package org.bozan.traincontrol.device.bt;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;


public class TrainControlWorkerThread extends Thread {

  private String TAG = getClass().getName();

  private final TrainControlBluetoothDevice control;
  private BluetoothSocket btSocket;
  private PrintWriter out;
  private BufferedReader in;
  private boolean run = true;
  private boolean closeAfterName = false;
  private Queue<String> sendQueue = new LinkedList<>();

  private TrainControlWorkerThread(TrainControlBluetoothDevice control) throws IOException {
    this.control = control;
  }

  public static TrainControlWorkerThread create(TrainControlBluetoothDevice control) throws IOException {
    TrainControlWorkerThread worker = new TrainControlWorkerThread(control);
    worker.send("N");
    worker.start();
    return worker;
  }

  ;

  public static TrainControlWorkerThread readNameOnly(TrainControlBluetoothDevice control) throws IOException {
    TrainControlWorkerThread worker = new TrainControlWorkerThread(control);
    worker.closeAfterName = true;
    worker.send("N");
    worker.start();
    return worker;
  }

  private void connect() throws IOException {
    try { // Secure Socket
      this.btSocket = control.device.createRfcommSocketToServiceRecord(BlueToothController.SPP_UUID);
      btSocket.connect();
      out = new PrintWriter(btSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
      control.setConnected(true);
      sendQueue();
      return;
    } catch (IOException ex) {
      Log.e(TAG, "Secure Socket: " + ex.getMessage());
    }

    // Inecure Socket fallback
    this.btSocket = control.device.createInsecureRfcommSocketToServiceRecord(BlueToothController.SPP_UUID);
    btSocket.connect();
    out = new PrintWriter(btSocket.getOutputStream());
    in = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
    control.setConnected(true);
    sendQueue();
  }

  @Override
  public void run() {
    try {
      connect();

      while (run) {
        String message = in.readLine();
        if (message.startsWith("N:")) {
          String name = message.substring(2);
          control.setTrainName(name);
          if (closeAfterName) {
            close();
          }
        }
      }

    } catch (Exception ex) {
      Log.e(TAG, ex.getMessage());
    } finally {
      close();
    }
  }

  public void close() {
    control.setConnected(false);
    run = false;
    try {
//      close(in);
//      close(out);
      close(btSocket);
    } catch (IOException e) {
    }
  }

  private void close(Closeable closeable) throws IOException {
    if (closeable != null) closeable.close();
  }

  synchronized void send(String message) {
    sendQueue.offer(message);
    sendQueue();
  }

  private void sendQueue() {
    if (control.isConnected()) {
      while (!sendQueue.isEmpty()) {
        out.println(sendQueue.poll());
        out.flush();
      }
    }
  }
}
