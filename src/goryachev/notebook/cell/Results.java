// Copyright © 2015-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.cell;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import org.matheclipse.image.expression.data.ImageExpr;
import goryachev.notebook.Styles;
import goryachev.notebook.image.JImageViewer;
import goryachev.notebook.js.classes.DPlot;
import goryachev.notebook.js.classes.DTable;
import goryachev.notebook.js.classes.JImage;
import goryachev.notebook.js.classes.JImageBuilder;
import goryachev.notebook.plot.DPlotViewer;
import goryachev.notebook.symja.SymjaError;
import goryachev.notebook.symja.SymjaUtil;
import goryachev.notebook.table.DTableViewer;
import goryachev.swing.Theme;


public class Results
{
	/** 
	 * Creates snapshot of javascript object into an immutable data 
	 * suitable for rendering after the execution is completed.
	 * See also createViewer() below.
	 */
	public static Object copyValue(Object x)
	{
      // if(x instanceof NativeJavaObject)
      // {
      // x = ((NativeJavaObject)x).unwrap();
      // }
      if (x instanceof ImageExpr) {
        return ((ImageExpr) x).getBufferedImage();
      }
		if(x instanceof JImage)
		{
			return ((JImage)x).getBufferedImage();
		}
		else if(x instanceof JImageBuilder)
		{
			return ((JImageBuilder)x).getImage().getBufferedImage();
		}
		else if(x instanceof DTable)
		{
			return ((DTable)x).copy();
		}
		else if(x instanceof DPlot)
		{
			return ((DPlot)x).copy();
		}
		else if(x instanceof Throwable)
		{
			String msg = SymjaUtil.decodeException((Throwable)x);
			return new SymjaError(msg);
		}
        // else if(x instanceof Undefined)
        // {
        // return x;
        // }
		else if(x instanceof JComponent)
		{
			return x;
		}
        // else if(x instanceof ProcessMonitor)
        // {
        // ProcessMonitor m = (ProcessMonitor)x;
        // return new Object[]
        // {
        // m.stdout.getBuffer(),
        // m.stderr.getError()
        // };
        // }
		else if(x != null)
		{
			return x.toString();
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * Creates viewer component for the result created by copyValue() above.
	 */
	public static JComponent createViewer(CodePanel p, Object x)
	{
		if(x == null)
		{
			return createTextViewer(p, "null", Styles.numberColor);
		}
		else if(x instanceof BufferedImage)
		{
			JImageViewer v = new JImageViewer((BufferedImage)x);
			v.addMouseListener(p.handler);
			return v;
		}
		else if(x instanceof SymjaError)
		{
			String text = ((SymjaError)x).error;
			return createTextViewer(p, text, Styles.errorColor);
		}
		else if(x instanceof DTable)
		{
			return new DTableViewer((DTable)x, p.handler);
		}
		else if(x instanceof DPlot)
		{
			return new DPlotViewer((DPlot)x, p.handler);
		}
        // else if(x == Context.getUndefinedValue())
        // {
        // // do not show undefined value
        // return null;
        // }
		else if(x instanceof JComponent)
		{
			return (JComponent)x;
		}
		else
		{
			String text = x.toString();
			return createTextViewer(p, text, Styles.resultColor);
		}
	}
	
	
	private static JComponent createTextViewer(CodePanel p, String text, Color c)
	{
		JTextArea t = new JTextArea();
		t.setFont(Theme.monospacedFont());
		t.setForeground(c);
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		t.setEditable(false);
		t.addMouseListener(p.handler);
		t.setText(text);
		return t;
	}
}
