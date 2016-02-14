package tests;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import tests.handlers.AnnotationHandler;
import tests.models.Address;
import tests.models.Choir;
import utils.Runner;
import utils.TestUtility;

public class Annotations extends AbstractVerticle
{	  
	  private Router router;
	  
	  final String choirBase = "http://localhost:3030/api/annotations/";
	
	  // Convenience method so you can run it in your IDE, from Vertx examples
	  @BeforeClass
	  public static void main() {
		  
		  Runner.runExample(Annotations.class);
	  }
	  
	  @Test
	  public void testNonBlockingAnnotation()
	  {
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  String result = TestUtility.post(choirBase + "nameOfChoir", testChoir.toJson(false));

		  Choir resultChoir = TestUtility.toChoirFromJson(result);
		  
		  assertEquals(testChoir.getChoirName(), resultChoir.getChoirName());
		  
		  assertEquals(testChoir.getAddress().getCity(), resultChoir.getAddress().getCity());

		  assertEquals(testChoir.getAddress().getState(), resultChoir.getAddress().getState());  
	  }
	  
	  @Test
	  public void testDefaultBlocking()
	  {
		  Choir testChoir = new Choir();
		  
		  Address choirAddress = new Address();
		  
		  choirAddress.setCity("Omaha");
		  
		  choirAddress.setState("NE");

		  testChoir.setChoirName("Omaha Children's Choir");
		  
		  testChoir.setAddress(choirAddress);
		  
		  String result = TestUtility.post(choirBase + "nameOfChoirBlocking", testChoir.toJson(false));

		  Choir resultChoir = TestUtility.toChoirFromJson(result);
		  
		  assertEquals(testChoir.getChoirName(), resultChoir.getChoirName());
		  
		  assertEquals(testChoir.getAddress().getCity(), resultChoir.getAddress().getCity());

		  assertEquals(testChoir.getAddress().getState(), resultChoir.getAddress().getState());
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
		  AnnotationHandler annotations = new AnnotationHandler(vertx, router);
	  }
	  
	  static void say(String args)
	  {
		  System.out.println(args);
	  }
}
