// Copyright (c) 2014 Andy Goryachev <andy@goryachev.com>
package research.notebook;
import goryachev.common.ui.BackgroundThread;
import goryachev.common.util.Log;
import goryachev.common.util.SB;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import research.notebook.js.ScriptBody;
import research.notebook.js.ScriptLogger;


public class CodeSection
	implements ScriptLogger
{
	public JTextArea textField;
	public JLabel left;
	public JLabel right;
	public JTextArea resultField;
	public ScriptBody process;
	public SB result = new SB();
	public int row;
	
	
	public synchronized void print(String s)
	{
		if(result.isNotEmpty())
		{
			result.nl();
		}
		result.append(s);
	}

	
	public synchronized void printSystem(String s)
	{
		if(result.isNotEmpty())
		{
			result.nl();
		}
		result.append(s);
	}

	
	public synchronized void printError(String s)
	{
		if(result.isNotEmpty())
		{
			result.nl();
		}
		result.append(s);
	}
	
	
	protected ScriptBody newProcess()
	{
		if(process != null)
		{
			process.cancel();
		}
		
		String script = textField.getText();
		ScriptBody p = new ScriptBody(this, script); 
		process = p;
		return p;
	}
	
	
	protected void start()
	{
		right.setText("*");
	}
	
	
	protected void setWaiting()
	{
		right.setText("?");
	}
	
	
	protected void stopped(ScriptBody p)
	{
		if(process == p)
		{
			right.setText("=");

			resultField.setText(result.getAndClear());
			resultField.setCaretPosition(0);
		}
	}
	
	
	protected void error(Throwable e)
	{
		// TODO
		Log.err(e);
	}
	
	
	public void runSection()
	{
		final ScriptBody p = newProcess();
		
		start();
		
		new BackgroundThread("script")
		{
			public void process() throws Throwable
			{
				// TODO result
				p.process();
			}
			
			public void success()
			{
				// TODO result
				stopped(p);
			}
			
			public void onError(Throwable e)
			{
				error(e);
			}
		}.start();
	}
}