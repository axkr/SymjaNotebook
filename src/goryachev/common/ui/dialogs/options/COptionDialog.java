// Copyright (c) 2008-2014 Andy Goryachev <andy@goryachev.com>
package goryachev.common.ui.dialogs.options;
import goryachev.common.ui.CBorder;
import goryachev.common.ui.CButtonPanel;
import goryachev.common.ui.CDialog;
import goryachev.common.ui.CPanel;
import goryachev.common.ui.Dialogs;
import goryachev.common.ui.Menus;
import goryachev.common.ui.UI;
import java.awt.Component;
import java.awt.event.KeyEvent;


public class COptionDialog
	extends CDialog
{
	public final OptionPanel optionPanel;
	public final CButtonPanel buttonPanel;
	private boolean changed;
	

	public COptionDialog(Component parent, String title, OptionTreeNode root, String name)
	{
		super(parent, name, true);
		setTitle(title);
		setMinimumSize(500, 400);

		optionPanel = new OptionPanel(root);

		buttonPanel = new CButtonPanel(10);
		buttonPanel.addButton(Menus.Cancel, closeAction);
		buttonPanel.addButton(Menus.OK, okAction, true);
		buttonPanel.setBorder(new CBorder(10));

		CPanel p = new CPanel();
		p.setCenter(optionPanel);
		p.setSouth(buttonPanel);

		getContentPane().add(p);
		setSize(750, 400);

		UI.whenAncestorOfFocusedComponent(getRootPane(), KeyEvent.VK_ESCAPE, closeAction);
	}


	public void onWindowOpened()
	{
		optionPanel.ensureSelection();
		//optionPanel.tree.requestFocus();
		optionPanel.filter.requestFocus();
	}
	
	
	public void expandTree()
	{
		optionPanel.tree.expandAll();
		optionPanel.tree.changeSelection(0, 0, false, false);
	}
	

	public boolean onWindowClosing()
	{
		return true;
	}
	
	
	public void close()
	{
		if(optionPanel.isModified())
		{
			switch(Dialogs.discardChanges(this))
			{
			case DISCARD:
				break;
			case SAVE:
				save();
				break;
			default:
				return;
			}
		}
		
		super.close();
	}


	protected void onCancel()
	{
		close();	
	}
	
	
	protected void save()
	{
		optionPanel.commit();
		changed = true;
	}
	
	
	public boolean isChanged()
	{
		return changed;
	}
	
	
	protected void onOk()
	{
		save();
		super.close();
	}
}
