package com.smarthome.schedule;

import java.sql.Timestamp;
import java.util.TimerTask;

import com.smarthome.dao.DAOException;
import com.smarthome.dao.SmartHomeDAO;
import com.smarthome.model.Humidity;
import com.smarthome.service.SmartHomeService_V1;

public class HumiditySchedule extends TimerTask{
	private SmartHomeService_V1 smartHomeService;
    private SmartHomeDAO smartHomeDAO;

    public HumiditySchedule(SmartHomeService_V1 smartHomeService, SmartHomeDAO smartHomeDAO)
    {
    	this.smartHomeService = smartHomeService;
    	this.smartHomeDAO = smartHomeDAO;
    }
    
    @Override   
    public void run()
    {
    	System.out.println("Running the humidity job");
    	Humidity humidity = smartHomeService.getHumidityReading(SmartHomeService_V1.PHOTON_CORE_ID); //get humidity using the smart home service
		if(humidity != null) {
			long timeNow = (System.currentTimeMillis()/1000);
			Timestamp timestamp = new Timestamp(timeNow*1000);
			try {
				smartHomeDAO.createHumidity(humidity, timestamp);
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done the humidity job");
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
//		HumiditySchedule humiditySchedule = new HumiditySchedule(smartHomeService, smartHomeDAO);
//		humiditySchedule.run();
//	}
    
}
