// Copyright © 2015-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.js.io;
import goryachev.common.io.CReader;
import goryachev.common.io.CSVReader;
import goryachev.common.util.CKit;
import goryachev.common.util.CMap;
import goryachev.common.util.SB;
import goryachev.common.util.UserException;
import goryachev.notebook.js.classes.DTable;
import goryachev.notebook.symja.SymjaUtil;
import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;


public class DTableReader
{
	private boolean firstRowContainsColumnNames;
	private boolean lenient;
	private Charset encoding;
	private File file;
	private CMap<Object,Object> columns;
	private DateFormat dateFormat;
	private DateFormat dateTimeFormat;
	private DateFormat timeFormat;
	private char decimalSeparator = '.';
	private static final String PREFIX_NAME = "c.";
	private static final String PREFIX_TYPE = "t.";
	private enum ColumnType
	{
		STRING,
		DATE,
		TIME,
		DATE_TIME,
		FLOAT,
		DOUBLE,
		INT,
		LONG
	}
	
	
	public DTableReader()
	{
	}
	
	
	public void setLenient(boolean on)
	{
		lenient = on;
	}
	
	
	public void setEncoding(String enc) throws Exception
	{
		encoding = Charset.forName(enc);
	}
	
	
	public void setColumnName(int col, String name)
	{
		columns().put(PREFIX_NAME + col, name);
	}
	
	
	protected String getColumnName(int col)
	{
		return (String)getColumnValue(PREFIX_NAME + col);
	}
	
	
	protected Object getColumnValue(Object key)
	{
		if(columns == null)
		{
			return null;
		}
		else
		{
			return columns.get(key);
		}
	}
	
	
	public void setColumnType(int col, Object type)
	{
		columns().put(PREFIX_TYPE + col, parseType(type));
	}
	
	
	protected ColumnType getColumnType(int col)
	{
		return (ColumnType)getColumnValue(PREFIX_TYPE + col);
	}
	
	
	protected CMap<Object,Object> columns()
	{
		if(columns == null)
		{
			columns = new CMap();
		}
		return columns;
	}
	
	
	protected ColumnType parseType(Object x)
	{
		String s = x.toString();
		for(ColumnType t: ColumnType.values())
		{
			if(t.toString().equalsIgnoreCase(s))
			{
				return t;
			}
		}
		throw new UserException("invalid column type: " + x);
	}
	
	
	public void setFirstRowContainsColumnNames(boolean on)
	{
		firstRowContainsColumnNames = on;
	}
	
	
	public void setDecimalSeparator(Object x)
	{
		decimalSeparator = x.toString().charAt(0);
	}
	
	
	public void setDateFormat(Object f)
	{
		dateFormat = SymjaUtil.parseDateFormat(f);
	}
	
	
	public void setDateTimeFormat(Object f)
	{
		dateTimeFormat = SymjaUtil.parseDateFormat(f);
	}
	
	
	public void setTimeFormat(Object f)
	{
		timeFormat = SymjaUtil.parseDateFormat(f);
	}
	
	
	protected void setColumns(DTable table, String[] ss)
	{
		// fill in column names unless exist
		for(int i=0; i<ss.length; i++)
		{
			String name = getColumnName(i);
			if(name == null)
			{
				// use data
				name = ss[i];
			}
			
			table.addColumn(name);
		}
	}
	
	
	protected void addRow(DTable table, String[] ss) throws Exception
	{
		int row = table.getRowCount();
		
		for(int i=0; i<ss.length; i++)
		{
			String s = ss[i];
			ColumnType t = getColumnType(i);
			Object v;
			try
			{
				v = convert(s, t);
			}
			catch(Exception e)
			{
				if(lenient)
				{
					v = s;
				}
				else
				{
					throw e;
				}
			}
			table.setValue(row, i, v);
		}
	}
	
	
	protected Object convert(String s, ColumnType t) throws Exception
	{
		if(t == null)
		{
			return s;
		}
		
		switch(t)
		{
		case DATE:
			if(dateFormat == null)
			{
				throw err("date format");
			}
			else
			{
				return dateFormat.parse(s);
			}
			
		case DATE_TIME:
			if(dateFormat == null)
			{
				throw err("date time format");
			}
			else
			{
				return dateFormat.parse(sanitizeNumber(s));
			}
			
		case DOUBLE:
			return Double.parseDouble(sanitizeNumber(s));
			
		case FLOAT:
			return Float.parseFloat(sanitizeNumber(s));
			
		case INT:
			return Integer.parseInt(sanitizeNumber(s));
			
		case LONG:
			return Long.parseLong(sanitizeNumber(s));
			
		case TIME:
			if(timeFormat == null)
			{
				throw err("time format");
			}
			else
			{
				return timeFormat.parse(s);
			}
			
		case STRING:
		default:
			return s;
		}
	}
	
	
	protected String sanitizeNumber(String s)
	{
		// decimal separator
		// 1e2
		int sz = s.length();
		SB sb = new SB(sz);
		for(int i=0; i<sz; i++)
		{
			char c = s.charAt(i);
			
			if(decimalSeparator != 0)
			{
				if(c == decimalSeparator)
				{
					sb.append(c);
					continue;
				}
			}
			
			switch(c)
			{
			case '-':
			case '+':
			case 'e':
			case 'E':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	
	protected RuntimeException err(String s)
	{
		return new UserException("please set a valid " + s);
	}
	
	
	public DTable readFile(Object file) throws Exception
	{
		File f = SymjaUtil.parseFile(file);
		CReader rd = new CReader(f, encoding);
		return read(rd);
	}
	
	
	protected DTable read(CReader rd) throws Exception
	{
		DTable t = new DTable();
		
		try
		{
			boolean header = firstRowContainsColumnNames;
			CSVReader csv = new CSVReader(rd);
			try
			{
				String[] ss;
				while((ss = csv.readNext()) != null)
				{
					if(header)
					{
						setColumns(t, ss);
						header = false;
					}
					else
					{
						addRow(t, ss);
					}
				}
			}
			finally
			{
				CKit.close(csv);
			}
		}
		finally
		{
			CKit.close(rd);
		}
		
		return t;
	}
}
