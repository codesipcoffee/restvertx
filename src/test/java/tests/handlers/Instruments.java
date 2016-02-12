package tests.handlers;

import rest.vertx.RestVertx;
import rest.vertx.Annotations.Base;
import rest.vertx.Annotations.Method;
import rest.vertx.Annotations.Path;
import rest.vertx.Annotations.ResultType;
import rest.vertx.models.RestResponse;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

@Base("api/instruments")
public class Instruments {
	
	public Instruments(Vertx _vertx, Router router)
	{
		RestVertx.register(_vertx, router, this);
	}
	
	@Method("Get")
	@Path("name/:id")
	public RestResponse Get(String id)
	{
		return new RestResponse(id + " is a an instrument used by the choir");
	}
	
	@Method("Post")
	@Path("names")
	@ResultType("Json")
	public RestResponse PostInstrument(String name)
	{
		System.out.println("Point A Instruments");
		
		JsonObject jObject = new JsonObject();
		
		String instrumentSentence = "One of the instruments of the choir is a " + name + ".  It's instrumental to the choir's success!";
		
		jObject.put("result", instrumentSentence);
		
		System.out.println("Point B Instruments");
		
		return new RestResponse(jObject.toString());
	}
	
	@Method("Post")
	@Path("nameThem")
	@ResultType("Json")
	public RestResponse PostInstrument(String nameTwo, String nameOne)
	{
		System.out.println("Point A Instruments");
		
		JsonObject jObject = new JsonObject();
		
		String instrumentSentence = "One of the instruments of the choir is a " + nameOne + ".  It's not " + nameTwo + ".  It's instrumental to the choir's success!";
		
		jObject.put("result", instrumentSentence);
		
		System.out.println("Point B Instruments");
		
		return new RestResponse(jObject.toString());
	}
}