package rest.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rest.vertx.Annotations.*;

public class RestVertx {
		
	public static <T> void register(Vertx _v, Router _r, T _toInvoke)
	{
		@SuppressWarnings("unchecked")
		Class<T> sub = (Class<T>) _toInvoke.getClass();
				
		Router mainRouter = _r;
		
		Router subRouter = Router.router(_v);
		
		String basePath = getBasePathValue(sub);
		
		Object toInvoke = _toInvoke;
		
		for (Method m : sub.getMethods())
		{			
			boolean ignore = getIgnore(m);
			
			// If ignore set, skip this method right away
			if (ignore)
				continue;
			
			String path = getPath(m);
			
			// If path isn't set, skip this method right away
			if (path == null)
				continue;
			
			String method = getMethod(m);
			
			HashMap<Integer, Parameter> paramOrder = new HashMap<Integer, Parameter>();
						
			loadOrder(paramOrder, m);	
			
			HashMap<Integer, Class<?>> paramTypes = new HashMap<Integer, Class<?>>();
			
			loadParamTypes(paramTypes, m);
			
			if (method == null)
			{
				// method is not specified, check if the name of the actual method is an http method
				method = parseMethodName(m);				
			}
			
			// Assumption: path and/or base path and the http method has been set (method defaults to GET)
			
			String resultType = getResultType(m);
								
			ArrayList<String> pathParamList = new ArrayList<String>();
			
			// If the path was something/:id, then 'id' would be added to the pathParamList
			setArgumentNameIndex(pathParamList, path);
			
			getRouteMethod(method, path, subRouter).handler(rc -> {
												
				// Store the argument values in a map since they're not guaranteed to be in order
				HashMap<String,Object> argValues = new HashMap<String, Object>();
				ArrayList<Object> argValueList = new ArrayList<Object>();
				
				if (!pathParamList.isEmpty())
				{
					Iterator<String> iter = pathParamList.listIterator();
					
					while (iter.hasNext())
					{
						String key = iter.next();
						
						Object val = rc.request().getParam(key);
						
						argValues.put(key, val);
						
						argValueList.add(val);
					}
				}
				else {
					// There weren't any path variables set
					
					// Assumption:
					// Therefore, there shouldn't be any arguments passed to the handling method
				}
				
				// Places the path variable/arguments in order specified by the parameter
				Object[] arguments = buildArgs(paramOrder, paramTypes, argValues, argValueList);
				
				try {
					
					if (arguments.length != pathParamList.size())
					{
						say("Error, please check @Path annotation and ensure it's set and path variable count equals method parameter count");
						say("Arguments length = " + arguments.length);
						say("pathParamList size = " + pathParamList.size());
						
						rc.response().end();
					}
					
					Object toret = m.invoke(toInvoke, arguments);
					
					// Handle CORS stuff here (only if set individually on the method via annotation)
					String[] _corsAllowedIPs = getCORS(m);
					
					if (_corsAllowedIPs != null && _corsAllowedIPs.length > 0)
					{
						CORS.allow(rc, _corsAllowedIPs);
					}								
					
					// Initially, we will tell it what type of thing is being returned to avoid performance issues while guessing
					if (resultType != null)					
					{
						
						if (resultType.equals("file") && (toret instanceof String))
						{
							// Send the file
							rc.response().sendFile((String) toret).end();
						}
						else if (resultType.equals("json") && (toret instanceof String))
						{
							// Set the header for json content
							rc.response().putHeader("content-type", "application/json; charset=utf-8");
							
							rc.response().end((String) toret);
						}
					}
					else {						
						rc.response().putHeader("content-type", "text/plain");
						
						try {
							// Primitive will err
							rc.response().end((toret.toString() != null) ? toret.toString() : " ");
						}
						catch (Exception e)
						{
							rc.response().end((toret != null) ? "" + toret : " ");
						}
					}
				}
				catch (Exception e)
				{
					say(e.getMessage());
					e.printStackTrace();
				}
			});
		}
		
		mainRouter.mountSubRouter((basePath == null) ? "/" : basePath, subRouter);
	}
	
	static Object[] buildArgs(HashMap<Integer, Parameter> _order, HashMap<Integer, Class<?>> _paramTypes,
								HashMap<String, Object> _argValues, ArrayList<Object> _argValueList)
	{
		if (_order.isEmpty() && _argValues.isEmpty())
			return new Object[0];
		
		Object[] toret = new Object[_order.size()];
				
		if (_order.get(0).getName().contains("arg0"))
		{
			// Then program wasn't compiled so parameters were saved
			
			// Because the parameters weren't saved, we don't know anything about the parameter(s)
			// We can't autobind or assume we know what the argument type is supposed to be (we could guess, but we could be wrong)
			
			// Resolution:
			// We'll process as if the path variables are in the same order as the parameters			
			// ?  Will this work if the parameters of handling method aren't all strings ?
			_argValueList.toArray(toret);
		}
		else
		{		
			// The program was compiled so parameters were saved (yay!)
			// We'll match the path variable names to the method parameter names, no matter the path variable order
			for (int key = 0; key < _order.size(); key++)
			{
				// Unbox and cast - Initially a String argument
				try {		
					switch (_paramTypes.get(key).getName())
					{					
						case "int":
							toret[key] = Integer.parseInt((String)_argValues.get(_order.get(key).getName()));
							break;
						case "double":
							toret[key] = Double.parseDouble((String)_argValues.get(_order.get(key).getName()));
							break;
						case "long":
							toret[key] = Long.parseLong((String)_argValues.get(_order.get(key).getName()));
							break;
						case "boolean":
							toret[key] = Boolean.parseBoolean((String) _argValues.get(_order.get(key).getName()));
							break;
						case "short":
							toret[key] = Short.parseShort((String) _argValues.get(_order.get(key).getName()));
							break;
						case "byte":
							toret[key] = Byte.parseByte((String) _argValues.get(_order.get(key).getName()));
							break;
						case "float":
							toret[key] = Float.parseFloat((String) _argValues.get(_order.get(key).getName()));
							break;
						case "char":
							toret[key] = ((String) _argValues.get(_order.get(key).getName())).charAt(0);
							break;
						case "java.lang.String":
							toret[key] = (String) _argValues.get(_order.get(key).getName());
							break;
						default:							
							// Treat it as a JSON Stringified/Serialized Object if the arg value is a string at this point,
							// try to deserialize/autobind if it is
							if (_argValues.get(_order.get(key).getName()) instanceof String) {
								
								String jValue = (String) _argValues.get(_order.get(key).getName());
								
								toret[key] = parse(_paramTypes.get(key), _argValues.get(_order.get(key).getName()));
							}
							else {		
								// Array?
								toret[key] = _argValues.get(_order.get(key).getName()).toString();
							}
							break;
					}			
				}
				catch (Exception e)
				{
					say(e.getMessage());
					e.printStackTrace();	
				}
			}
		}
		
		return toret;
	}
	
	static Object parse(Class<?> p, Object o)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		
		Object toret = null;
		
		String s = (String) o;
		
		// Escape if URL Encoded string, which often times contains a % sign
		if (s.contains("%"))
		{
			try {
				s = URLDecoder.decode(s, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		try {
			toret = mapper.readValue((String) s, p);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return toret;
	}
	
	static void loadOrder(HashMap<Integer, Parameter> _map, Method _method)
	{
		Parameter[] params = _method.getParameters();
		
		for (int i = 0; i < params.length; i++)
		{
			_map.put(i, params[i]);
		}
	}
	
	static void loadParamTypes(HashMap<Integer, Class<?>> _map, Method _method)
	{		
		Class<?>[] test = _method.getParameterTypes();
		
		for (int i = 0; i < test.length; i++)
		{
			_map.put(i, test[i]);
		}
	}
	
	static void setArgumentNameIndex(ArrayList<String> _list, String _path)
	{
		if (_path != null)
		{
			String[] split = _path.split("/");
			
			for (String arg : split)
			{
				if (arg.startsWith(":"))
				{
					_list.add(arg.substring(1));
				}
			}
		}
	}
	
	static String parseMethodName(Method _method)
	{
		String name = _method.getName().toLowerCase();
		
		if (name.contains("/"))
		{
			String[] sections = name.split("/");
			
			name = sections[0];
		}
		
		// Check if the method is an exact match of an http method, if it is then we'll treat it as such
		switch(name)
		{
			case "get": 
			case "post":
			case "put":
			case "delete":
				return _method.getName();
			default:
				break;
		}
		
		return null;
	}
	
	static <T> String getBasePathValue(Class<T> _sub)
	{
		String basePath = "";
		
		if (_sub.isAnnotationPresent(rest.vertx.Annotations.Base.class))
			basePath = _sub.getAnnotation(Base.class).value();
		
		// Make sure the forward slash is present
		if (basePath.length() > 0 && !basePath.equals("/"))
			return "/" + basePath;
		else
			return "/";
	}
	
	static boolean getIgnore(Method _method)
	{
		if (_method.isAnnotationPresent(rest.vertx.Annotations.RestIgnore.class))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	static String[] getCORS(Method _method)
	{
		if (_method.isAnnotationPresent(rest.vertx.Annotations.CORS.class))
		{			
			rest.vertx.Annotations.CORS[] cors = _method.getAnnotationsByType(rest.vertx.Annotations.CORS.class);

			String[] ipAndPorts = new String[cors.length];
			
			for (int i = 0; i < cors.length; i++)
			{
				if (cors[i].value() != null && cors[i].value().length() > 0)
				{
					// Only support one
					ipAndPorts[i] = cors[i].value();					
				}
				else
				{
					ipAndPorts[i] = "*";
				}
				
				if (i < cors.length - 1)
				{
					ipAndPorts[i] += ",";
				}
			}
			
			return ipAndPorts;
		}
		else
		{
			return null;
		}
	}
	
	static String getPath(Method _method)
	{
		if (_method.isAnnotationPresent(rest.vertx.Annotations.Path.class))
		{
			String path = _method.getAnnotation(rest.vertx.Annotations.Path.class).value();
			
			// Make sure the forward slash is present
			if (path.length() > 0 && !path.equals("/"))
				return "/" + path;
			
			return path;
		}
		else
		{
			return null;
		}
	}
	
	static String getMethod(Method _method)
	{
		if (_method.isAnnotationPresent(rest.vertx.Annotations.Method.class))
		{
			return _method.getAnnotation(rest.vertx.Annotations.Method.class).value();
		}
		else
		{
			return null;
		}
	}
	
	static String getResultType(Method _method)
	{
		if (_method.isAnnotationPresent(rest.vertx.Annotations.ResultType.class))
		{
			return _method.getAnnotation(rest.vertx.Annotations.ResultType.class).value().toLowerCase();
		}
		else
		{
			return null;
		}
	}
	
	private static final String TYPE_NAME_PREFIX = "class ";
	 
	public static String getClassName(Type type) {
	    if (type==null) {
	        return "";
	    }
	    String className = type.toString();
	    if (className.startsWith(TYPE_NAME_PREFIX)) {
	        className = className.substring(TYPE_NAME_PREFIX.length());
	    }
	    return className;
	}
	 
	public static Class<?> getClass(Type type) 
	            throws ClassNotFoundException {
	    String className = getClassName(type);
	    if (className==null || className.isEmpty()) {
	        return null;
	    }
	    return Class.forName(className);
	}
	
	private static Route getRouteMethod(String mthd, String path, Router subRouter)
	{
		Route toret = null;
		
		switch (mthd.toUpperCase()) {
		
		case "GET":
			if (path != null)
				toret = subRouter.get(path);
			else
				toret = subRouter.get();
			break;
		case "POST":
			if (path != null)
				toret = subRouter.post(path);
			else
				toret = subRouter.post();
			break;
		case "PUT":
			if (path != null)
				toret = subRouter.put(path);
			else
				toret = subRouter.put();
			break;
		case "DELETE":
			if (path != null)
				toret = subRouter.delete(path);
			else
				toret = subRouter.delete();
			break;
		default:
			if (path != null)
				toret = subRouter.get(path);
			else
				toret = subRouter.get();
			break;
		}
		
		return toret;
	}

	private static void say(String arg)
	{
		System.out.println(arg);
	}
}