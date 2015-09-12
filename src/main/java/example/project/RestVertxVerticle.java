package example.project;

import utils.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class RestVertxVerticle extends AbstractVerticle
{
	  private Router router;
	
	  // Convenience method so you can run it in your IDE, from Vertx examples
	  public static void main(String[] args) {
		  
		  Runner.runExample(RestVertxVerticle.class);
	  }

	  @Override
	  public void start() throws Exception {
		  
		  HttpServer server = vertx.createHttpServer();
		  
		  server = vertx.createHttpServer();
		  
		  router = Router.router(vertx);
		  
		  RegisterRoutes();
		  	  
	  	  server.requestHandler(router::accept).listen(8080);  	  
	  }
	  
	  /**
	   * This method simply instantiates all classes extending RestVertx
	   */
	  private void RegisterRoutes()
	  {
		  Singers singers = new Singers(vertx, router);
	  }
}
