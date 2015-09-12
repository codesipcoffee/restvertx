package rest.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CORS {
	
	static String defaultHeaders = "Origin, X-Requested-With, Content-Type, Accept";
	static String defaultMethods = "GET, POST, OPTIONS, PUT, HEAD, DELETE, CONNECT";
	static String defaultIpAndPorts = "*";
	
	/**
	 * Enable CORS for all IP addresses with default headers and methods
	 * 
	 * @param router - Your Vertx Router
	 */
	public static void allowAll(Router router)
	{
		router.route().handler(rc -> {
	  	    
	  	    rc.response().putHeader("Access-Control-Allow-Headers", defaultHeaders);
			rc.response().putHeader("Access-Control-Allow-Methods", defaultMethods);
			rc.response().putHeader("Access-Control-Allow-Origin", defaultIpAndPorts);
			rc.next();
  	  });
	}
	
	/**
	 * Enable CORS for all IP addresses with custom headers and methods
	 * 
	 * @param router - Your Vertx Router
	 * @param allowedHeaders - Custom Headers
	 * @param allowedMethods - Custom Methods
	 */
	public static void allowAll(Router router, String allowedHeaders, String allowedMethods)
	{
		router.route().handler(rc -> {
	  	    
	  	    rc.response().putHeader("Access-Control-Allow-Headers", allowedHeaders);
			rc.response().putHeader("Access-Control-Allow-Methods", allowedMethods);
			rc.response().putHeader("Access-Control-Allow-Origin", defaultIpAndPorts);
			rc.next();
  	  });
	}
	
	/**
	 * Enable CORS for a specific IP address with default headers and methods
	 * 
	 * @param router - Your Vertx Router
	 * @param ipAndPort - ie: http://localhost:8080
	 */
	public static void allow(Router router, String ipAndPort)
	{
		router.route().handler(rc -> {
	  	    
	  	    rc.response().putHeader("Access-Control-Allow-Headers", defaultHeaders);
			rc.response().putHeader("Access-Control-Allow-Methods", defaultMethods);
			rc.response().putHeader("Access-Control-Allow-Origin", ipAndPort);
			rc.next();
  	  });
	}
	
	/**
	 * Sets CORS on routing context using default headers & methods on specific ip and port (note: does not call next() )
	 * 
	 * @param rc
	 * @param ipAndPort
	 */
	public static void allow(RoutingContext rc, String ipAndPort)
	{
		if (ipAndPort == null || ipAndPort.length() == 0)
			return;
		
  	    rc.response().putHeader("Access-Control-Allow-Headers", defaultHeaders);
		rc.response().putHeader("Access-Control-Allow-Methods", defaultMethods);
		rc.response().putHeader("Access-Control-Allow-Origin", ipAndPort);
	}
	
	/**
	 * Sets CORS on routing context using default headers & methods on specific ips and ports (note: does not call next() )
	 * 
	 * @param rc
	 * @param ipAndPort
	 */
	public static void allow(RoutingContext rc, String[] ipAndPorts)
	{
		if (ipAndPorts == null || ipAndPorts.length == 0)
			return;
		
  	    rc.response().putHeader("Access-Control-Allow-Headers", defaultHeaders);
		rc.response().putHeader("Access-Control-Allow-Methods", defaultMethods);
		
		String ips = "";
		
		for (int i = 0; i < ipAndPorts.length; i++)
		{
			ips += ipAndPorts[i];
			
			if (i < ipAndPorts.length - 1)
			{
				ips += ",";
			}
		}		
		
		rc.response().putHeader("Access-Control-Allow-Origin", ips);
	}
	
	/**
	 * Enable CORS for a specific IP address with custom headers and methods
	 * 
	 * @param router - Your Vertx Router
	 * @param ipAndPort - ie: http://localhost:8080
	 * @param allowedHeaders - Custom Headers
	 * @param allowedMethods - Custom Methods
	 */
	public static void allow(Router router, String ipAndPort, String allowedHeaders, String allowedMethods)
	{
		router.route().handler(rc -> {
	  	    
	  	    rc.response().putHeader("Access-Control-Allow-Headers", allowedHeaders);
			rc.response().putHeader("Access-Control-Allow-Methods", allowedMethods);
			rc.response().putHeader("Access-Control-Allow-Origin", ipAndPort);
			rc.next();
  	  });
	}
	
	/**
	 * 
	 * @param router - Your Vertx Router
	 * @param ip - The base IP Address to use (we'll attach the ports to this address) - example http://localhost
	 * @param ports - Comma delimited String of port numbers - example 8080, 8000
	 */
	public static void allow(Router router, String ip, String ports)
	{
		String ipAndPorts = attachPorts(ip, ports);
		
		router.route().handler(rc -> {
	  	    
	  	    rc.response().putHeader("Access-Control-Allow-Headers", defaultHeaders);
			rc.response().putHeader("Access-Control-Allow-Methods", defaultMethods);
			rc.response().putHeader("Access-Control-Allow-Origin", ipAndPorts);
			rc.next();
  	  });
	}
	
	/**
	 * Private helper method for attaching multiple ports to a base IP Address
	 * 
	 * @param _ip
	 * @param _ports
	 * @return
	 */
	private static String attachPorts(String _ip, String _ports)
	{
		StringBuilder str = new StringBuilder();
		
		String[] ports = _ports.split(",");
		
		for (int i = 0; i < ports.length; i++)
		{
			str.append(_ip + ":" + ports[i]);
			
			if (i < ports.length - 1)
			{
				str.append(",");
			}
		}
		
		return str.toString();
	}
}