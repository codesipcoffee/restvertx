package tests.handlers;

import rest.vertx.RestVertx;
import rest.vertx.Annotations.Base;
import rest.vertx.Annotations.Method;
import rest.vertx.Annotations.Path;
import rest.vertx.Annotations.ResultType;
import rest.vertx.models.RestResponse;
import tests.models.Choir;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

@Base("api/timeTest")
public class TimeTest {
	
	public TimeTest(Vertx _vertx, Router router)
	{
		RestVertx.register(_vertx, router, this);
	}
	
	@Method("Post")
	@Path("names")
	@ResultType("Json")
	public RestResponse PostChoir(Choir choir)
	{		
		return new RestResponse(choir.toJson(false));
	}
}