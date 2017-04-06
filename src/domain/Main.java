package domain;

import iot.Manager;


public class Main {
		
	public static void main(String[] args) {
		
        System.err.close();

		for (int iter = 0; iter < 1; ++iter) {
            new Manager();
        }
	}
}