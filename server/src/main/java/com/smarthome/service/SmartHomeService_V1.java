package com.smarthome.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.smarthome.model.Humidity;
import com.smarthome.model.Led;
import com.smarthome.model.Temperature;

public class SmartHomeService_V1 {
    public static final String PHOTON_CORE_ID = "230046001347343339383037";
    public static final String[] SPARK_CORE_IDS = {"53ff72066667574817532367","53ff6f066667574834212367","53ff6f066667574835380967"};
    
	public SmartHomeService_V1() {
	}

	public Temperature getTemperatureReading(String boardId) {
		String url = "https://api.particle.io/v1/devices/" + boardId + "/tempC?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba";
		Temperature temperature = null;
		try {
			String response = getHTML(url);
			String tempValue = response.split("\"result\": ")[1].split(",")[0];
			temperature = new Temperature();
			temperature.setValue(tempValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temperature;
	}
	
	public Humidity getHumidityReading(String boardId) {
		String url = "https://api.particle.io/v1/devices/" + boardId + "/humidity?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba";
		Humidity humidity = null;
		try {
			String response = getHTML(url);
			String humidityValue = response.split("\"result\": ")[1].split(",")[0];
			humidity = new Humidity();
			humidity.setValue(humidityValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return humidity;
	}
	
	public Led getLedUsageReading(String boardId) {
		String url = "https://api.particle.io/v1/devices/" + boardId + "/usageTime?access_token=04b90f278a1415636513f0f71fe9f89e92cdfcba";
		Led led = null;
		try {
			String response = getHTML(url);
			String ledValue = response.split("\"result\": ")[1].split(",")[0];
			led = new Led();
			led.setValue(ledValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return led;
	}
	
	private String getHTML(String urlToRead) throws IOException {
		int count = 0;
		int maxTries = 3;
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		while(true) {
			try {
				conn.setRequestMethod("GET");
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				rd.close();
				return result.toString();
			} catch (Exception e) {
				count++;
				System.out.println("Rest Call to the Board failed on attempt " + count);
				if(count == maxTries) {
					throw e;
				}
			}
		}
	}
	
}
