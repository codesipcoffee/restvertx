package tests.params;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import utils.Runner;
import utils.TestUtility;

import static org.junit.Assert.*;

import org.junit.*;

public class ParamTypeTests extends AbstractVerticle
{
	  private Router router;
	  final String base = "http://localhost:3030/api/params/";
	
	  // Convenience method so you can run it in your IDE, from Vertx examples
	  @BeforeClass
	  public static void setUpClass() {
		  Runner.runExample(ParamTypeTests.class);
	  }
	  
	  @Test
	  public void testInt()
	  {
		  String url = base + "count/int/1";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "1");
	  }
	  
	  @Test
	  public void testShort()
	  {
		  String url = base + "count/short/1";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "1");
	  }
	  
	  @Test
	  public void testByte()
	  {
		  String url = base + "count/byte/1";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "1");
	  }

	  @Test
	  public void testString()
	  {
		  String url = base + "count/string/one";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "one");
	  }

	  @Test
	  public void testChar()
	  {
		  String url = base + "count/char/o";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "o");
	  }
	  
	  @Test
	  public void testLong()
	  {
		  String url = base + "count/long/10";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "10");
	  }
	  
	  @Test
	  public void testFloat()
	  {
		  String url = base + "count/float/10.2f";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "10.2");
	  }
	  
	  @Test
	  public void testJson()
	  {
		  JsonObject obj = new JsonObject();
		  
		  obj.put("numb", "one");
		  
		  String url = base + "count/json/" + obj.toString();
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "one");
	  }

	  @Test
	  public void testDouble()
	  {
		  String url = base + "count/double/10.20d";
		  
		  String result = TestUtility.get(url);
		  
		  assertEquals(result, "10.2");
	  }
	  
	  @Override
	  public void start() throws Exception {
		  
		  HttpServer server = vertx.createHttpServer();
		  
		  server = vertx.createHttpServer();
		  
		  router = Router.router(vertx);
		  
		  RegisterRoutes();
		  	  
	  	  server.requestHandler(router::accept).listen(3030);  
	  }
	  
	  /**
	   * This method simply instantiates all classes extending RestVertx
	   */
	  private void RegisterRoutes()
	  {
		  Params _params = new Params(vertx, router);
	  }	  
	  
	  static void say(String args)
	  {
		  System.out.println(args);
	  }
}