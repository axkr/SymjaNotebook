// Copyright (c) 2014-2015 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook;
import goryachev.common.ui.Accelerator;
import goryachev.common.ui.Menus;
import java.awt.event.KeyEvent;


public class Accelerators
{
	public static final String MAIN_MENU = "Main Menu";
	
	public static Accelerator CLOSE_WINDOW = new Accelerator("close.window", MAIN_MENU, "Close");
	public static Accelerator FIND = new Accelerator("find", MAIN_MENU, "Find", KeyEvent.VK_F, true);
	public static Accelerator PREFERENCES = new Accelerator("preferences", MAIN_MENU, "Preferences", KeyEvent.VK_PERIOD, true);
	public static Accelerator SAVE = new Accelerator("save", MAIN_MENU, Menus.Save, KeyEvent.VK_S, true);
	public static Accelerator SAVE_AS = new Accelerator("save.as", MAIN_MENU, Menus.SaveAs);
}
