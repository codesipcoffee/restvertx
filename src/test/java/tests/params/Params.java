package tests.params;

import rest.vertx.RestVertx;
import rest.vertx.Annotations.Base;
import rest.vertx.Annotations.Method;
import rest.vertx.Annotations.Path;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import models.Number;

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
	public String GetInt(int number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing short
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/short/:number")
	public String Get(short number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing char
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/char/:number")
	public String Get(char number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing String
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/string/:number")
	public String Get(String number)
	{		
		return number;
	}
	
	/**
	 * For testing double
	 * @param number
	 * @return
	 */	
	@Method("Get")
	@Path("count/double/:number")
	public String Get(double number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing long
	 * @param number
	 * @return
	 */	
	@Method("Get")
	@Path("count/long/:number")
	public String Get(long number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing boolean
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/boolean/:number")
	public String Get(boolean number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing byte
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/byte/:number")
	public String Get(byte number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing float
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/float/:number")
	public String Get(float number)
	{		
		return "" + number;
	}
	
	/**
	 * For testing JSON
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/json/:number")
	public String Get(Number number)
	{		
		return "" + number.getNumb();
	}
	
	/**
	 * For testing array (int)
	 * @param number
	 * @return
	 */
	@Method("Get")
	@Path("count/array/:number")
	public String Get(int[] number)
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
		
		return toret;
	}
}