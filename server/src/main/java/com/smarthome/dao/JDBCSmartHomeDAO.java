package com.smarthome.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

import com.smarthome.model.Humidity;
import com.smarthome.model.Led;
import com.smarthome.model.Room;
import com.smarthome.model.Temperature;
import com.smarthome.model.User;
import com.smarthome.service.SmartHomeService_V1;

public class JDBCSmartHomeDAO implements SmartHomeDAO{
	private static Connection connection = null;
	private BasicDataSource basicDataSource;
	private final String SENSORS_BOARD_NAME = "Sensors room";
	
	private final String CREATE_TEMPERATURE_SQL = "insert into temperature (temperature_value, timestamp, board_id) values (?, ?, ?);";
	private final String CREATE_HUMIDITY_SQL = "insert into humidity (humidity_value, timestamp, board_id) values (?, ?, ?);";
	private final String CREATE_LED_SQL = "insert into led (led_usage, timestamp, board_id) values (?, ?, ?);";
	
	private final String CREATE_USER_SQL = "insert into user (user_name, user_username, email, password, isadmin, main_room) values (?, ?, ?, ?, ?, ?);";
	private final String UPDATE_USER_SQL = "update user set user_name=?, password=?, email=?, main_room=? where user_username=?;";
	private final String GET_USER_SQL = "select * from user where user_username=?;";
	private final String GET_ALLUSERS_SQL = "select * from user where main_room=?;";
	
	private final String GET_USER_ROOM_SQL = "select * from user_room where user_id=? and room_id=?;";
	private final String CREATE_USER_ROOM_SQL = "insert into user_room (user_id, room_id) values (?, ?);";
	private final String GET_ALLROOM_SQL = "select * from user_room where user_id=?;";
	private final String GET_ROOM_SQL = "select * from room where room_board_id=?;";
	private final String GET_ROOM2_SQL = "select * from room where room_board_id=? and room_name=?;";
	private final String CREATE_ROOM_SQL = "insert into room (room_name, room_board_id) values (?, ?);";
	private final String GET_ROOM_SQL_ROOMID = "select * from room where room_id=?;";
	

	public JDBCSmartHomeDAO(BasicDataSource basicDataSource) {
		try {
			connection = basicDataSource.getConnection();
			this.basicDataSource = basicDataSource;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Boolean roomExists(Connection connection, String id, String name, String query) throws SQLException {
		try(PreparedStatement statement = connection.prepareStatement(query);) {	
			statement.setString(1, id);
			statement.setString(2, name);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				if(searchIdResultSet.next()) {
					return true;
				}else {
					return false;
				}
			}
		}
	}
	
	private Boolean recordExists(Connection connection, String value, String query) throws SQLException {
		try(PreparedStatement statement = connection.prepareStatement(query);) {	
			statement.setString(1, value);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				if(searchIdResultSet.next()) {
					return true;
				}else {
					return false;
				}
			}
		}
	}
	
	private Boolean userRoomSQLRecordExists(Connection connection, int userId, int roomId, String query) throws SQLException {
		try(PreparedStatement statement = connection.prepareStatement(query);) {	
			statement.setInt(1, userId);
			statement.setInt(2, roomId);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				if(searchIdResultSet.next()) {
					return true;
				}else {
					return false;
				}
			}
		}
	}
	
	private int getRoomId(Connection connection, String value, String query) throws SQLException {
		int roomId;
		try(PreparedStatement statement = connection.prepareStatement(query);) {	
			statement.setString(1, value);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				if(searchIdResultSet.next()) {
					roomId = searchIdResultSet.getInt("room_id");
				}else {
					throw new RuntimeException("Expected room_id not found");
				}
			}
		}
		return roomId;
	}
	
	private int getRoomIdAndName(Connection connection, String id, String name, String query) throws SQLException {
		int roomId;
		try(PreparedStatement statement = connection.prepareStatement(query);) {	
			statement.setString(1, id);	
			statement.setString(2, name);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				if(searchIdResultSet.next()) {
					roomId = searchIdResultSet.getInt("room_id");
				}else {
					throw new RuntimeException("Expected room_id not found");
				}
			}
		}
		return roomId;
	}
	
	private Collection<Room> getRooms(Connection connection, int value, String query) throws SQLException {
		List<Room> rooms = new ArrayList<Room>();
		try(PreparedStatement statement = connection.prepareStatement(query);) {	
			statement.setInt(1, value);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				while(searchIdResultSet.next()) {
					int roomId = searchIdResultSet.getInt("room_id");
					Room room = getRoomObject(roomId);
					rooms.add(room);
				}
			}
		}
		return rooms;
	}
	
	private Room getRoomObject(int roomId) throws SQLException {
		try(PreparedStatement statement = connection.prepareStatement(GET_ROOM_SQL_ROOMID);) {	
			statement.setInt(1, roomId);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				if(searchIdResultSet.next()) {
					String name = searchIdResultSet.getString("room_name");
					String id = searchIdResultSet.getString("room_board_id");
					Room room = new Room(id, name);
					return room;
				}else {
					return null;
				}
			}
		}
	}
	
	private int createRoom(Connection connection, String name, String id, String query) throws SQLException, DAOException {
		int roomId;
		try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {	
			statement.setString(1, name);
			statement.setString(2, id);
			statement.execute();
			
			ResultSet roomResultSet = connection.createStatement().executeQuery("SELECT * FROM room where room_board_id='" +  id + "' and room_name='"+  name + "'");
			if (roomResultSet.next()) {
				roomId = roomResultSet.getInt("room_id");
			} else {
				throw new RuntimeException("Expected room_id not found");
			}
		} catch(SQLException sqlException) {
			throw new DAOException("Error creating room", sqlException);
		}
		return roomId;
	}
	
	private void createUserRoom(Connection connection, int userId, int roomId, String query) throws SQLException, DAOException {
		try(PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {	
			statement.setInt(1, userId);
			statement.setInt(2, roomId);
			statement.execute();
			
			ResultSet roomResultSet = connection.createStatement().executeQuery("SELECT * FROM user_room where user_id=" + userId + " and room_id=" + roomId);
			if (roomResultSet.next()) {
			} else {
				throw new RuntimeException("Expected user_room sql record not found");
			}
		} catch(SQLException sqlException) {
			throw new DAOException("Error creating user_room sql record", sqlException);
		}
	}

	@Override
	public void createTemperature(Temperature temperature, Timestamp timestamp) throws DAOException {
		int roomId;
		try(PreparedStatement createTemperatureStatement = connection.prepareStatement(CREATE_TEMPERATURE_SQL, Statement.RETURN_GENERATED_KEYS);
		) {
				if(!recordExists(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL))
				{
					roomId = createRoom(connection, SENSORS_BOARD_NAME, SmartHomeService_V1.PHOTON_CORE_ID, CREATE_ROOM_SQL);
				} else {
					roomId = getRoomId(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL);
				}
			
				PreparedStatement temperatureStatement=createTemperatureStatement;
				temperatureStatement.setString(1, temperature.getValue());
				temperatureStatement.setTimestamp(2, timestamp);
				temperatureStatement.setInt(3, roomId);
				temperatureStatement.execute();
				ResultSet temperatureResultSet = connection.createStatement().executeQuery("SELECT * FROM temperature where timestamp='" + timestamp.toString().split("\\.")[0] +"'");
				if (!temperatureResultSet.next()) {
					throw new RuntimeException("Expected timestamp not found");
				}
		} catch (SQLException sqlException) {
			throw new DAOException("Error creating temperature.", sqlException);
		}
	}

	@Override
	public Collection<Temperature> getAllTemperature() throws SQLException {
		Statement temperatureStatement = connection.createStatement();
		String query = "SELECT * FROM temperature";
		ResultSet temperatureResultSet = temperatureStatement.executeQuery(query);
		List<Temperature> temperatures = new ArrayList<Temperature>();
		while (temperatureResultSet.next()) {
			Temperature temperature =  new Temperature();
			Timestamp timestamp= temperatureResultSet.getTimestamp("timestamp");
			String value = temperatureResultSet.getString("temperature_value");
			temperature.setValue(value);
			temperature.setTime(timestamp.toString());
			temperatures.add(temperature);
		}
		Collections.sort(temperatures);
		return temperatures;
	}
	

	@Override
	public List<Temperature> getTemperature(String startDate, String endDate) throws SQLException {
		int roomId;
		List<Temperature> temperatures = new ArrayList<Temperature>();
		if(recordExists(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL)) {
			roomId = getRoomId(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL);
			Statement temperatureStatement = connection.createStatement();
			String query = "SELECT * FROM temperature where board_id=" + roomId + " AND " + "timestamp BETWEEN '" + startDate + " 00:00:01' AND '" + endDate + " 23:59:59'";
			ResultSet temperatureResultSet = temperatureStatement.executeQuery(query);
			while (temperatureResultSet.next()) {
				Temperature temperature =  new Temperature();
				Timestamp timestamp= temperatureResultSet.getTimestamp("timestamp");
				String value = temperatureResultSet.getString("temperature_value");
				temperature.setValue(value);
				temperature.setTime(timestamp.toString());
				temperatures.add(temperature);
			}
			Collections.sort(temperatures);
		}
		return temperatures;
	}

	@Override
	public void createHumidity(Humidity humidity, Timestamp timestamp) throws DAOException {
		int roomId;
		try(PreparedStatement createHumidityStatement = connection.prepareStatement(CREATE_HUMIDITY_SQL, Statement.RETURN_GENERATED_KEYS);
		) {
				if(!recordExists(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL))
				{
					roomId = createRoom(connection, SENSORS_BOARD_NAME, SmartHomeService_V1.PHOTON_CORE_ID, CREATE_ROOM_SQL);
				} else {
					roomId = getRoomId(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL);
				}
			
				PreparedStatement humidityStatement = createHumidityStatement;
				humidityStatement.setString(1, humidity.getValue());
				humidityStatement.setTimestamp(2, timestamp);
				humidityStatement.setInt(3, roomId);
				humidityStatement.execute();
				
				ResultSet humidityResultSet = connection.createStatement().executeQuery("SELECT * FROM humidity where timestamp='" + timestamp.toString().split("\\.")[0] +"'");
				if (!humidityResultSet.next()) {
					throw new RuntimeException("Expected timestamp not found");
				}
		} catch (SQLException sqlException) {
			throw new DAOException("Error creating humidity.", sqlException);
		}
	}

	@Override
	public Collection<Humidity> getAllHumidity() throws SQLException {
		Statement humidityStatement = connection.createStatement();
		String query = "SELECT * FROM humidity";
		ResultSet humidityResultSet = humidityStatement.executeQuery(query);
		List<Humidity> humidities = new ArrayList<Humidity>();
		while (humidityResultSet.next()) {
			Humidity humidity =  new Humidity();
			Timestamp timestamp= humidityResultSet.getTimestamp("timestamp");
			String value = humidityResultSet.getString("humidity_value");
			humidity.setValue(value);
			humidity.setTime(timestamp.toString());
			humidities.add(humidity);
		}
		Collections.sort(humidities);
		return humidities;
	}

	@Override
	public Collection<Humidity> getHumidity(String startDate, String endDate) throws SQLException {
		List<Humidity> humidities = new ArrayList<Humidity>();
		int roomId;
		if(recordExists(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL)) {
			roomId = getRoomId(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL);
			Statement humidityStatement = connection.createStatement();
			String query = "SELECT * FROM humidity where board_id=" + roomId + " AND " + "timestamp BETWEEN '" + startDate + " 00:00:01' AND '" + endDate + " 23:59:59'";
			ResultSet humidityResultSet = humidityStatement.executeQuery(query);
			while (humidityResultSet.next()) {
				Humidity humidity =  new Humidity();
				Timestamp timestamp= humidityResultSet.getTimestamp("timestamp");
				String value = humidityResultSet.getString("humidity_value");
				humidity.setValue(value);
				humidity.setTime(timestamp.toString());
				humidities.add(humidity);
			}
			Collections.sort(humidities);
		}
		return humidities;
	}

	@Override
	public void createLed(Led led, Timestamp timestamp, String sparkCoreId) throws DAOException {
		int roomId;
		try(PreparedStatement createLedStatement = connection.prepareStatement(CREATE_LED_SQL, Statement.RETURN_GENERATED_KEYS);
		) {
				if(!recordExists(connection,sparkCoreId,GET_ROOM_SQL))
				{
					roomId = createRoom(connection, SENSORS_BOARD_NAME, sparkCoreId, CREATE_ROOM_SQL);
				} else {
					roomId = getRoomId(connection,sparkCoreId,GET_ROOM_SQL);
				}
			
				PreparedStatement ledStatement = createLedStatement;
				ledStatement.setString(1, led.getValue());
				ledStatement.setTimestamp(2, timestamp);
				ledStatement.setInt(3, roomId);
				ledStatement.execute();
				
				ResultSet ledResultSet = connection.createStatement().executeQuery("SELECT * FROM led where timestamp='" + timestamp.toString().split("\\.")[0] + "'");
				if (!ledResultSet.next()) {
					throw new RuntimeException("Expected timestamp not found");
				}
		} catch (SQLException sqlException) {
			throw new DAOException("Error creating led.", sqlException);
		}
	}

	@Override
	public Collection<Led> getAllLedUsage() throws SQLException {
		Statement ledStatement = connection.createStatement();
		String query = "SELECT * FROM led";
		ResultSet ledResultSet = ledStatement.executeQuery(query);
		List<Led> leds = new ArrayList<Led>();
		while (ledResultSet.next()) {
			Led led =  new Led();
			Timestamp timestamp= ledResultSet.getTimestamp("timestamp");
			String value = ledResultSet.getString("led_usage");
			led.setValue(value);
			led.setTime(timestamp.toString());
			leds.add(led);
		}
		Collections.sort(leds);
		return leds;
	}

	@Override
	public Collection<Led> getLedUsage(String startDate, String endDate, String sparkCoreId) throws SQLException {
		int roomId;
		List<Led> leds = new ArrayList<Led>();
		if(recordExists(connection,sparkCoreId,GET_ROOM_SQL)) {
			roomId = getRoomId(connection,sparkCoreId,GET_ROOM_SQL);
			Statement ledStatement = connection.createStatement();
			String query = "SELECT * FROM led where board_id=" + roomId + " AND " + "timestamp BETWEEN '" + startDate + " 00:00:01' AND '" + endDate + " 23:59:59'";
			ResultSet ledResultSet = ledStatement.executeQuery(query);

			while (ledResultSet.next()) {
				Led led =  new Led();
				Timestamp timestamp= ledResultSet.getTimestamp("timestamp");
				String value = ledResultSet.getString("led_usage");
				led.setValue(value);
				led.setTime(timestamp.toString());
				leds.add(led);
			}
			Collections.sort(leds);
		}
		return leds;
	}

	@Override
	public void createUser(User user) throws DAOException {
		try(PreparedStatement createUserStatement = connection.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS);
			PreparedStatement updateUserStatement = connection.prepareStatement(UPDATE_USER_SQL, Statement.RETURN_GENERATED_KEYS);
			) {
					PreparedStatement userStatement=createUserStatement;
					if(recordExists(connection,user.getUsername(),GET_USER_SQL))
					{
						userStatement=updateUserStatement;
						userStatement.setString(1, user.getName());
						userStatement.setString(2, user.getPassword());
						userStatement.setString(3, user.getEmail());
						int mainRoomId;
						if(!recordExists(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL))
						{
							mainRoomId = createRoom(connection, SENSORS_BOARD_NAME, SmartHomeService_V1.PHOTON_CORE_ID, CREATE_ROOM_SQL);
						} else {
							mainRoomId = getRoomId(connection,SmartHomeService_V1.PHOTON_CORE_ID,GET_ROOM_SQL);
						}
						userStatement.setInt(4, mainRoomId);
						userStatement.setString(5, user.getUsername());
						userStatement.execute();
					} else {
						userStatement.setString(1, user.getName());
						userStatement.setString(2, user.getUsername());
						userStatement.setString(3, user.getEmail());
						userStatement.setString(4, user.getPassword());
						userStatement.setBoolean(5, user.isAdmin());
						
						int mainRoomId;
						if(!roomExists(connection,user.getMainRoom().getId(),"House",GET_ROOM2_SQL))
						{
							mainRoomId = createRoom(connection, user.getMainRoom().getRoomName(), user.getMainRoom().getId(), CREATE_ROOM_SQL);
						} else {
							mainRoomId = getRoomIdAndName(connection,user.getMainRoom().getId(),"House",GET_ROOM2_SQL);
						}
						userStatement.setInt(6, mainRoomId);
						userStatement.execute();
					}
					
					ResultSet userResultSet = connection.createStatement().executeQuery("SELECT * FROM user where user_username='" + user.getUsername() + "'");
					if (userResultSet.next()) {
						for (Room room:user.getRooms()) {
							int roomId;
							if(!roomExists(connection,room.getId(),room.getRoomName(),GET_ROOM2_SQL)) {
								roomId = createRoom(connection, room.getRoomName(), room.getId(), CREATE_ROOM_SQL);
							} else {
								roomId = getRoomIdAndName(connection,room.getId(),room.getRoomName(),GET_ROOM2_SQL);
							}
							int userId = userResultSet.getInt("user_id");
							if(!userRoomSQLRecordExists(connection,userId,roomId,GET_USER_ROOM_SQL)) {
								createUserRoom(connection, userId, roomId, CREATE_USER_ROOM_SQL);
							}
						}
					} else {
						throw new RuntimeException("Expected user not found");
					}
			} catch (SQLException sqlException) {
				throw new DAOException("Error creating user.", sqlException);
			}
	}

	@Override
	public User getUser(String userName) throws SQLException {
		Statement userStatement = connection.createStatement();
		String query = "SELECT * FROM user where user_username='" + userName + "'";
		ResultSet userResultSet = userStatement.executeQuery(query);
		User user =  new User();
		if (userResultSet.next()) {
			int id = userResultSet.getInt("user_id");
			String name = userResultSet.getString("user_name");
			String username = userResultSet.getString("user_username");
			String password = userResultSet.getString("password");
			String email = userResultSet.getString("email");
			boolean isAdmin = userResultSet.getBoolean("isadmin");
			user.setId(Integer.toString(id));
			user.setName(name);
			user.setUsername(username);
			user.setPassword(password);
			user.setEmail(email);
			user.setAdmin(isAdmin);
			
			List<Room> rooms = (List<Room>) getRooms(connection, id, GET_ALLROOM_SQL);
			Room mainRoom = getRoomObject(userResultSet.getInt("main_room"));
			user.setmainRoom(mainRoom);
			user.setRooms(rooms);
		}
		return user;
	}

	@Override
	public boolean isUser(String username, String password) throws SQLException {
		String query = "SELECT * FROM user where user_username='" + username + "' AND password='" + password + "'" ;
		try(PreparedStatement statement = connection.prepareStatement(query);) {		
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				if(searchIdResultSet.next()) {
					return true;
				}else {
					return false;
				}
			}
		}
	}

	@Override
	public List<User> getAllUserInHouse(String houseId) throws SQLException {
		int roomId = getRoomIdAndName(connection,houseId,"House",GET_ROOM2_SQL);
		List<User> users = new ArrayList<User>();
		try(PreparedStatement statement = connection.prepareStatement(GET_ALLUSERS_SQL);) {	
			statement.setInt(1, roomId);	
			try(ResultSet searchIdResultSet = statement.executeQuery();) {
				while(searchIdResultSet.next()) {
					User user =  new User();
					int id = searchIdResultSet.getInt("user_id");
					String name = searchIdResultSet.getString("user_name");
					String username = searchIdResultSet.getString("user_username");
					String password = searchIdResultSet.getString("password");
					String email = searchIdResultSet.getString("email");
					boolean isAdmin = searchIdResultSet.getBoolean("isadmin");
					user.setId(Integer.toString(id));
					user.setName(name);
					user.setUsername(username);
					user.setPassword(password);
					user.setEmail(email);
					user.setAdmin(isAdmin);
					
					List<Room> rooms = (List<Room>) getRooms(connection, id, GET_ALLROOM_SQL);
					Room mainRoom = getRoomObject(searchIdResultSet.getInt("main_room"));
					user.setmainRoom(mainRoom);
					user.setRooms(rooms);
					users.add(user);
				}
			}
		}
		return users;
	}
	
}
