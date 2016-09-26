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
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
//import com.smarthome.dao.SmartHomeDAO;
import com.smarthome.model.Temperature;

@Path("/api/smarthome")
public class SmartHomeRESTService {
	
	private final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
//	private SmartHomeDAO smarthomeDAO;
//
//	public SmartHomeRESTService(SmartHomeDAO smarthomeDAO)
//	{
//		this.smarthomeDAO = smarthomeDAO;
//	}
	
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
	@Path("/json")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTemperature()
	{
		try {
			List<Temperature> temperatureCollection = new ArrayList<Temperature>();
			temperatureCollection.add(new Temperature("23-11-16","45"));
			return Response.ok().entity(convertToJSON(temperatureCollection)).build();
		} catch(RuntimeException e){
			e.printStackTrace();
			return Response.status(SC_INTERNAL_SERVER_ERROR).entity(GSON.toJson(e.getStackTrace())).build();
		}
	}
	
	private StreamingOutput convertToJSON(final List<Temperature> temperatureCollection)
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
					jsonWriter.beginObject(); //begin the epic object
					jsonWriter.name("type").value("snapshot");
					jsonWriter.name("id").value(i);

					jsonWriter.name("attributes");
					jsonWriter.beginObject(); //begin the attributes object
					jsonWriter.name("value").value(temperature.getValue());
					jsonWriter.name("timestamp").value(temperature.getTime());
					jsonWriter.endObject(); //end the attributes object

					jsonWriter.name("relationships");
					jsonWriter.beginObject(); //begin the epic relationships object

					jsonWriter.endObject(); //end the epic relationships object

					jsonWriter.endObject(); //end the epic object
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
}
