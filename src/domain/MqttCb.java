package domain;

import iot.Manager;

import org.eclipse.paho.client.mqttv3.*;

/**
 * 
 * @author David
 * 
 */

class MqttCb implements MqttCallback {
	
	private Manager manager;
	private int messCount;
	
	MqttCb(Manager m) {
		this.manager = m;
		this.messCount = 0;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println(arg0.getMessage());
		System.out.println("Connection lost!");
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		/*
		 * No publications will be performed
		 */
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		String mess = new String(message.getPayload());
	
		System.out.println("-------------------------------------------------");
		System.out.println("| Message: " + mess);
		System.out.println("-------------------------------------------------");
		
		this.messCount++;
		manager.manageMessage(topic, mess);
		System.out.println("Message " + messCount);
	}
}
