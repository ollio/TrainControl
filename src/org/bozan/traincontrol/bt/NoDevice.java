package org.bozan.traincontrol.bt;

public class NoDevice extends TrainControlDevice {
  public NoDevice() {
    super(null);
    super.trainName = "Kein Zug gewählt";
  }
}
