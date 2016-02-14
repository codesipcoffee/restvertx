package rest.vertx;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import rest.vertx.Annotations.Base;
import rest.vertx.Annotations.NoParam;
import rest.vertx.Annotations.RestIgnore;
import rest.vertx.models.Blocking;
import rest.vertx.models.RequestInfo;
import rest.vertx.models.RestResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RestVertx {

    public static <T> void register(Vertx _v, Router _r, T _toInvoke) {
        @SuppressWarnings("unchecked")
        Class<T> sub = (Class<T>) _toInvoke.getClass();

        Router mainRouter = _r;

        Router subRouter = Router.router(_v);

        // So we can use getBodyAsJson() and/or getBodyAsString() in our handling methods
        subRouter.route().handler(BodyHandler.create());

        String basePath = getBasePathValue(sub);

        Object toInvoke = _toInvoke;

        for (Method m : sub.getMethods()) {
            
        	boolean ignore = getIgnore(m);

            // If ignore set, skip this method right away
            if (ignore)
                continue;

            String path = getPath(m);

            // If path isn't set, skip this method right away
            if (path == null)
                continue;

            String httpMethod = getHttpMethod(m);

            HashMap<Integer, Parameter> paramOrder = new HashMap<Integer, Parameter>();

            loadOrder(paramOrder, m);

            HashMap<Integer, Class<?>> paramTypes = new HashMap<Integer, Class<?>>();

            loadParamTypes(paramTypes, m);

            Parameter[] params = m.getParameters();

            ArrayList<String> paramNames = new ArrayList<String>();

            for (int i = 0; i < params.length; i++) {
                paramNames.add(m.getParameters()[i].getName());
            }

            if (httpMethod == null) {
                // Assumption: if http method is not specified in annotation, http method might be embedded in the actual method name
                // check if the name of the actual method is an http method
                httpMethod = parseMethodName(m);
            }

            // Assumption: path and/or base path and the http method has been set (method defaults to GET)

            String resultType = getResultType(m);
            
            ArrayList<String> pathParamList = new ArrayList<String>();
            
            RequestInfo requestInfo = new RequestInfo(getBlocking(m));
            
            // If the path was something/:id, then 'id' would be added to the pathParamList
            setArgumentNameIndex(pathParamList, path);

            getRouteMethod(httpMethod, path, subRouter).handler(rc -> {
                
                // Specifies whether we should parse the request body for the specified variables
                boolean parseRequestBody = false;

                // Store the argument values in a map since they're not guaranteed to be in order
                HashMap<String, Object> argValues = new HashMap<String, Object>();
                ArrayList<Object> argValueList = new ArrayList<Object>();
                
                // If there is more than one parameter for this method, parse the arguments sent via request
                if (paramOrder.size() > 0) {
                	
	                if (!pathParamList.isEmpty()) {
	                    // Assumption: the request body is not being used, argument(s) sent in path params
	                    Iterator<String> iter = pathParamList.listIterator();
	
	                    while (iter.hasNext()) {
	                        String key = iter.next();
	
	                        Object val = rc.request().getParam(key);
	
	                        argValues.put(key, val);
	
	                        argValueList.add(val);
	                    }
	                } else if (paramOrder.size() == 1) {
	                	
	                    JsonObject jObject = rc.getBodyAsJson();
	                    
	                    // Do not trust the client. He may not have sent the json, or it could be malformed.
	                    // In this case, send an empty response with status code = 400 (bad request)
	                    if (jObject == null) {
	                        rc.response().setStatusCode(400).end();
	                        return;
	                    }
	                    
	                    if (jObject.size() == 1 && jObject.containsKey(paramNames.get(0))) {
	                        //Object val = toString(jObject.getValue(paramNames.get(0)));
	
	                        argValues.put(paramNames.get(0), jObject.getValue(paramNames.get(0)));
	
	                        argValueList.add(jObject.getValue(paramNames.get(0)));
	                    } else {
	                    	// One method parameter is set
	                        // Assumption: User sent all objects in one serialized Json string
	                        String val = rc.getBodyAsString();
	
	                        argValues.put("__serialized", val);
	
	                        argValueList.add(val);
	
	                        parseRequestBody = true;
	                    }
	                } else if (paramOrder.size() > 1) {
	                    // There weren't any path variables set and there is more than one method parameter,
	                    // therefore, we'll try parsing the request body and deserialize/reserialize
	                    // Assumption: Request body must be in serialized Json format with each argument variable name set as in the arguments
	
	                    // Deserialize body into Json Object
	                    JsonObject jObject = rc.getBodyAsJson();
	                    
	                    // Do not trust the client. He may not have sent the json, or it could be malformed.
	                    // In this case, send an empty response with status code = 400 (bad request)
	                    if (jObject == null) {
	                        rc.response().setStatusCode(400).end();
	                        return;
	                    }
	
	                    Iterator<HashMap.Entry<String, Object>> iter = jObject.iterator();
	
	                    while (iter.hasNext()) {
	                        Entry<String, Object> current = iter.next();
	
	                        String key = current.getKey();
	
	                        Object val = current.getValue().toString();
	
	                        argValues.put(key, val);
	
	                        argValueList.add(val);
	                    }
	
	                    parseRequestBody = true;
	                }
                }

                // Places the path variable/arguments in order specified by the parameter
                Object[] arguments = buildArgs(paramOrder, paramTypes, argValues, argValueList, parseRequestBody);

                final Object[] arguments_f = arguments;

                // This can be pretty long. Since we have a grip on the Vertx object we can use it to create a blocking function
                	// Only use blocking if blocking is indicated in the handling method's blocking annotation
                if (requestInfo.getBlocking().isBlocking()) {
	                _v.executeBlocking(
	                        objectFuture -> {
	                        	invokeMethod(m, toInvoke, arguments_f, objectFuture);
	                        },
	                        requestInfo.getBlocking().isSerial(),
	                        objectAsyncResult -> {
	                            Object toret = objectAsyncResult.result();
	                            
	                            invokeResponse(rc, m, toret, resultType);
	                        }
                	);
                } else {
                	// Non-blocking
                	Object toret = invokeMethod(m, toInvoke, arguments_f, null);
                    
                    invokeResponse(rc, m, toret, resultType);               	
                }
            });
        }

        mainRouter.mountSubRouter((basePath == null) ? "/" : basePath, subRouter);
    }
    
    static void invokeResponse(RoutingContext rc, Method m, Object toret, String resultType) {
    	// Handle CORS stuff here (only if set individually on the method via annotation)
        String[] _corsAllowedIPs = getCORS(m);
        
        if (_corsAllowedIPs != null && _corsAllowedIPs.length > 0) {
            CORS.allow(rc, _corsAllowedIPs);
        }
        
        // Combine errors
        if (toret == null || !(toret instanceof RestResponse)){

        	// Handling function didn't return a RestResponse object, return an appropriate error message
            rc.response().setStatusCode(500).setStatusMessage("Error: Function didn't return a RestResponse object").end();
            
            // We don't "have" to throw an exception, a 500 may suffice and client may log error and message in their logs
//                              new RuntimeException("The return type of a REST function must be a RestResponse");
        }else {
        	
        	// Put the custom headers first, if any
        	putHeaders(((RestResponse) toret).getHeaders(), rc);
        	
        	// PUt the custom status code/message, if any
        	putStatus((RestResponse) toret, rc);
        	
            if (resultType != null) {

                if (resultType.equals("file")) {
                	
                    // Send the file
                    rc.response().sendFile(((RestResponse) toret).getBody()).end();
                } else if (resultType.equals("json")) {

                    // Set the header for json content - will override any custom header for content-type
                    rc.response().putHeader("content-type", "application/json; charset=utf-8");
                    
                    rc.response().end(((RestResponse) toret).getBody());
                }
            } else {
            	
            	// New assumption: If result type not set, we're sending back whatever the custom headers say to return
            	// or else if no custom headers for content-type, we'll return text
            	
            	if (!rc.response().headers().contains("content-type")) {
            		rc.response().putHeader("content-type", "text/plain");
            	}
            	
            	String result = ((((RestResponse) toret).getBody() != null) ? ((RestResponse) toret).getBody() : " ");
            	
                rc.response().end(result);
            }
        }
    }
    
    static Object invokeMethod(Method m, Object toInvoke, Object[] arguments_f, Future<Object> objectFuture)
    {
    	try {
            Object toret = m.invoke(toInvoke, arguments_f);
            
            if (objectFuture != null) {
            	objectFuture.complete(toret);
            }
            else {
            	return toret;
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            
            if (objectFuture != null) {
            	objectFuture.complete(null);
            }
        }
    	
    	// If error & non-blocking
    	return null;
    }

    static Object[] buildArgs(HashMap<Integer, Parameter> _order, HashMap<Integer, Class<?>> _paramTypes,
                              HashMap<String, Object> _argValues, ArrayList<Object> _argValueList,
                              boolean parseRequestBody) {
        if (_order.isEmpty() && _argValues.isEmpty())
            return new Object[0];

        Object[] toret = new Object[_order.size()];

        if (_order.get(0).getName().contains("arg0")) {
            // Then program wasn't compiled so parameters were saved or user named first parameter arg0
            // Because the parameters weren't saved, we don't know anything about the parameter(s)
            // We can't autobind or assume we know what the argument type is supposed to be (we could guess, but we could be wrong)

            // Assumption: Path variables are in the same order as the parameters

            if (parseRequestBody) {
                if (_argValues.containsKey("__serialized")) {
                    // only one Json string sent in request body for one complex object

                    if (_paramTypes.size() == 1)
                        toret[0] = parse(_paramTypes.get(0), _argValues.get("__serialized"));
                } else {

                }
            } else {
                _argValueList.toArray(toret);
            }
        } else {
            // The program was compiled so parameters were saved (yay!)

            if (parseRequestBody) {
                if (_argValues.containsKey("__serialized")) {
                    if (_paramTypes.size() == 1) {
                        toret[0] = tryParse(_paramTypes.get(0), _paramTypes.get(0).getName(), _argValues.get("__serialized"));
                    }
                } else {
                    // each argValue will be deserialized into complex objects
                    for (int key = 0; key < _order.size(); key++) {
                        // Unbox and cast - Initially a String argument

                        toret[key] = tryParse(_paramTypes.get(key), _paramTypes.get(key).getName(), _argValues.get(_order.get(key).getName()));
                    }
                }
            } else {

                // We'll match the path variable names to the method parameter names, no matter the path variable order
                for (int key = 0; key < _order.size(); key++) {
                    // Unbox and cast - Initially a String argument
                    try {
                        toret[key] = tryParse(_paramTypes.get(key), _paramTypes.get(key).getName(), _argValues.get(_order.get(key).getName()));
                    } catch (Exception e) {
                        say(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        return toret;
    }

    private static Object tryParse(Class<?> _paramType, String paramName, Object o) {
        switch (paramName) {
            case "java.lang.String":
                return (String) o;
            case "int":
                return Integer.parseInt((String) o);
            case "boolean":
                return Boolean.parseBoolean((String) o);
            case "char":
                return ((String) o).charAt(0);
            case "double":
                return Double.parseDouble((String) o);
            case "long":
                return Long.parseLong((String) o);
            case "short":
                return Short.parseShort((String) o);
            case "byte":
                return Byte.parseByte((String) o);
            case "float":
                return Float.parseFloat((String) o);
            default:
                // Treat it as a JSON Stringified/Serialized Object if the arg value is a string at this point,
                // try to deserialize/autobind if it is
                if (o instanceof String) {

                    return parse(_paramType, o);
                } else {
                    return o.toString();
                }
        }
    }

    static String toString(Object o) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        String toret = "";

        try {
            toret = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return toret;
    }

    static Object parse(Class<?> p, Object o) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        Object toret = null;

        String s = (String) o;

        // Escape if URL Encoded string, which often times contains a % sign
        if (s.contains("%")) {
            try {
                s = URLDecoder.decode(s, "UTF-8");
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

    static void loadOrder(HashMap<Integer, Parameter> _map, Method _method) {
        Parameter[] params = _method.getParameters();

        for (int i = 0; i < params.length; i++) {
            _map.put(i, params[i]);
        }
    }

    static void loadParamTypes(HashMap<Integer, Class<?>> _map, Method _method) {
        Class<?>[] test = _method.getParameterTypes();

        for (int i = 0; i < test.length; i++) {
            _map.put(i, test[i]);
        }
    }

    static void setArgumentNameIndex(ArrayList<String> _list, String _path) {
        if (_path != null) {
            String[] split = _path.split("/");

            for (String arg : split) {
                if (arg.startsWith(":")) {
                    _list.add(arg.substring(1));
                }
            }
        }
    }

    static String parseMethodName(Method _method) {
        String name = _method.getName().toLowerCase();

        // Check if the method is an exact match of an http method, if it is then we'll treat it as such
        switch (name) {
            case "get":
            case "post":
            case "put":
            case "delete":
            case "options":
                return _method.getName();
            default:
                break;
        }

        // If to this point, check if name contains the method name - performance hit but who are we to decide?  Tradeoffs...
        if (name.contains("get")) {
            return "get";
        } else if (name.contains("post")) {
            return "post";
        } else if (name.contains("put")) {
            return "put";
        } else if (name.contains("delete")) {
            return "delete";
        } else if (name.contains("options")) {
            return "options";
        }

        // Defaults to get
        return "get";
    }

    static <T> String getBasePathValue(Class<T> _sub) {
        String basePath = "";

        if (_sub.isAnnotationPresent(rest.vertx.Annotations.Base.class))
            basePath = _sub.getAnnotation(Base.class).value();

        // Make sure the forward slash is present at the very minimum for base path
        if (basePath.length() > 0 && !basePath.equals("/"))
            return "/" + basePath;
        else
            return "/";
    }

    static boolean hasNoParam(Method _method) {
        return _method.isAnnotationPresent(NoParam.class);
    }

    static boolean getIgnore(Method _method) {
        return _method.isAnnotationPresent(RestIgnore.class);
    }

    static String[] getCORS(Method _method) {
        if (_method.isAnnotationPresent(rest.vertx.Annotations.CORS.class)) {
            rest.vertx.Annotations.CORS[] cors = _method.getAnnotationsByType(rest.vertx.Annotations.CORS.class);

            String[] ipAndPorts = new String[cors.length];

            for (int i = 0; i < cors.length; i++) {
                if (cors[i].value() != null && cors[i].value().length() > 0) {
                    // Only support one
                    ipAndPorts[i] = cors[i].value();
                } else {
                    ipAndPorts[i] = "*";
                }

                if (i < cors.length - 1) {
                    ipAndPorts[i] += ",";
                }
            }

            return ipAndPorts;
        } else {
            return null;
        }
    }

    static String getPath(Method _method) {
        if (_method.isAnnotationPresent(rest.vertx.Annotations.Path.class)) {
            String path = _method.getAnnotation(rest.vertx.Annotations.Path.class).value();

            // Make sure the forward slash is present at the very minimum for method path if it's set
            if (path.length() > 0 && !(path.charAt(0) == '/'))
                return "/" + path;

            return path;
        } else {
            return null;
        }
    }

    static String getHttpMethod(Method _method) {
        if (_method.isAnnotationPresent(rest.vertx.Annotations.Method.class)) {
            return _method.getAnnotation(rest.vertx.Annotations.Method.class).value();
        } else {
            return null;
        }
    }

    static String getResultType(Method _method) {
        if (_method.isAnnotationPresent(rest.vertx.Annotations.ResultType.class)) {
            return _method.getAnnotation(rest.vertx.Annotations.ResultType.class).value().toLowerCase();
        } else {
            return null;
        }
    }
    
    static Blocking getBlocking(Method _method) {
    	
    	boolean blocking = false;
    	boolean serial = true;
    	
    	if (_method.isAnnotationPresent(rest.vertx.Annotations.Blocking.class)) {
            
    		blocking = _method.getAnnotation(rest.vertx.Annotations.Blocking.class).value().toLowerCase().equals("true");
    		serial = _method.getAnnotation(rest.vertx.Annotations.Blocking.class).serial().toLowerCase().equals("true");
        } else {
            return new Blocking();
        }
    	
    	return new Blocking(blocking, serial);
    }

    private static final String TYPE_NAME_PREFIX = "class ";

    public static String getClassName(Type type) {
        if (type == null) {
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
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    private static Route getRouteMethod(String mthd, String path, Router subRouter) {
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
            case "OPTIONS":
                if (path != null)
                    toret = subRouter.options(path);
                else
                    toret = subRouter.options();
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
    
    private static void putHeaders(Map<String, String> headers, RoutingContext rc) {
    	
    	if (headers != null) {
	    	for (String key : headers.keySet()) {
	    		
	            rc.response().putHeader(key, headers.get(key));
	    	}
    	}
    }
    
    private static void putStatus(RestResponse rr, RoutingContext rc) {
    	
    	if (rr.getStatusCode() != 200) {
    		
    		rc.response().setStatusCode(rr.getStatusCode());
    		
    		if (rr.getStatusMessage() != null && !rr.getStatusMessage().isEmpty())
    		{
    			rc.response().setStatusMessage(rr.getStatusMessage());
    		}
    	}
    }

    private static void say(String arg) {
        System.out.println(arg);
    }
}