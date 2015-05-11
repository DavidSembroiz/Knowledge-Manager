package domain;


public class Main {
		
	public static void main(String[] args) {
		
		Database awsdb = new Database();
		Mqtt m = new Mqtt(awsdb);
		m.start();
		
	}
}