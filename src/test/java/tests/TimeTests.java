package tests;

import static org.junit.Assert.assertNotNull;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.BeforeClass;
import org.junit.Test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import tests.handlers.TimeTest;
import tests.models.Address;
import tests.models.Choir;
import utils.Runner;
import utils.TestUtility;

public class TimeTests extends AbstractVerticle
{
	  private Router router;
	  
	  final String timeTestBase = "http://localhost:3030/api/timeTest/";
	
	  // Convenience method so you can run it in your IDE, from Vertx examples
	  @BeforeClass
	  public static void main() {
		  
		  Runner.runExample(TimeTests.class);
	  }
	  
//	  @Test
	  public void testTimeFor_FiftyThousand_UsingVertxAlone()
	  {
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  StopWatch timer = new StopWatch();
		  
		  String result = null;

		  timer.start();
		  
		  for (int i = 0; i < 50000; i++) {
			  			  
			  result = TestUtility.post(timeTestBase + "namez", testChoir.toJson(false));
			  		  			  
		  }		  

		  timer.stop();
		  
		  assertNotNull(result);
		  
		  say("\n\nTime taken Vert.x alone = " + timer.getTime());
		  say("Time taken (nano) Vert.x alone = " + timer.getNanoTime());
	  }
	  
	  @Test
	  public void testTimeFor_FiftyThousand()
	  {
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  StopWatch timer = new StopWatch();
		  
		  String result = null;

		  timer.start();
		  
		  for (int i = 0; i < 50000; i++) {			  
			  
			  result = TestUtility.post(timeTestBase + "names", testChoir.toJson(false));
			  		  			  
		  }		  

		  timer.stop();
		  
		  assertNotNull(result);
		  
		  say("\n\nTime taken = " + timer.getTime());
		  say("Time taken (nano) = " + timer.getNanoTime());
	  }
	  
	  @Override
	  public void start() throws Exception {
		  
		  HttpServer server = vertx.createHttpServer();
		  
		  router = Router.router(vertx);
		  
		  // So we can use getBodyAsJson() and/or getBodyAsString() in our handling methods
		  router.route().handler(BodyHandler.create());

//		  router.post("/api/timeTest/namez").handler(this::PostChoir);
		  
		  RegisterRoutes();
		  
		  System.out.println("Now listening on port 3030");
		  	  
	  	  server.requestHandler(router::accept).listen(3030);  	  
	  }
	  
	  public void PostChoir(RoutingContext rc)
		{		
		    Choir choir = TestUtility.toChoirFromJson(rc.getBodyAsString());
		    
			rc.response().end(choir.toJson(false));
		}
	  
	  /**
	   * This method simply instantiates all classes extending RestVertx
	   */
	  private void RegisterRoutes()
	  {
		  TimeTest tTest = new TimeTest(vertx, router);
	  }
	  
	  static void say(String args)
	  {
		  System.out.println(args);
	  }
}
