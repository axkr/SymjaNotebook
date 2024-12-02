// Copyright © 2014-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook;
import goryachev.i18n.Menus;
import goryachev.swing.Application;
import goryachev.swing.CAction;
import goryachev.swing.CMenu;
import goryachev.swing.CMenuItem;
import goryachev.swing.dialogs.CheckForUpdatesDialog;


public class HelpMenu
	extends CMenu
{
	public final CAction checkForUpdatesAction = new CAction() { @Override
  public void action() { actionCheckForUpdates(); }};

	
	public HelpMenu()
	{
		super(Menus.Help);
		add(new CMenuItem(Menus.ContactSupport, ContactSupport.action));
        // add(new CMenuItem(Menus.CheckForUpdates, checkForUpdatesAction));
		addSeparator();
		add(new CMenuItem(Menus.License, Application.licenseAction()));
		add(new CMenuItem(Menus.OpenSourceLicenses, OpenSourceLicenses.openDialogAction));
		addSeparator();
		add(new CMenuItem(Menus.About, About.action));
	}
	
	
	public void actionCheckForUpdates()
	{
		new CheckForUpdatesDialog(this, SymjaNotebookApp.getUpdateURL(true), SymjaNotebookApp.WEB_SITE).open();
	}
}
