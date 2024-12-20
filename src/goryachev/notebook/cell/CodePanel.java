// Copyright © 2014-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.cell;
import goryachev.common.log.Log;
import goryachev.common.util.CList;
import goryachev.notebook.Accelerators;
import goryachev.notebook.DataBook;
import goryachev.notebook.Styles;
import goryachev.notebook.icons.NotebookIcons;
import goryachev.notebook.image.JImageViewer;
import goryachev.notebook.symja.SymjaError;
import goryachev.swing.CBorder;
import goryachev.swing.CMenuItem;
import goryachev.swing.Theme;
import goryachev.swing.UI;
import goryachev.swing.XAction;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import research.dhtml3.HDocument;


public class CodePanel
	extends CellPanel
{
	public final RSyntaxTextArea textField;
	public final JLabel inField;
	public final JLabel marginField;
	private int sequence;
	private boolean error;
	private boolean running;
	private boolean clear;
	private CList<Object> results;
	private CList<JComponent> resultComponents;
	private static CBorder BORDER = new CBorder(2, 4);
	static Log log = Log.get("CodePanel");
	
	
	public CodePanel(NotebookPanel np, String text, int seq, CList<Object> results)
	{
		super(np);
		
		textField = new RSyntaxTextArea(text);
		textField.setFont(Theme.monospacedFont());
		textField.setBackground(Styles.codeColor);
		textField.setForeground(Theme.TEXT_FG);
		textField.setLineWrap(true);
		textField.setWrapStyleWord(true);
		textField.addMouseListener(handler);
		//
		textField.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		textField.setCodeFoldingEnabled(false);
		textField.setFont(Theme.monospacedFont());
		textField.setAnimateBracketMatching(false);
		textField.setHighlightCurrentLine(false);
		//
		UI.whenFocused(textField, Accelerators.TO_CODE.getKeyStroke(), np.toCodeAction);
		UI.whenFocused(textField, Accelerators.TO_H1.getKeyStroke(), np.toH1Action);
		UI.whenFocused(textField, Accelerators.TO_H2.getKeyStroke(), np.toH2Action);
		UI.whenFocused(textField, Accelerators.TO_H3.getKeyStroke(), np.toH3Action);		
		UI.whenFocused(textField, Accelerators.TO_TEXT.getKeyStroke(), np.toTextAction);
		//
		setEditor(textField, false);
		
		inField = new JLabel();
		inField.setBorder(BORDER);
		inField.setForeground(Styles.marginTextColor);
		inField.setHorizontalAlignment(JLabel.RIGHT);
		inField.addMouseListener(handler);
		setLeft(inField);
		
		marginField = new JLabel(">");
		marginField.setBorder(BORDER);
		marginField.setForeground(Styles.marginTextColor);
		marginField.addMouseListener(handler);
		setRight(marginField);
		
		setResult(seq, results);
	}
	
	
	public CellType getType()
	{
		return CellType.CODE;
	}
	
	
	public JTextComponent getEditor()
	{
		return textField;
	}
	
	
	public void saveCell(DataBook b)
	{
		CList<Object> rs = (results == null ? null : new CList(results));
		b.addCell(CellType.CODE, getText(), sequence, rs);
	}
	
	
	public void saveCell(HDocument d)
	{
		// with syntax coloring
		emitStyledHtml(d, textField);
		d.nl();
		
		// results
		for(Object r: results)
		{
			emitResultHtml(d, r);
			d.nl();
		}
	}


	protected void emitStyledHtml(HDocument d, RSyntaxTextArea ta)
	{
		int selStart = 0;
		int selEnd = ta.getDocument().getLength();
			
		Token tokenList = ta.getTokenListFor(selStart, selEnd);
		for(Token t=tokenList; t!=null; t=t.getNextToken())
		{
			if(t.isPaintable())
			{
				if(t.length() == 1 && t.charAt(0) == '\n')
				{
					d.nl();
				}
				else
				{
					Font f = ta.getFontForTokenType(t.getType());
					Color bg = ta.getBackgroundForToken(t);
					boolean underline = ta.getUnderlineForToken(t);
					if(t.isWhitespace())
					{
						// FIX
						//gen.appendToDocNoFG(t.getLexeme(), f, bg, underline);
						d.text(ExportHtml.STYLE_CODE, t.getLexeme());
					}
					else
					{
						Color fg = ta.getForegroundForToken(t);
						// FIX
						//gen.appendToDoc(t.getLexeme(), f, fg, bg, underline);
						d.text(ExportHtml.STYLE_CODE, t.getLexeme());
					}
				}
			}
		}
	}
	
	
	protected void emitResultHtml(HDocument d, Object r)
	{
		if(r instanceof BufferedImage)
		{
			try
			{
				d.image((BufferedImage)r);
			}
			catch(Exception e)
			{
				log.error(e);
			}
		}
		else if(r != null)
		{
			d.text(ExportHtml.STYLE_RESULT, r.toString());
		}
	}


	public String getText()
	{
		return textField.getText();
	}


	public void setRunning(boolean on)
	{
		if(running != on)
		{
			running = on;
			
			if(running)
			{
				clear = true;
			}
			else
			{
				clear = false;
			}
			
			updateMargin();
		}
	}
	
	
	protected void setMargin(String text, boolean error)
	{
		marginField.setText(text);
		marginField.setForeground(error ? Styles.errorColor : Styles.marginTextColor);
	}
	
	
	public void clearResult()
	{
		if(resultComponents != null)
		{
			for(JComponent c: resultComponents)
			{
				remove(c);
			}
			resultComponents = null;
		}
		
		setSequence(-1);
		
		results = null;
		error = false;
		updateMargin();
		
		UI.validateAndRepaint(np);
		np.setModified(true);
		np.updateActions();
	}
	
	
	protected void updateMargin()
	{
		if(running)
		{
			setMargin("...", false);
		}
		else
		{
			setMargin(error ? "ERROR" : "=", error);
		}
	}
	
	
	public void setSequence(int seq)
	{
		this.sequence = seq;
		inField.setText("In (" + (seq <= 0 ? "*" : seq) + "):");		
	}
	
	
	public void setFinished(int seq)
	{
		setRunning(false);
		setSequence(seq);
		
		UI.validateAndRepaint(np);
		np.setModified(true);
		np.updateActions();
	}
	
	
	public void addResult(Object v)
	{
		addResultPrivate(v);
		updateMargin();
		
		UI.validateAndRepaint(np);
		np.setModified(true);
		np.updateActions();
	}
	
	
	protected void addResultPrivate(Object v)
	{
		if(clear)
		{
			clear = false;
			clearResult();
		}
		
		if(results == null)
		{
			results = new CList();
		}
		
		results.add(v);
		
		if(v instanceof SymjaError)
		{
			error = true;
		}
		
		JComponent c = Results.createViewer(this, v);
		if(c != null)
		{
			if(resultComponents == null)
			{
				resultComponents = new CList();
			}
			
			add(c);
			resultComponents.add(c);
		}
	}
	
	
	public void setResult(int seq, CList<Object> results)
	{
		// sequence
		setSequence(seq);
		
		// results
		if(resultComponents != null)
		{
			for(JComponent c: resultComponents)
			{
				remove(c);
			}
		}

		if(results != null)
		{
			for(Object rv: results)
			{
				addResultPrivate(rv);
			}
		}
		
		// margin
		updateMargin();
		
		UI.validateAndRepaint(np);
		np.setModified(true);
		np.updateActions();
	}
	
	
	protected JPopupMenu createPopupMenu(Component c, JPopupMenu m)
	{
		boolean run = true;
		boolean text = false;
		JImageViewer imgViewer = null;
		boolean copy = false;
		
		if(c == textField)
		{
			text = true;
		}
		
		if(c instanceof JImageViewer)
		{
			imgViewer = (JImageViewer)c;
			run = false;
		}
		
		if(run)
		{
			//m.add(new CMenuItem("Run", Accelerators.RUN_CELL, np.runCellAction));
			m.add(new CMenuItem(NotebookIcons.Start, "Run in Place", Accelerators.RUN_IN_PLACE, np.runInPlaceAction));
			//m.add(new CMenuItem("Run All", Accelerators.RUN_ALL, np.runAllAction));
			m.add(new CMenuItem("Run in Debugger", XAction.DISABLED));
			m.add(new CMenuItem("Clear Result", np.clearResultsAction));
		}
		
		addCellMenus(m, np);
		
		if(text)
		{
			UI.separator(m);
			m.add(new CMenuItem("Cut", np.cutAction));
			m.add(new CMenuItem("Copy", np.copyAction));
			m.add(new CMenuItem("Paste", np.pasteAction));
		}
		
		if(imgViewer != null)
		{
			UI.separator(m);
			m.add(new CMenuItem("Copy", np.copyAction));
			m.addSeparator();
			m.add(new CMenuItem("Save Image", imgViewer.saveImageAction));
		}
		
		return m;
	}
}
