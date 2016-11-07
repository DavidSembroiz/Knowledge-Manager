package domain;

import static iot.Manager.CURRENT_STEP;

public class Debugger {

    private static boolean enabled = true;

    public static boolean isEnabled() {
		return enabled;
	}
	
	public static void log(Object o) {
		System.out.print(CURRENT_STEP + " ");
        System.out.println(o.toString());
	}

}
