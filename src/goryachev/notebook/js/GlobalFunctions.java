// Copyright (c) 2015 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.js;
import goryachev.common.util.SB;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


public class GlobalFunctions
	extends ImporterTopLevel
{
	public static void init(ScriptableObject sc)
    {
		String[] names = 
		{
			"help", 
			"print",
		};
		sc.defineFunctionProperties(names, GlobalFunctions.class, ScriptableObject.DONTENUM);
    }
	
	
	/**
	 * Print a help message.
	 *
	 * This method is defined as a JavaScript function.
	 */
	public static void help(Context cx, Scriptable thisObj, Object[] args, Function f)
	{
		JsEngine.get().print("Help!");
	}


	/**
	 * Print the string values of its arguments.
	 *
	 * This method is defined as a JavaScript function.
	 * Note that its arguments are of the "varargs" form, which
	 * allows it to handle an arbitrary number of arguments
	 * supplied to the JavaScript function.
	 *
	 */
	public static void print(Context cx, Scriptable thisObj, Object[] args, Function f)
	{
		SB sb = new SB();
		
		for(int i=0; i<args.length; i++)
		{
			if(i > 0)
			{
				sb.sp();
			}

			// convert the arbitrary JavaScript value into a string form.
			String s = Context.toString(args[i]);
			sb.append(s);
		}
		
		JsEngine.get().print(sb.toString());
		
		//return Context.getUndefinedValue();
	}
}
