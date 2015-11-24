package domain;

import iot.Manager;

import org.eclipse.paho.client.mqttv3.*;

/**
 * 
 * @author David
 * 
 */

public class MqttCb implements MqttCallback {
	
	private Manager manager;
	
	public MqttCb(Manager m) {
		this.manager = m;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println(arg0.getMessage());
		System.out.println("Connection lost!");
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		/**
		 * No publications will be performed
		 */
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String mess = new String(message.getPayload());
	
		System.out.println("-------------------------------------------------");
		System.out.println("| Message: " + mess);
		System.out.println("-------------------------------------------------");
		
		manager.manageMessage(topic, mess);
	}
}
