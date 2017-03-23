package domain;

import static iot.Manager.CURRENT_STEP;

public class Debugger {

    public static boolean isEnabled() {
        return false;
	}
	
	public static void log(Object o) {
		System.out.print(CURRENT_STEP + " ");
        System.out.println(o.toString());
	}

}
