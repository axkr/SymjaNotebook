// Copyright (c) 2015 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.js;
import goryachev.common.ui.BackgroundThread;
import goryachev.common.util.CList;
import goryachev.common.util.SB;
import goryachev.notebook.cell.CodePanel;
import goryachev.notebook.cell.NotebookPanel;
import goryachev.notebook.js.fs.FS;
import goryachev.notebook.js.io.IO;
import goryachev.notebook.js.nb.NB;
import java.util.concurrent.atomic.AtomicInteger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


public class JsEngine
{
	private final NotebookPanel np;
	//private Context context;
	protected Scriptable scope;
	//private ScriptEngine scriptEngine;
	private AtomicInteger runCount = new AtomicInteger(1);
	private BackgroundThread thread;
	private SB log = new SB();
	private CList<Object> results = new CList();
	//protected static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	protected static final ThreadLocal<JsEngine> engine = new ThreadLocal();
	
	
	public JsEngine(NotebookPanel np)
	{
		this.np = np;
	}
	
	
//	protected synchronized ScriptEngine engine() throws Exception
//	{
//		if(scriptEngine == null)
//		{
//			scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
//			
//			scriptEngine.getContext().setWriter(new PrintWriter(new Writer()
//			{
//				public void write(char[] cbuf, int off, int len) throws IOException
//				{
//					print(new String(cbuf,off,len));
//				}
//
//				public void close() throws IOException { }
//				public void flush() throws IOException { }
//			}));
//		
//			// global context objects
//			scriptEngine.put("FS", new FS());
//			scriptEngine.put("IO", new IO());
//			scriptEngine.put("NB", new NB());
//			
//			// common packages
//			scriptEngine.eval("importPackage(Packages.java.lang);");
//		}
//		return scriptEngine;
//	}
	
	
	protected synchronized Scriptable scope(Context cx) throws Exception
	{
		if(scope == null)
		{
//			scriptEngine.getContext().setWriter(new PrintWriter(new Writer()
//			{
//				public void write(char[] cbuf, int off, int len) throws IOException
//				{
//					print(new String(cbuf,off,len));
//				}
//
//				public void close() throws IOException { }
//				public void flush() throws IOException { }
//			}));
//		
//			// global context objects
//			scriptEngine.put("FS", new FS());
//			scriptEngine.put("IO", new IO());
//			scriptEngine.put("NB", new NB());
//			
//			// common packages
//			scriptEngine.eval("importPackage(Packages.java.lang);");
			
			scope = cx.initStandardObjects();
			
			ScriptableObject.putProperty(scope, "FS", new FS());
			ScriptableObject.putProperty(scope, "IO", new IO());
			ScriptableObject.putProperty(scope, "NB", new NB());
			
			//cx.evaluateString(scope(cx), "importPackage(java.lang);", "<cmd>", 1, null);
		}
		
		return scope;
	}
	
	
	protected synchronized void print(String s)
	{
		if(log.isNotEmpty())
		{
			log.nl();
		}
		
		// TODO check for full buffer and append message "Too many lines" 
		
		log.append(s);
	}
	
	
	public synchronized void display(Object x)
	{
		if(log.isNotEmpty())
		{
			results.add(log.getAndClear());
		}
		
		Object v = JsUtil.makeDatasnapshot(x);
		results.add(v);
	}
	
	
	public void execute(final CodePanel p)
	{
		p.setRunning();
		results.clear();

		final String script = p.getText();

		thread = new BackgroundThread("js")
		{
			public void process() throws Throwable
			{
				Context cx = Context.enter();
				try
				{
					engine.set(JsEngine.this);
					Object rv = cx.evaluateString(scope(cx), script, "<cmd>", 1, null);
					display(rv);
				}
				finally
				{
					Context.exit();
				}
			}
			
			public void success()
			{
				finished(p);
			}

			public void onError(Throwable e)
			{
				display(e);
				finished(p);
			}
		};
		thread.start();
	}
	
	
	protected void finished(CodePanel p)
	{
		thread = null;
		
		int count = runCount.getAndIncrement();  
		p.setResult(count, results);
	}
	
	
	public void execute(CList<CodePanel> ps)
	{
		// TODO
	}
	
	
	public void interrupt()
	{
		BackgroundThread t = thread;
		if(t != null)
		{
			t.cancel();
		}
	}
	
	
	public boolean isRunning()
	{
		return (thread != null);
	}
	
	
	public static JsEngine get()
	{
		return engine.get();
	}
}