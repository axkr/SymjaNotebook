// Copyright (c) 2009-2015 Andy Goryachev <andy@goryachev.com>
package goryachev.common.ui.theme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicRadioButtonUI;


public class AgRadioButtonUI
	extends BasicRadioButtonUI
{
	private final static AgRadioButtonUI radioButtonUI = new AgRadioButtonUI();
	protected Color focusColor;
	
	
	public static void init(UIDefaults d)
	{
		d.put("RadioButtonUI", AgRadioButtonUI.class.getName());
		d.put("RadioButton.contentAreaFilled", Boolean.FALSE);
		d.put("RadioButton.icon", new AgRadioButtonIcon());
	}


	protected void installDefaults(AbstractButton b)
	{
		super.installDefaults(b);
		b.setOpaque(false);
		focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
	}


	public static ComponentUI createUI(JComponent b)
	{
		return radioButtonUI;
	}


	protected Color getFocusColor()
	{
		return focusColor;
	}


	protected void paintFocus(Graphics g, Rectangle textRect, Dimension d)
	{
		g.setColor(getFocusColor());
		BasicGraphicsUtils.drawDashedRect(g, textRect.x, textRect.y, textRect.width, textRect.height);
	}
}