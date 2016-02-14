package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tests.models.Choir;

public class TestUtility {
	
	public static String post(String location)
	{		
		Content result = null;
		
		try {
			result = Request.Post(location)
				    .execute()
				    .returnContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (result == null) ? null : result.toString();
	}
	
	public static String post(String location, String jsonArgs)
	{		
		Content result = null;
		
		try {
			StringEntity jsonEntity = new StringEntity(jsonArgs);
			result = Request.Post(location)
					.body(jsonEntity)
				    .execute()
				    .returnContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (result == null) ? null : result.toString();
	}
	
	public static String get(String location)
	{		
		Content result = null;
		
		try {
			result = Request.Get(location)
				    .execute()
				    .returnContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Test Utility result = " + result.toString());
		
		return (result == null) ? null : result.toString();
	}
  
	  static void copy(InputStream in, ByteArrayOutputStream out, int bufferSize) {
			byte[] buf = new byte[bufferSize];
			int n;
			
			try {
				
				while ((n = in.read(buf)) >= 0) {
					out.write(buf, 0, n);
				}
				
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (in != null) 
				{
					try 
					{					
						in.close();
					}			
					catch (IOException e) 
					{
						e.printStackTrace();
					}		
				}			
				if (out != null) {
					try 
					{
						out.close();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}			
			}
	  }
  
	public static Choir toChoirFromJson(String json)
		{
			ObjectMapper mapper = new ObjectMapper();
			
			mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
	              .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
	              .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
	              .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
	              .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
			
			Choir toret = null;
			
			try {
				toret = mapper.readValue(json, Choir.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return toret;
		}
	}
