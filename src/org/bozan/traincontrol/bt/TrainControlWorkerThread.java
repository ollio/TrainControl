package org.bozan.traincontrol.bt;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

import static org.bozan.traincontrol.bt.BlueToothController.SPP_UUID;

public class TrainControlWorkerThread extends Thread {
  public final TrainControlDevice control;
  private BluetoothSocket btSocket;
  private PrintWriter out;
  private BufferedReader in;
  private boolean run = true;
  private boolean closeAfterName = false;
  private boolean connected = false;
  private Queue<String> sendQueue = new LinkedList<>();

  private TrainControlWorkerThread(TrainControlDevice control) throws IOException {
    this.control = control;
  }

  public static TrainControlWorkerThread create(TrainControlDevice control) throws IOException {
    TrainControlWorkerThread worker = new TrainControlWorkerThread(control);
    worker.send("N");
    worker.start();
    return worker;
  };

  public static TrainControlWorkerThread readNameOnly(TrainControlDevice control) throws IOException {
    TrainControlWorkerThread worker = new TrainControlWorkerThread(control);
    worker.closeAfterName = true;
    worker.send("N");
    worker.start();
    return worker;
  };

  private void connect() throws IOException {
    this.btSocket = control.device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
    btSocket.connect();
    out = new PrintWriter(btSocket.getOutputStream());
    in = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
    connected = true;
    sendQueue();
  }

  @Override
  public void run() {
    try  {
      connect();

      while (run) {
        String message = in.readLine();
        if (message.startsWith("N:")) {
          String name = message.substring(2);
          control.setTrainName(name);
          if(closeAfterName) {
            close();
          }
        }
      }

    } catch (Exception ex) {
      Log.e(getClass().getName(), ex.getMessage());
    } finally {
      close();
    }
  }

  public void close() {
    connected = false;
    run = false;
    try {
//      close(in);
//      close(out);
      close(btSocket);
    } catch (IOException e) {
    }
  }

  private void close(Closeable closeable) throws IOException {
    if(closeable != null) closeable.close();
  }

  synchronized void send(String message) {
    sendQueue.offer(message);
    sendQueue();
  }

  private void sendQueue() {
    if (connected) {
      while (!sendQueue.isEmpty()){
        out.println(sendQueue.poll());
        out.flush();
      }
    }
  }
}
