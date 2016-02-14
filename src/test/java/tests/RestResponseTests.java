package tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import tests.handlers.AnnotationHandler;
import tests.models.Choir;
import utils.Runner;
import utils.TestUtility;

public class RestResponseTests extends AbstractVerticle
{
	  private Router router;
	  
	  final String choirBase = "http://localhost:3030/api/annotations/";
	
	  // Convenience method so you can run it in your IDE, from Vertx examples
	  @BeforeClass
	  public static void main() {
		  
		  Runner.runExample(RestResponseTests.class);
	  }
	  
	  @Test
	  public void testCustomStatus()
	  {
		  Choir testChoir = new Choir();
		  
		  Response result = TestUtility.postGetResponse(choirBase + "nameOfChoirBlocking", testChoir.toJson(false));
		  
		  HttpResponse response = null;
		  
		  try {
			  response = result.returnResponse();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		assertTrue(response.getStatusLine().getStatusCode() == 400);
		
		assertTrue(response.getStatusLine().getReasonPhrase().equals("Choir Name is required!"));  
	  }
	  
	  @Test
	  public void testCustomHeaders()
	  {
		  Choir testChoir = new Choir();
		  		  
		  Response result = TestUtility.postGetResponse(choirBase + "nameOfChoirHeaders", testChoir.toJson(false));
		  
		  HttpResponse response = null;
		  
		  try {
			  response = result.returnResponse();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// Normally would return plain text because the ResultType annotation is missing on the handling method
		assertTrue(response.getHeaders("content-type")[0].getValue().equals("application/json; charset=utf-8"));  
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
