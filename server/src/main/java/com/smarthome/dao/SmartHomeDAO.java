package com.smarthome.dao;

import java.util.Collection;

import com.smarthome.model.Humidity;
import com.smarthome.model.Led;
import com.smarthome.model.Temperature;
import com.smarthome.model.User;

public interface SmartHomeDAO {
	public void createTemperature(Temperature temperature, long timestamp);
	public Collection<Temperature> getAllTemperature();
	public Temperature getTemperature(String startDate, String endDate);
	
	public void createHumidity(Humidity humidity, long timestamp);
	public Collection<Humidity> getAllHumidity();
	public Humidity getHumidity(String startDate, String endDate);
	
	public void createLed(Led led, long timestamp);
	public Collection<Led> getAllLedUsage();
	public Led getLedUsage(String startDate, String endDate);
	
	public void createUser(User user, long timestamp);
	public User getUser(String userId);
}
