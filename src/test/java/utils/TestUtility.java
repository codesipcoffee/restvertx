package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestUtility {
	
	public static String get(String dataLocation)
	{		
		HttpURLConnection urlconnection = null;
		InputStream instream = null;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		
		try {
			URL url = new URL(dataLocation);
			urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.setRequestMethod("GET");
			
			if (urlconnection != null)
			{
				urlconnection.setReadTimeout(240 * 1000);
			}
			
			if (urlconnection != null && urlconnection.getInputStream() != null)
			{
				instream = urlconnection.getInputStream();
				
				copy(instream, bytes, 1024);
				
				byte[] toret = bytes.toByteArray();
				
				String utf8b = new String(toret, 0, toret.length, "UTF-8");
				
				bytes.close();
				instream.close();
				
				return utf8b;				
			}
		}
		catch (IOException e)
		{
			
		}
		
		return null;
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
}
