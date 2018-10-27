package org.bozan.traincontrol;

import java.util.HashMap;
import java.util.Map;

public class SpeedValues {

  static Map<Integer, String> speedsteps = new HashMap<Integer, String>();

  static {
    speedsteps.put(0, "0");
    speedsteps.put(1, "0");
    speedsteps.put(2, "1");
    speedsteps.put(3, "2");
    speedsteps.put(4, "3");
    speedsteps.put(5, "4");
    speedsteps.put(6, "5");
    speedsteps.put(7, "6");
    speedsteps.put(8, "7");
    speedsteps.put(9, "8");
    speedsteps.put(10, "9");

  }

  public static String getSpeedForProgress(int progress) {
    return "S" + speedsteps.get(progress / 10);
  }

}
