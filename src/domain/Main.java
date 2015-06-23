package domain;

import java.util.HashMap;
import java.util.Map;

import behaviour.*;
import behaviour.Person.*;
import iot.Manager;



public class Main {
		
	public static void main(String[] args) {
		
		//new Manager();
		
		PeopleManager pm = new PeopleManager();
		pm.printPeople();
	}
}