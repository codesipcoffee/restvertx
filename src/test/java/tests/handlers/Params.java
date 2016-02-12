package tests.handlers;

import rest.vertx.RestVertx;
import rest.vertx.Annotations.Base;
import rest.vertx.Annotations.Method;
import rest.vertx.Annotations.Path;
import rest.vertx.models.RestResponse;
import tests.models.Number;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

@Base("api/params")
public class Params {
	
	public Params(Vertx _vertx, Router router)
	{
		RestVertx.register(_vertx, router, this);
	}
	
	/**
	 * For testing int
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/int/:number")
	public RestResponse GetInt(int number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing short
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/short/:number")
	public RestResponse Get(short number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing char
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/char/:number")
	public RestResponse Get(char number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing String
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/string/:number")
	public RestResponse Get(String number)
	{		
		return new RestResponse(number);
	}
	
	/**
	 * For testing double
	 * @param number
	 * @return
	 */	
	@Method("Get")
	@Path("count/double/:number")
	public RestResponse Get(double number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing long
	 * @param number
	 * @return
	 */	
	@Method("Get")
	@Path("count/long/:number")
	public RestResponse Get(long number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing boolean
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/boolean/:number")
	public RestResponse Get(boolean number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing byte
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/byte/:number")
	public RestResponse Get(byte number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing float
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/float/:number")
	public RestResponse Get(float number)
	{		
		return new RestResponse("" + number);
	}
	
	/**
	 * For testing JSON
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/json/:number")
	public RestResponse Get(Number number)
	{		
		return new RestResponse("" + number.getNumb());
	}
	
	/**
	 * For testing array (int)
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/array/:number")
	public RestResponse Get(int[] number)
	{		
		String toret = "";
			
		for (int i = 0; i < number.length; i++)
		{
			toret += number[i];
			
			if (i < number.length - 1)
			{
				toret += ",";
			}
		}
		
		return new RestResponse(toret);
	}
}