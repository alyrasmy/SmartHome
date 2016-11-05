package com.smarthome.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import com.smarthome.model.Humidity;
import com.smarthome.model.Led;
import com.smarthome.model.Temperature;
import com.smarthome.model.User;

public interface SmartHomeDAO {
	public void createTemperature(Temperature temperature, Timestamp timestamp) throws DAOException;
	public Collection<Temperature> getAllTemperature() throws SQLException;
	public Collection<Temperature> getTemperature(String startDate, String endDate) throws SQLException;
	
	public void createHumidity(Humidity humidity, Timestamp timestamp) throws DAOException;
	public Collection<Humidity> getAllHumidity() throws SQLException;
	public Collection<Humidity> getHumidity(String startDate, String endDate) throws SQLException;
	
	public void createLed(Led led, Timestamp timestamp, String sparkCoreId) throws DAOException;
	public Collection<Led> getAllLedUsage() throws SQLException;
	public Collection<Led> getLedUsage(String startDate, String endDate) throws SQLException;
	
	public void createUser(User user, long timestamp) throws DAOException;
	public User getUser(String userName) throws SQLException;
}
