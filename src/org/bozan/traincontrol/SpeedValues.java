package org.bozan.traincontrol;

import java.util.HashMap;
import java.util.Map;

public class SpeedValues {

  static Map<Integer, String> speedsteps = new HashMap<Integer, String>();

  static {
    speedsteps.put(0, "000");
    speedsteps.put(1, "060");
    speedsteps.put(2, "080");
    speedsteps.put(3, "100");
    speedsteps.put(4, "120");
    speedsteps.put(5, "140");
    speedsteps.put(6, "160");
    speedsteps.put(7, "180");
    speedsteps.put(8, "200");
    speedsteps.put(9, "240");
    speedsteps.put(10, "255");

  }

  public static String getSpeedForProgress(int progress) {
    return "S" + speedsteps.get(progress / 10);
  }

}
