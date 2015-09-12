package example.project;

import rest.vertx.RestVertx;
import rest.vertx.Annotations.Base;
import rest.vertx.Annotations.Method;
import rest.vertx.Annotations.Path;
import rest.vertx.Annotations.ResultType;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

@Base("api/singers")
public class Singers {
	
	public Singers(Vertx _vertx, Router router)
	{
		RestVertx.register(_vertx, router, this);
	}
	
	@Method("Get")
	@Path("name/:id")
	public String Get(String id)
	{
		return id + " is a wonderful singer in the choir";
	}
	
	@Method("Get")
	@Path("names/:choir")
	@ResultType("Json")
	public String Put(Choir choir)
	{
		JsonObject jObject = new JsonObject();
		
		String choirSentence = choir.getChoirName() + " is the name of the choir";
		
		jObject.put("result", choirSentence);
		
		return jObject.toString();
	}
}