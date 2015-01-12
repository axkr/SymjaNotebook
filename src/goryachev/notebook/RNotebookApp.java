// Copyright (c) 2014-2015 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook;
import goryachev.common.ui.Application;
import goryachev.common.ui.UI;
import goryachev.common.ui.options.IntOption;
import goryachev.common.ui.options.StringListOption;
import goryachev.common.util.CList;
import goryachev.common.util.Log;
import goryachev.common.util.SB;
import goryachev.notebook.icons.NotebookIcons;
import java.io.File;
import javax.swing.ImageIcon;


public class RNotebookApp
	extends Application
{
	public static final String VERSION = "0.04.07";
	public static final String COPYRIGHT = "copyright © 2015 andy goryachev";	
	public static final String WEB_SITE = "http://goryachev.com/products/rnotebook";
	protected static final StringListOption openDocumentsOption = new StringListOption("open.documents");
	protected static final IntOption startCountOption = new IntOption("start.count", 0, 0, Integer.MAX_VALUE);
	public static final int REMINDER_COUNT = 60;
	
	
	public RNotebookApp()
	{
		super("RNotebook", VERSION, COPYRIGHT);
	}
	
	
	public static void main(String[] args)
	{
		new RNotebookApp().start();
	}
	
	
	public static String getUpdateURL(boolean menu)
	{
		SB sb = new SB();
		sb.a(WEB_SITE);
		sb.a("/version?");
		sb.a(getVersion());
		
		if(menu)
		{
			sb.a("&menu");
		}
		
		return sb.toString();
	}


	public ImageIcon getAppIcon()
	{
		return NotebookIcons.Application;
	}


	public String getAppTitle()
	{
		return "RNotebook";
	}


	protected void initApplication() throws Exception
	{
	}
	
	
	protected void storeOpenWindows()
	{
		CList<String> ids = new CList();
		for(NotebookWindow w: UI.getWindowsOfType(NotebookWindow.class))
		{
			File f = w.getFile();
			if(f != null)
			{
				ids.add(f.getAbsolutePath());
			}
		}
		
		openDocumentsOption.set(ids);
	}
	
	
	public boolean exiting()
	{
		storeOpenWindows();
		
		if(NotebookWindow.askToSaveAllOnExit() == false)
		{
			return false;
		}

		return true;
	}


//	public void openMainWindow()
//	{
//		new NotebookWindow().open();
//	}
	
	
	public void openMainWindow() throws Exception
	{
		NotebookWindow lastWindow = null;

		// load last state
		CList<String> fs = openDocumentsOption.get();
		if(fs != null)
		{
			for(String fname: fs)
			{
				try
				{
					final File f = new File(fname);
					if(f.exists())
					{
						final NotebookWindow w = new NotebookWindow();
						w.open();
						lastWindow = w;

						// show all windows before doing i/o
						UI.later(new Runnable()
						{
							public void run()
							{
								w.openFile(f);
							}
						});
					}
				}
				catch(Exception e)
				{
					Log.err(e);
				}
			}
		}

		if(lastWindow == null)
		{
			lastWindow = new NotebookWindow();
			lastWindow.open();
		}

		// thank you dialog every so often
		int startCount = startCountOption.get() + 1;
		startCountOption.set(startCount);
		if((startCount % REMINDER_COUNT) == 0)
		{
			ThankYouDialog.openDialog(lastWindow);
		}
	}
}