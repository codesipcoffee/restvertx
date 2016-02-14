package tests.handlers;

import rest.vertx.RestVertx;
import rest.vertx.Annotations.Base;
import rest.vertx.Annotations.Blocking;
import rest.vertx.Annotations.Method;
import rest.vertx.Annotations.Path;
import rest.vertx.Annotations.ResultType;
import rest.vertx.models.RestResponse;
import tests.models.Choir;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

@Base("api/annotations")
public class AnnotationHandler {
	
	public AnnotationHandler(Vertx _vertx, Router router)
	{
		RestVertx.register(_vertx, router, this);
	}
	
	@Method("Get")
	@Path("name/:id")
	public RestResponse Get(String id)
	{
		return new RestResponse(id + " is a wonderful singer in the choir");
	}
	
	@Method("Post")
	@Path("choirNames")
	public RestResponse JSON_MULTIPLE_SIMPLE_PARAMS(String nameOne, String nameTwo, int howMany)
	{
		return new RestResponse("There were " + howMany + " people in the choir.  Their names included " + nameOne + ", and " + nameTwo);
	}
	
	@Method("Post")
	@Path("choirMixedInput")
	public RestResponse JSON_MULTIPLE_SIMPLE_PARAMS(Choir choir, String nameOne, String nameTwo, int howMany)
	{
		return new RestResponse("There were " + howMany + " people in the " + choir.getChoirName() + ".  Their names included " + nameOne + ", and " + nameTwo);
	}
	
	@Blocking("true")
	@Method("Post")
	@Path("nameOfChoirBlocking")
	@ResultType("Json")
	public RestResponse PutBlocking(Choir choir)
	{
		if (choir.getChoirName() == null) {
			return new RestResponse(choir.toJson(false), 400, "Choir Name is required!");
		}
		
		return new RestResponse(choir.toJson(false));
	}
	
	@Method("Post")
	@Path("nameOfChoir")
	@ResultType("Json")
	public RestResponse Put(Choir choir)
	{
		return new RestResponse(choir.toJson(false));
	}
	
	@Method("Post")
	@Path("nameOfChoirHeaders")
	public RestResponse PutWithCustomHeaders(Choir choir)
	{
		HashMap<String, String> customHeaders = new HashMap<String, String>();
		
		// Without the ResultType annotation, this would normally return String
		customHeaders.put("content-type", "application/json; charset=utf-8");
		
		return new RestResponse(choir.toJson(false), 200, customHeaders);
	}
	
	@Method("Post")
	@Path("names")
	@ResultType("Json")
	public RestResponse PostChoir(Choir choir)
	{		
		return new RestResponse(choir.toJson(false));
	}
	
	@Method("Post")
	@Path("namesOfChoirs")
	@ResultType("Json")
	public RestResponse PostChoir(Choir choirOne, Choir choirTwo)
	{		
		HashMap<String, Choir> toret = new HashMap<String, Choir>();
		
		toret.put("choirOne", choirOne);
		
		toret.put("choirTwo", choirTwo);
		
		ObjectMapper mapper = new ObjectMapper();		
		  
		  String jsonString = null;
		  
			try {
				jsonString = mapper.writeValueAsString(toret);
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
			
		return new RestResponse(jsonString);
	}
}