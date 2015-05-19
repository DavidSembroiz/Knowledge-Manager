package domain;

import org.eclipse.paho.client.mqttv3.*;

public class MqttCb implements MqttCallback {
	
	private Utils uts;
	private Database awsdb;
	
	public MqttCb(Utils uts, Database awsdb) {
		this.uts = uts;
		this.awsdb = awsdb;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println(arg0.getMessage());
		System.out.println("Connection lost!");
		System.exit(-1);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// Not necessary, no publications will be performed
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String soID = uts.extractIdFromTopic(topic);
		
		/**
		 * With the obtained ID, it is possible to query the database to extract
		 * the location and data model.
		 * 
		 * TODO location allows us to create a new Sensor instance and define it
		 * 
		 */
		
		/*String data = awsdb.getDatafile(soID);
		System.out.println(data);*/
		
		
		System.out.println("-------------------------------------------------");
		System.out.println("| Topic: " + topic);
		System.out.println("| Message: " + new String(message.getPayload()));
		System.out.println("-------------------------------------------------");
		
		uts.parseJSON(soID, message);
	}
}
