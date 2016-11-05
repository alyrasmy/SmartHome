package com.smarthome.schedule;

import java.sql.Timestamp;
import java.util.TimerTask;

import org.apache.commons.dbcp2.BasicDataSource;

import com.smarthome.dao.DAOException;
import com.smarthome.dao.JDBCSmartHomeDAO;
import com.smarthome.dao.SmartHomeDAO;
import com.smarthome.model.Temperature;
import com.smarthome.service.SmartHomeService_V1;

public class TemperatureSchedule extends TimerTask{
	private SmartHomeService_V1 smartHomeService;
    private SmartHomeDAO smartHomeDAO;

    public TemperatureSchedule(SmartHomeService_V1 smartHomeService, SmartHomeDAO smartHomeDAO)
    {
    	this.smartHomeService = smartHomeService;
    	this.smartHomeDAO = smartHomeDAO;
    }
    
    @Override   
    public void run()
    {
    	System.out.println("Running the temeperature job");
    	Temperature temperature = smartHomeService.getTemperatureReading(SmartHomeService_V1.PHOTON_CORE_ID); //get temperature using the smart home service
		if(temperature != null) {
			long timeNow = (System.currentTimeMillis()/1000);
			Timestamp timestamp = new Timestamp(timeNow*1000);
			try {
				smartHomeDAO.createTemperature(temperature, timestamp);
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
    	System.out.println("Done the temeperature job");
    }
    
// Used for Testing    
//    public static void main(String[] args) {
//    	BasicDataSource basicDataSource;
//    	SmartHomeService_V1 smartHomeService;
//        SmartHomeDAO smartHomeDAO;
//    	basicDataSource = new BasicDataSource();
//		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//		basicDataSource.setUsername("team");
//		basicDataSource.setPassword("password");
//		basicDataSource.setUrl("jdbc:mysql://localhost/SmartHome?autoReconnect=true");
//		smartHomeDAO = new JDBCSmartHomeDAO(basicDataSource);
//		smartHomeService = new SmartHomeService_V1();
//    	TemperatureSchedule temperatureSchedule = new TemperatureSchedule(smartHomeService, smartHomeDAO);
//    	temperatureSchedule.run();
//	}
    
}
