// Copyright (c) 2006-2015 Andy Goryachev <andy@goryachev.com>
package goryachev.common.ui;
import goryachev.common.ui.theme.ALinearPanel;


/** panel lays out components vertically */
public class VerticalPanel
	extends ALinearPanel
{
	public VerticalPanel(int gap)
	{
		super(false, gap);
		setGap(gap);
	}
	
	
	public VerticalPanel()
	{
		super(false);
	}
}