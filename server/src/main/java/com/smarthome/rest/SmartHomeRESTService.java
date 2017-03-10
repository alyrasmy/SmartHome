package com.smarthome.rest;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.dbcp2.BasicDataSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.smarthome.dao.DAOException;
import com.smarthome.dao.JDBCSmartHomeDAO;
import com.smarthome.dao.SmartHomeDAO;
import com.smarthome.model.Humidity;
import com.smarthome.model.Led;
import com.smarthome.model.Room;
//import com.smarthome.dao.SmartHomeDAO;
import com.smarthome.model.Temperature;
import com.smarthome.model.User;
import com.smarthome.schedule.HumiditySchedule;
import com.smarthome.schedule.LedSchedule;
import com.smarthome.schedule.TemperatureSchedule;
import com.smarthome.service.SmartHomeService_V1;

@Path("/api/smarthome")
public class SmartHomeRESTService {
	
	private final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	private BasicDataSource basicDataSource;
	private SmartHomeService_V1 smartHomeService;
    private SmartHomeDAO smartHomeDAO;
	private TemperatureSchedule temperatureSchedule;
	private HumiditySchedule humiditySchedule;
	private LedSchedule ledSchedule;

	public SmartHomeRESTService()
	{
		basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		basicDataSource.setUsername("team");
		basicDataSource.setPassword("password");
		basicDataSource.setUrl("jdbc:mysql://localhost/SmartHome?autoReconnect=true");
		smartHomeDAO = new JDBCSmartHomeDAO(basicDataSource);
		smartHomeService = new SmartHomeService_V1();
		temperatureSchedule = new TemperatureSchedule(smartHomeService, smartHomeDAO);
		humiditySchedule = new HumiditySchedule(smartHomeService, smartHomeDAO);
		ledSchedule = new LedSchedule(smartHomeService, smartHomeDAO);
	}
	
	@POST
	@Path("/authenticate")
	@Produces({MediaType.APPLICATION_JSON})
	public Response authenticate(@FormParam("username") String username,
            @FormParam("password") String password)
	{
		try {
			boolean isUser = smartHomeDAO.isUser(username,password);
			if (isUser) {
				List<User> usersCollection = new ArrayList<User>();
				User user = smartHomeDAO.getUser(username);
				usersCollection.add(user);
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertUserToJSON(usersCollection, true)).build();
			} else {
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("Unsupported user").build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	@GET
	@Produces("text/html")
	public Response getStartingPage()
	{
		String output = "<h1>Hello World!<h1>" +
				"<p>RESTful Service is running ... <br>Ping @ " + new Date().toString() + "</p<br>";
		return Response.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.entity(output).build();
	}
	
	//http response for a list of temperature readings
	@GET
	@Path("/temperatures")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTemperature(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) throws SQLException
	{
		try {
			if(startDate != null && endDate != null) // get all temperature record in the database between these 2 dates
			{
				List<Temperature> temperatureCollection = new ArrayList<Temperature>();
				temperatureCollection = (List<Temperature>) smartHomeDAO.getTemperature(startDate,endDate);
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertTemperatureToJSON(temperatureCollection)).build();
			} else { // get all temperature record in the database
				List<Temperature> temperatureCollection = new ArrayList<Temperature>();
				temperatureCollection = (List<Temperature>) smartHomeDAO.getAllTemperature();
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertTemperatureToJSON(temperatureCollection)).build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	//http response for a list of humidity readings
	@GET
	@Path("/humidities")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getHumidity(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) throws SQLException
	{
		try {
			if(startDate != null && endDate != null) // get all humidity record in the database between these 2 dates
			{
				List<Humidity> humidityCollection = new ArrayList<Humidity>();
				humidityCollection = (List<Humidity>) smartHomeDAO.getHumidity(startDate,endDate);
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertHumidityToJSON(humidityCollection)).build();
			} else { // get all humidity record in the database
				List<Humidity> humidityCollection = new ArrayList<Humidity>();
				humidityCollection = (List<Humidity>) smartHomeDAO.getAllHumidity();
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertHumidityToJSON(humidityCollection)).build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	//http response for a list of led daily usage readings
	@GET
	@Path("/leds")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getLedUsage(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate, @QueryParam("boardId") String boardId) throws SQLException
	{
		try {
			if(startDate != null && endDate != null) // get all led usage record in the database between these 2 dates
			{
				List<Led> ledUsageCollection = new ArrayList<Led>();
				ledUsageCollection = (List<Led>) smartHomeDAO.getLedUsage(startDate,endDate,boardId);
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertLedUsageToJSON(ledUsageCollection)).build();
			} else { // get all led usage record in the database
				List<Led> ledUsageCollection = new ArrayList<Led>();
				ledUsageCollection = (List<Led>) smartHomeDAO.getAllLedUsage();
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertLedUsageToJSON(ledUsageCollection)).build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	//http response for a list of all users in house usage readings
	@GET
	@Path("/users")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllUserInHouse(@QueryParam("houseId") String houseId) throws SQLException
	{
		try {
			if(houseId != null) {
				List<User> usersInHouse = new ArrayList<User>();
				usersInHouse = (List<User>) smartHomeDAO.getAllUserInHouse(houseId);
				return Response.ok()
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity(convertUserToJSON(usersInHouse, false)).build();
			} else {
				return Response.status(SC_INTERNAL_SERVER_ERROR)
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.entity("Query Param houseId is undefined").build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	
	//http response for a user
	@GET
	@Path("/users/{user_id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getUser(@PathParam("user_id") String userId)
	{
		try {
			List<User> usersCollection = new ArrayList<User>();
			User user = smartHomeDAO.getUser(userId);
			usersCollection.add(user);
			return Response.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(convertUserToJSON(usersCollection, true)).build();
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	@POST
	@Path("/job/temperature/{action}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTemperatureJobStatus(@PathParam("action") String action)
	{
		//start the job
		if(action.equals("start"))
		{
			temperatureSchedule.run(); //run the job
			return Response.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("Ran the temperature job schedule successfully").build();
		}else{
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("Unsupported action").build();
		}
	}
	
	@POST
	@Path("/job/humidity/{action}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getHumidityJobStatus(@PathParam("action") String action)
	{
		//start the job
		if(action.equals("start"))
		{
			humiditySchedule.run(); //run the job
			return Response.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("Ran the humidity job schedule successfully").build();
		}else{
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("Unsupported action").build();
		}
	}
	
	@POST
	@Path("/job/led/{action}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getLedJobStatus(@PathParam("action") String action)
	{
		//start the job
		if(action.equals("start"))
		{
			ledSchedule.run(); //run the job
			return Response.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("Ran the led job schedule successfully").build();
		}else{
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("Unsupported action").build();
		}
	}
	
	@POST
	@Path("/user/create")
	@Produces({MediaType.APPLICATION_JSON})
	public Response addUser(@FormParam("isAdmin") boolean isAdmin, @FormParam("name") String name
			, @FormParam("username") String username, @FormParam("password") String password
			, @FormParam("email") String email, @FormParam("houseId") String houseId
			, @FormParam("roomsStringfy") String roomsStringfy, @FormParam("tempthreshold") String temp_threshold
			, @FormParam("humidthreshold") String humid_threshold, @FormParam("ledthreshold") String led_threshold
			, @FormParam("city") String city, @FormParam("address") String address
			, @FormParam("camera") String camera) {

		List<Room> allRooms = new ArrayList<Room>();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(roomsStringfy);
        JsonArray jasonArray = element.getAsJsonArray();
        for(Object js : jasonArray){
            JsonObject json = (JsonObject) js;
            String id = json.get("id").toString();
            String roomName = json.get("name").toString();
			Room room = new Room(id.substring(1, id.length()-1), roomName.substring(1, roomName.length()-1));
			allRooms.add(room);
        }
        
		User user = new User();
		user.setAdmin(isAdmin);
		user.setEmail(email);
		Room house = new Room(houseId, "House");
		user.setmainRoom(house);
		user.setName(name);
		user.setPassword(password);
		user.setRooms(allRooms);
		user.setUsername(username);
		user.setTemp_threshold(temp_threshold);
		user.setHumid_threshold(humid_threshold);
		user.setLed_threshold(led_threshold);
		user.setCity(city);
		user.setAddress(address);
		user.setCamera(camera);
		
		try {
			smartHomeDAO.createUser(user);
			return Response.ok()
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("User successfully registered").build();
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity(GSON.toJson(e.getStackTrace())).build();
		}	
	}
	
	private StreamingOutput convertTemperatureToJSON(final List<Temperature> temperatureCollection)
	{
		return new StreamingOutput(){

			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException
			{
				Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
				JsonWriter jsonWriter = new JsonWriter(writer);
				jsonWriter.setIndent("  "); //enable pretty printing
				jsonWriter.beginObject(); //begin top level wrapper object

				jsonWriter.name("data");
				jsonWriter.beginArray(); //begin top level data array
				for(int i=0; i<temperatureCollection.size();i++)
				{
					Temperature temperature= new Temperature();
					temperature= temperatureCollection.get(i);
					jsonWriter.beginObject(); //begin the temperature object
					jsonWriter.name("type").value("temperature");
					jsonWriter.name("id").value(i);

					jsonWriter.name("attributes");
					jsonWriter.beginObject(); //begin the attributes object
					jsonWriter.name("value").value(temperature.getValue());
					jsonWriter.name("timestamp").value(temperature.getTime());
					jsonWriter.endObject(); //end the attributes object

					jsonWriter.name("relationships");
					jsonWriter.beginObject(); //begin the temperature relationships object

					jsonWriter.endObject(); //end the temperature relationships object

					jsonWriter.endObject(); //end the temperature object
				}
				jsonWriter.endArray(); //end top level data array

				jsonWriter.name("included");
				jsonWriter.beginArray(); //begin top level included array

				jsonWriter.endArray(); //end top level included array

				jsonWriter.endObject(); //end top level wrapper object
				jsonWriter.flush();
				jsonWriter.close();
			}
		};
		
	}
	
	private StreamingOutput convertHumidityToJSON(final List<Humidity> humidityCollection)
	{
		return new StreamingOutput(){

			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException
			{
				Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
				JsonWriter jsonWriter = new JsonWriter(writer);
				jsonWriter.setIndent("  "); //enable pretty printing
				jsonWriter.beginObject(); //begin top level wrapper object

				jsonWriter.name("data");
				jsonWriter.beginArray(); //begin top level data array
				for(int i=0; i<humidityCollection.size();i++)
				{
					Humidity humidity= new Humidity();
					humidity= humidityCollection.get(i);
					jsonWriter.beginObject(); //begin the humidity object
					jsonWriter.name("type").value("humidity");
					jsonWriter.name("id").value(i);

					jsonWriter.name("attributes");
					jsonWriter.beginObject(); //begin the attributes object
					jsonWriter.name("value").value(humidity.getValue());
					jsonWriter.name("timestamp").value(humidity.getTime());
					jsonWriter.endObject(); //end the attributes object

					jsonWriter.name("relationships");
					jsonWriter.beginObject(); //begin the humidity relationships object

					jsonWriter.endObject(); //end the humidity relationships object

					jsonWriter.endObject(); //end the humidity object
				}
				jsonWriter.endArray(); //end top level data array

				jsonWriter.name("included");
				jsonWriter.beginArray(); //begin top level included array

				jsonWriter.endArray(); //end top level included array

				jsonWriter.endObject(); //end top level wrapper object
				jsonWriter.flush();
				jsonWriter.close();
			}
		};
		
	}
	
	private StreamingOutput convertLedUsageToJSON(final List<Led> ledUsageCollection)
	{
		return new StreamingOutput(){

			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException
			{
				Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
				JsonWriter jsonWriter = new JsonWriter(writer);
				jsonWriter.setIndent("  "); //enable pretty printing
				jsonWriter.beginObject(); //begin top level wrapper object

				jsonWriter.name("data");
				jsonWriter.beginArray(); //begin top level data array
				for(int i=0; i<ledUsageCollection.size();i++)
				{
					Led led= new Led();
					led = ledUsageCollection.get(i);
					jsonWriter.beginObject(); //begin the led object
					jsonWriter.name("type").value("led");
					jsonWriter.name("id").value(i);

					jsonWriter.name("attributes");
					jsonWriter.beginObject(); //begin the attributes object
					jsonWriter.name("value").value(led.getValue());
					jsonWriter.name("timestamp").value(led.getTime());
					jsonWriter.endObject(); //end the attributes object

					jsonWriter.name("relationships");
					jsonWriter.beginObject(); //begin the led relationships object

					jsonWriter.endObject(); //end the led relationships object

					jsonWriter.endObject(); //end the led object
				}
				jsonWriter.endArray(); //end top level data array

				jsonWriter.name("included");
				jsonWriter.beginArray(); //begin top level included array

				jsonWriter.endArray(); //end top level included array

				jsonWriter.endObject(); //end top level wrapper object
				jsonWriter.flush();
				jsonWriter.close();
			}
		};
		
	}
	
	private StreamingOutput convertUserToJSON(final List<User> usersCollection, boolean oneRecord)
	{
		return new StreamingOutput(){

			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException
			{
				Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
				JsonWriter jsonWriter = new JsonWriter(writer);
				jsonWriter.setIndent("  "); //enable pretty printing
				jsonWriter.beginObject(); //begin top level wrapper object

				jsonWriter.name("data");				
				if(!oneRecord){
					jsonWriter.beginArray(); //begin data array
				}
				for(int i=0; i<usersCollection.size();i++)
				{
					User user= new User();
					user = usersCollection.get(i);
					jsonWriter.beginObject(); //begin the user object
					jsonWriter.name("type").value("user");
					jsonWriter.name("id").value(user.getId());

					jsonWriter.name("attributes");
					jsonWriter.beginObject(); //begin the attributes object
					jsonWriter.name("name").value(user.getName());
					jsonWriter.name("username").value(user.getUsername());
					jsonWriter.name("password").value(user.getPassword());
					jsonWriter.name("email").value(user.getEmail());
					jsonWriter.name("isadmin").value(user.isAdmin());
					jsonWriter.name("tempthreshold").value(user.getTemp_threshold());
					jsonWriter.name("humidthreshold").value(user.getHumid_threshold());
					jsonWriter.name("ledthreshold").value(user.getLed_threshold());
					jsonWriter.name("city").value(user.getCity());
					jsonWriter.name("address").value(user.getAddress());
					jsonWriter.name("camera").value(user.getCamera());
					jsonWriter.endObject(); //end the attributes object

					jsonWriter.name("relationships");
					jsonWriter.beginObject(); //begin the user relationships object

					jsonWriter.name("house");
					jsonWriter.beginObject(); //begin the house relationship object
					jsonWriter.name("data");
					jsonWriter.beginArray(); //begin the house relationship data array

					jsonWriter.beginObject(); //begin wrapper object
					jsonWriter.name("type").value("room");
					jsonWriter.name("id").value(user.getMainRoom().getId());
					jsonWriter.endObject(); //end wrapper object
						
					jsonWriter.endArray(); //end the house relationship data
					jsonWriter.endObject(); //end the house relationship object
					
					jsonWriter.name("rooms");
					jsonWriter.beginObject(); //begin the rooms relationship object
					jsonWriter.name("data");
					jsonWriter.beginArray(); //begin the rooms relationship data array
					for(Room room : user.getRooms())
					{
						jsonWriter.beginObject(); //begin wrapper object
						jsonWriter.name("type").value("room");
						jsonWriter.name("id").value(room.getId());
						jsonWriter.endObject(); //end wrapper object
					}
					jsonWriter.endArray(); //end the rooms relationship data
					jsonWriter.endObject(); //end the rooms relationship object
					
					jsonWriter.endObject(); //end the user relationships object

					jsonWriter.endObject(); //end the user object
				}
				if(!oneRecord){
					jsonWriter.endArray(); //end data array
				}

				jsonWriter.name("included");
				jsonWriter.beginArray(); //begin top level included array

				for(int i=0; i<usersCollection.size();i++)
				{
					User user= new User();
					user = usersCollection.get(i);
					
					if(user.getMainRoom() != null)
					{
						jsonWriter.beginObject(); //begin wrapper object
						jsonWriter.name("type").value("room");
						jsonWriter.name("id").value(user.getMainRoom().getId());
						jsonWriter.name("attributes");
						jsonWriter.beginObject(); //begin attributes object
						jsonWriter.name("name").value(user.getMainRoom().getRoomName());
						jsonWriter.endObject(); //end attributes object
						jsonWriter.endObject(); //end wrapper object
					}
					
					for(Room room : user.getRooms())
					{
						if(room != null)
						{
							jsonWriter.beginObject(); //begin wrapper object
							jsonWriter.name("type").value("room");
							jsonWriter.name("id").value(room.getId());
							jsonWriter.name("attributes");
							jsonWriter.beginObject(); //begin attributes object
							jsonWriter.name("name").value(room.getRoomName());
							jsonWriter.endObject(); //end attributes object
							jsonWriter.endObject(); //end wrapper object
						}
					}
				}
				
				jsonWriter.endArray(); //end top level included array

				jsonWriter.endObject(); //end top level wrapper object
				jsonWriter.flush();
				jsonWriter.close();
			}
		};
		
	}
	
}
