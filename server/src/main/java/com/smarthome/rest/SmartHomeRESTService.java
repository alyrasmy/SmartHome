package com.smarthome.rest;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.smarthome.dao.SmartHomeDAO;
import com.smarthome.model.Humidity;
import com.smarthome.model.Led;
import com.smarthome.model.Room;
//import com.smarthome.dao.SmartHomeDAO;
import com.smarthome.model.Temperature;
import com.smarthome.model.User;

@Path("/api/smarthome")
public class SmartHomeRESTService {
	
	private final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	private SmartHomeDAO smarthomeDAO;

	public SmartHomeRESTService(SmartHomeDAO smarthomeDAO)
	{
		this.smarthomeDAO = smarthomeDAO;
	}
	
	@GET
	@Produces("text/html")
	public Response getStartingPage()
	{
		String output = "<h1>Hello World!<h1>" +
				"<p>RESTful Service is running ... <br>Ping @ " + new Date().toString() + "</p<br>";
		return Response.status(200).entity(output).build();
	}
	
	//http response for a list of temperature readings
	@GET
	@Path("/temperature")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTemperature(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate)
	{
		try {
			if(startDate != null && endDate != null) // get all temperature record in the database between these 2 dates
			{
				List<Temperature> temperatureCollection = new ArrayList<Temperature>();
				temperatureCollection.add(new Temperature("23-11-16","45"));
				//temperatureCollection = (List<Temperature>) smarthomeDAO.getTemperature(startDate,endDate);
				return Response.ok().entity(convertTemperatureToJSON(temperatureCollection)).build();
			} else { // get all temperature record in the database
				List<Temperature> temperatureCollection = new ArrayList<Temperature>();
				temperatureCollection.add(new Temperature("23-11-16","45"));
				//temperatureCollection = (List<Temperature>) smarthomeDAO.getAllTemperature();
				return Response.ok().entity(convertTemperatureToJSON(temperatureCollection)).build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR).entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	//http response for a list of humidity readings
	@GET
	@Path("/humidity")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getHumidity(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate)
	{
		try {
			if(startDate != null && endDate != null) // get all humidity record in the database between these 2 dates
			{
				List<Humidity> humidityCollection = new ArrayList<Humidity>();
				humidityCollection.add(new Humidity("23-11-16","45"));
				//humidityCollection = (List<Humidity>) smarthomeDAO.getHumidity(startDate,endDate);
				return Response.ok().entity(convertHumidityToJSON(humidityCollection)).build();
			} else { // get all humidity record in the database
				List<Humidity> humidityCollection = new ArrayList<Humidity>();
				humidityCollection.add(new Humidity("23-11-16","45"));
				//humidityCollection = (List<Humidity>) smarthomeDAO.getAllHumidity();
				return Response.ok().entity(convertHumidityToJSON(humidityCollection)).build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR).entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	//http response for a list of led daily usage readings
	@GET
	@Path("/led")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getLedUsage(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate)
	{
		try {
			if(startDate != null && endDate != null) // get all led usage record in the database between these 2 dates
			{
				List<Led> ledUsageCollection = new ArrayList<Led>();
				ledUsageCollection.add(new Led("23-11-16","45"));
				//ledUsageCollection = (List<Led>) smarthomeDAO.getLedUsage(startDate,endDate);
				return Response.ok().entity(convertLedUsageToJSON(ledUsageCollection)).build();
			} else { // get all led usage record in the database
				List<Led> ledUsageCollection = new ArrayList<Led>();
				ledUsageCollection.add(new Led("23-11-16","45"));
				//ledUsageCollection = (List<Led>) smarthomeDAO.getAllLedUsage();
				return Response.ok().entity(convertLedUsageToJSON(ledUsageCollection)).build();
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR).entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	//http response for a user
	@GET
	@Path("/user/{user_id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getUser(@PathParam("user_id") String userId)
	{
		try {
			List<User> usersCollection = new ArrayList<User>();
			//User user = new User(sdsdsdsd);
			User user = smarthomeDAO.getUser(userId);
			usersCollection.add(user);
			return Response.ok().entity(convertUserToJSON(usersCollection, true)).build();
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR).entity(GSON.toJson(e.getStackTrace())).build();
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
	
	private StreamingOutput convertUserToJSON(final List<User> usersCollection, boolean hasMany)
	{
		final boolean oneRecord = hasMany;
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
					jsonWriter.endObject(); //end the attributes object

					jsonWriter.name("relationships");
					jsonWriter.beginObject(); //begin the user relationships object

					jsonWriter.name("house");
					jsonWriter.beginObject(); //begin the house relationship object
					jsonWriter.name("data");
					jsonWriter.beginArray(); //begin the house relationship data array

					jsonWriter.beginObject(); //begin wrapper object
					jsonWriter.name("type").value("room");
					jsonWriter.name("id").value(user.getHouse().getId());
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
					
					if(user.getHouse() != null)
					{
						jsonWriter.beginObject(); //begin wrapper object
						jsonWriter.name("type").value("room");
						jsonWriter.name("id").value(user.getHouse().getId());
						jsonWriter.name("attributes");
						jsonWriter.beginObject(); //begin attributes object
						jsonWriter.name("name").value(user.getHouse().getRoomName());
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
