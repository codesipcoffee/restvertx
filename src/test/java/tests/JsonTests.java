package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import tests.handlers.Instruments;
import tests.handlers.Singers;
import tests.models.Address;
import tests.models.Choir;
import utils.Runner;
import utils.TestUtility;

public class JsonTests extends AbstractVerticle
{
	  private Router router;
	  
	  final String choirBase = "http://localhost:3030/api/singers/";
	  final String instrumentsBase = "http://localhost:3030/api/instruments/";
	
	  // Convenience method so you can run it in your IDE, from Vertx examples
	  @BeforeClass
	  public static void main() {
		  
		  Runner.runExample(JsonTests.class);
	  }
	  
	  @Test
	  public void testJsonParam()
	  {
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  String testChoirJSONURLEncoded = null;
		  
		  try {
			  testChoirJSONURLEncoded = URLEncoder.encode(testChoir.toJson(false), "utf-8");
		  } catch (UnsupportedEncodingException e) {
			  e.printStackTrace();
		  }
		
		  assertNotNull(testChoirJSONURLEncoded);
		  
		  String result = TestUtility.get(choirBase + "nameOfChoir/" + testChoirJSONURLEncoded);
		  
		  Choir resultChoir = TestUtility.toChoirFromJson(result);
		  
		  assertEquals(testChoir.getChoirName(), resultChoir.getChoirName());
		  
		  assertEquals(testChoir.getAddress().getCity(), resultChoir.getAddress().getCity());

		  assertEquals(testChoir.getAddress().getState(), resultChoir.getAddress().getState());
		  
		  assertEquals(testChoir.getAddress().getStreet(), null);
	  }
	  
	  @Test
	  public void testJsonBody()
	  {
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  String result = TestUtility.post(choirBase + "names", testChoir.toJson(false));
		  
		  Choir resultChoir = TestUtility.toChoirFromJson(result);
		  
		  assertEquals(testChoir.getChoirName(), resultChoir.getChoirName());
		  
		  assertEquals(testChoir.getAddress().getCity(), resultChoir.getAddress().getCity());

		  assertEquals(testChoir.getAddress().getState(), resultChoir.getAddress().getState());
		  
		  assertNull(testChoir.getAddress().getStreet());
	  }
	  
	  @Test
	  public void testJsonBody_TwoParams()
	  {
		  HashMap<String, Choir> choirs = new HashMap<String, Choir>();
		  HashMap<String, Choir> requestResult = new HashMap<String, Choir>();
		  
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  Choir testChoirTwo = new Choir();
		  
		  Address choirTwoAddress = new Address();
		  
		  choirTwoAddress.setCity("Windsor Heights");
		  
		  choirTwoAddress.setState("IA");

		  testChoirTwo.setChoirName("Heartland Youth Choir");
		  
		  testChoirTwo.setAddress(choirAddress);
		  
		  choirs.put("choirOne", testChoir);
		  
		  choirs.put("choirTwo", testChoirTwo);
		  
		  ObjectMapper mapper = new ObjectMapper();		
		  
		  String jsonString = null;
		  
			try {
				jsonString = mapper.writeValueAsString(choirs);
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
		
		  String result = TestUtility.post(choirBase + "namesOfChoirs", jsonString);
		  
		  assertNotNull(result);
		  
		  TypeFactory typeFactory = mapper.getTypeFactory();
		  MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Choir.class);
		  
		  try {
			  requestResult = mapper.readValue(result, mapType);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		  assertNotNull(requestResult);
		  		  
		  Choir resultChoirOne = requestResult.get("choirOne");
		  
		  Choir resultChoirTwo = requestResult.get("choirTwo");
		  
		  assertEquals(testChoir.getChoirName(), resultChoirOne.getChoirName());
		  
		  assertEquals(testChoir.getAddress().getCity(), resultChoirOne.getAddress().getCity());

		  assertEquals(testChoir.getAddress().getState(), resultChoirOne.getAddress().getState());
		  
		  assertNull(testChoir.getAddress().getStreet());
		  
		  assertEquals(testChoirTwo.getChoirName(), resultChoirTwo.getChoirName());
		  
		  assertEquals(testChoirTwo.getAddress().getCity(), resultChoirTwo.getAddress().getCity());

		  assertEquals(testChoirTwo.getAddress().getState(), resultChoirTwo.getAddress().getState());
		  
		  assertNull(testChoirTwo.getAddress().getStreet());
	  }
	  
	  @Test
	  public void testJsonBody_MultipleParams_SimpleTypes()
	  {
		  HashMap<String, Object> toSend = new HashMap<String, Object>();
		  
		  String nameOne = "Alice";
		  
		  String nameTwo = "Anne";
		  
		  int howMany = 3;
		  
		  toSend.put("nameOne", nameOne);
		  
		  toSend.put("nameTwo", nameTwo);
		  
		  toSend.put("howMany", howMany);		  
		  
		  ObjectMapper mapper = new ObjectMapper();		
		  
		  String jsonString = null;
		  
			try {
				jsonString = mapper.writeValueAsString(toSend);
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
		
		  String result = TestUtility.post(choirBase + "choirNames", jsonString);
		  
		  String expectedResult = "There were " + howMany + " people in the choir.  Their names included " + nameOne + ", and " + nameTwo;
		  
		  assertEquals(result, expectedResult);
	  }
	  
	  @Test
	  public void testJsonBody_MultipleParams_SimpleAndComplexTypes()
	  {
		  HashMap<String, Object> toSend = new HashMap<String, Object>();
		  
		  String nameOne = "Alice";
		  
		  String nameTwo = "Anne";
		  
		  int howMany = 3;
		  
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  toSend.put("nameOne", nameOne);
		  
		  toSend.put("nameTwo", nameTwo);
		  
		  toSend.put("howMany", howMany);		
		  
		  toSend.put("choir", testChoir);
		  
		  ObjectMapper mapper = new ObjectMapper();		
		  
		  String jsonString = null;
		  
			try {
				jsonString = mapper.writeValueAsString(toSend);
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
		
		  String result = TestUtility.post(choirBase + "choirMixedInput", jsonString);
		  
		  String expectedResult = "There were " + howMany + " people in the " + testChoir.getChoirName() +".  Their names included " + nameOne + ", and " + nameTwo;
		  
		  assertEquals(result, expectedResult);
	  }
	  
	  @Override
	  public void start() throws Exception {
		  
		  HttpServer server = vertx.createHttpServer();
		  
		  router = Router.router(vertx);
		  
		  // So we can use getBodyAsJson() and/or getBodyAsString() in our handling methods
		  router.route().handler(BodyHandler.create());
		  
		  RegisterRoutes();
		  
		  System.out.println("Now listening on port 3030");
		  	  
	  	  server.requestHandler(router::accept).listen(3030);  	  
	  }
	  
	  /**
	   * This method simply instantiates all classes extending RestVertx
	   */
	  private void RegisterRoutes()
	  {
		  Singers singers = new Singers(vertx, router);
		  Instruments instruments = new Instruments(vertx, router);
	  }
	  
	  static void say(String args)
	  {
		  System.out.println(args);
	  }
}
