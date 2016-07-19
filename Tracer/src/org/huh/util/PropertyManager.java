package org.huh.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager {
	public static PropertyManager instance = new PropertyManager();
	private Properties properties;
	
	private PropertyManager() {
		properties = new Properties();
		init();
	}

	private void init() {
		try {
			properties.load(new FileInputStream(Constants.propertiesFile));
		} catch (IOException e) {
			System.out.println("could not load properties file "+Constants.propertiesFile);
		}
	}
	
	public static void reload(){
		instance.init();
	}


	private Properties getProperties() {
		return properties;
	}

	public static String getProperty(String key, String defaultValue) {
		return instance.getProperties().getProperty(key, defaultValue);
	}

	public static String getProperty(String key) {
		String result = instance.properties.getProperty(key);
		if (result == null) {
			System.out.println("getProperty " + key + " returns null");
		}
		return result;
	}

	public static int getPropertyAsInt(String key) {
		return Integer.parseInt(getProperty(key));
	}

	public static long getPropertyAsLong(String key) {
		return Long.parseLong(getProperty(key));
	}

}
