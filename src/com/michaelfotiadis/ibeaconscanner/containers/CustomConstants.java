package com.michaelfotiadis.ibeaconscanner.containers;

/**
 * Class for storing constants used throughout the app
 * 
 * @author Michael Fotiadis
 * 
 */
public class CustomConstants {
	
	public enum Broadcasts {
		BROADCAST_1("Brodacast_1"),
		BROADCAST_2("Broadcast_2");
		
		private String text;
		Broadcasts(String description) {
			text = description;
		}
		
		public String getString() {
			return text;
		}
		
	}; 
	
	public enum Payloads {
		PAYLOAD_1("Payload_1"),
		PAYLOAD_2("Payload_2");
		
		private String text;
		Payloads(String description) {
			text = description;
		}
		
		public String getString() {
			return text;
		}
	}; 
	
	public enum Results {
		RESULT_1("Result_1"),
		RESULT_2("Result_2");
		
		private String text;
		Results(String description) {
			text = description;
		}
		
		public String getString() {
			return text;
		}
	}; 
	
	public enum Requests {
		REQUEST_CODE_1(1),
		REQUEST_CODE_2(2),
		REQUEST_CODE_3(3);
		
		private int code;
		Requests(int number) {
			code = number;
		}
		
		public int getCode() {
			return code;
		}
	}; 
	
    public static final int REQUEST_CODE_1 = 1;
    public static final int REQUEST_CODE_2 = 2;
    public static final int REQUEST_CODE_3 = 3;
    
	// Device identifier string
	public static final String BLUETOOTH_DEVICE = "Bluetooth LE Device";

	// Result for the BroadCast Receiver
	public static boolean SERVICE_RESULT; 

	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	// Configuration File status boolean
	public static boolean IS_CONFIG_FILE_SET = false;

	public static String CLOSEST_DEVICE;
	
}
