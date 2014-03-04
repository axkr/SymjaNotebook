// Copyright (c) 2011-2014 Andy Goryachev <andy@goryachev.com>
package goryachev.common.util;
import java.awt.Color;
import java.io.File;
import java.math.BigInteger;
import java.util.TimeZone;
import javax.swing.Icon;


/** 
 * Convenience methods that attempt to extract requested value, 
 * returning null if the said value can not be extracted.
 */
public class Parsers
{
	public static Double parseDouble(Object x)
	{
		if(x instanceof Number)
		{
			return ((Number)x).doubleValue();
		}
		else if(x != null)
		{
			try
			{
				String s = x.toString();
				s = s.trim();
				return Double.parseDouble(s);
			}
			catch(Exception e)
			{ }
		}
		return null;
	}
	
	
	public static double parseDouble(Object x, double defaultValue)
	{
		Double d = parseDouble(x);
		if(d == null)
		{
			return defaultValue;
		}
		else
		{
			return d;
		}
	}
	
	
	public static Integer parseInteger(Object x)
	{
		if(x instanceof Number)
		{
			return ((Number)x).intValue();
		}
		else if(x != null)
		{
			try
			{
				String s = x.toString();
				s = s.trim();
				return Integer.parseInt(s);
			}
			catch(Exception e)
			{ }
		}
		return null;
	}
	
	
	public static Integer parseIntegerHex(Object x)
	{
		if(x instanceof Number)
		{
			return ((Number)x).intValue();
		}
		else if(x != null)
		{
			try
			{
				String s = x.toString();
				s = s.trim();
				return Integer.parseInt(s, 16);
			}
			catch(Exception e)
			{ }
		}
		return null;
	}
	
	
	public static Integer[] parseIntegerArray(Object x)
	{
		if(x instanceof Integer[])
		{
			return (Integer[])x;
		}
		return null;
	}
	
	
	public static int parseInt(Object x, int defaultValue)
	{
		if(x instanceof Number)
		{
			return ((Number)x).intValue();
		}
		else if(x != null)
		{
			try
			{
				String s = x.toString();
				s = s.trim();
				return Integer.parseInt(s);
			}
			catch(Exception e)
			{ }
		}
		return defaultValue;
	}
	
	
	public static Float parseFloat(Object x)
	{
		if(x instanceof Number)
		{
			return ((Number)x).floatValue();
		}
		else if(x != null)
		{
			try
			{
				String s = x.toString();
				s = s.trim();
				return Float.parseFloat(s);
			}
			catch(Exception e)
			{ }
		}
		return null;
	}
	
	
	public static float parseFloat(Object x, float defaultValue)
	{
		Float f = parseFloat(x);
		if(f == null)
		{
			return defaultValue;
		}
		else
		{
			return f;
		}
	}
	
	
	public static Long parseLong(Object x)
	{
		if(x instanceof Number)
		{
			return ((Number)x).longValue();
		}
		else if(x != null)
		{
			try
			{
				String s = x.toString();
				s = s.trim();
				return Long.parseLong(s);
			}
			catch(Exception e)
			{ }
		}
		return null;
	}
	
	
	public static long parseLong(Object x, long defaultValue)
	{
		if(x instanceof Number)
		{
			return ((Number)x).longValue();
		}
		else if(x != null)
		{
			try
			{
				String s = x.toString();
				s = s.trim();
				return Long.parseLong(s);
			}
			catch(Exception e)
			{ }
		}
		return defaultValue;
	}
	
	
	public static String parseString(Object x)
	{
		if(x != null)
		{
			return x.toString();
		}
		return null;
	}
	
	
	public static String parseStringNotNull(Object x)
	{
		if(x != null)
		{
			return x.toString();
		}
		return "";
	}
	
	
	public static String parseStringOnly(Object x)
	{
		if(x instanceof String)
		{
			return (String)x;
		}
		return null;
	}
	
	
	public static Color parseColor(Object x)
	{
		if(x instanceof Color)
		{
			return (Color)x;
		}
		else if(x instanceof Integer)
		{
			return new Color((Integer)x, true);
		}
		return null;
	}
	
	
	public static boolean parseBool(Object x)
	{
		return parseBool(x, false);
	}
	
	
	public static boolean parseBool(Object x, boolean defaultValue)
	{
		if(x instanceof Boolean)
		{
			return (Boolean)x;
		}
		else if(x != null)
		{
			return "true".equals(x.toString());
		}

		return defaultValue;
	}
	
	
	public static boolean parseBooleanStrict(Object x)
	{
		return Boolean.TRUE.equals(x);
	}


	public static byte[] parseByteArray(Object x)
	{
		try
		{
			if(x instanceof byte[])
			{
				return (byte[])x;
			}
			else if(x instanceof String)
			{
				return Hex.parseByteArray((String)x);
			}
		}
		catch(Exception e)
		{
			Log.err(e);
		}
		
		return null;
	}
	

	public static Icon parseIcon(Object x)
	{
		return (x instanceof Icon) ? (Icon)x : null;
	}
	
	
	public static String parseTimeZoneCode(Object x)
	{
		if(x instanceof CTimeZone)
		{
			return ((CTimeZone)x).getID();
		}
		else if(x instanceof TimeZone)
		{
			return ((TimeZone)x).getID();
		}
		else if(x instanceof String)
		{
			// no validation
			return (String)x;
		}
		else
		{
			return null;
		}
	}
	
	
	public static File parseFile(Object x)
	{
		if(x != null)
		{
			try
			{
				if(x instanceof File)
				{
					return (File)x;
				}
				else if(x instanceof String)
				{
					return new File((String)x);
				}
			}
			catch(Exception e)
			{
				Log.err(e);
			}
		}
		return null;
	}
	
	
	public static String[] parseStringArray(Object x)
	{
		if(x instanceof String[])
		{
			return (String[])x;
		}
		return null;
	}
	
	
	public static String[] parseCommaSeparatedStringArray(String s)
	{
		if(s != null)
		{
			return CKit.split(s, ',');
		}
		return null;
	}
	
	
	public static Object[] parseObjectArray(Object x)
	{
		if(x instanceof Object[])
		{
			return (Object[])x;
		}
		return null;
	}
	
	
	public static BigInteger parseBigInteger(Object x)
	{
		if(x instanceof BigInteger)
		{
			return (BigInteger)x;
		}
		else if(x instanceof String)
		{
			try
			{
				return new BigInteger((String)x);
			}
			catch(Exception e)
			{ }
		}
		return null;
	}
	
	
	public static Exception parseException(Object x)
	{
		if(x instanceof Exception)
		{
			return (Exception)x;
		}
		if(x instanceof Throwable)
		{
			return new Exception((Throwable)x);
		}
		else
		{
			return null;
		}
	}
}
