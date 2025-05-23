// Copyright © 2015-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.js.classes;
import goryachev.common.util.Keep;
import goryachev.common.util.RFileFilter;
import goryachev.notebook.js.fs.FileSyncToolUI;
import goryachev.notebook.symja.SymjaUtil;
import goryachev.swing.UI;
import java.io.File;
import javax.swing.JComponent;
import research.tools.filesync.FileSyncTool;


@Keep
public class JsFileSyncTool
{
	private FileSyncTool tool;
	protected FileSyncToolUI ui;
	
	
	public JsFileSyncTool()
	{
	}
	
	
	protected FileSyncTool tool()
	{
		if(tool == null)
		{
			tool = new FileSyncTool();
		}
		return tool;
	}
	
	
	// should be protected, but being called by FS
	public void setListener(FileSyncTool.Listener li)
	{
		tool().setListener(li);
	}
	
	
	public JComponent getGui()
	{
		if(ui == null)
		{
			UI.inEDTW(new Runnable()
			{
				public void run()
				{
					ui = new FileSyncToolUI();
					tool().setListener(ui);
				}
			});
		}
		return ui;
	}
	
	
	public void addSource(Object file)
	{
		File f = SymjaUtil.parseFile(file);
		tool().addSource(f);
	}
	
	
	public void addSource(Object source, Object filter) throws Exception
	{
		File src = SymjaUtil.parseFile(source);
		RFileFilter f = SymjaUtil.parseRFileFilter(filter);
		tool().addSource(src, f);
	}
	
	
	public void addJob(Object source, Object target)
	{
		File src = SymjaUtil.parseFile(source);
		File dst = SymjaUtil.parseFile(target);
		tool().addJob(src, dst);
	}
	
	
	public void addJob(Object source, Object target, Object filter) throws Exception
	{
		File src = SymjaUtil.parseFile(source);
		File dst = SymjaUtil.parseFile(target);
		RFileFilter f = SymjaUtil.parseRFileFilter(filter);
		tool().addJob(src, dst, f);
	}
	
	
	public void setTarget(Object file)
    {
		File f = SymjaUtil.parseFile(file);
		tool().setTarget(f);
    }
	
	
	public void setFilter(Object filter) throws Exception
	{
		RFileFilter ff = SymjaUtil.parseRFileFilter(filter);
		tool().setFileFilter(ff);
	}


	public void setGranularity(int ms)
	{
		tool().setGranularity(ms);
	}


	public void setIgnoreFailures(boolean on)
	{
		tool().setIgnoreFailures(on);
	}


	public void sync() throws Exception
	{
		tool().sync();
	}
}
