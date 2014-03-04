// Copyright (c) 2010-2014 Andy Goryachev <andy@goryachev.com>
package goryachev.common.ui.options;
import goryachev.common.util.CSettings;
import java.util.Collection;


public class BooleanOption
    extends COption<Boolean>
{
	private boolean defaultValue;


	public BooleanOption(String key, boolean defaultValue)
	{
		super(key);
		this.defaultValue = defaultValue;
	}


	public BooleanOption(String key, CSettings options, Collection<COption<?>> list, boolean defaultValue)
	{
		super(key, options, list);
		this.defaultValue = defaultValue;
	}


	public Boolean defaultValue()
	{
		return defaultValue;
	}


	public Boolean parseProperty(String s)
	{
		return "true".equals(s);
	}


	public String toProperty(Boolean value)
	{
		return value.toString();
	}
	
	
	public void toggle()
	{
		set(!get());
	}
}
