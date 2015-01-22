// Copyright (c) 2008-2015 Andy Goryachev <andy@goryachev.com>
package goryachev.notebook.storage;
import goryachev.common.ui.BaseDialog;
import goryachev.common.ui.BasePanel;
import goryachev.common.ui.CAction;
import goryachev.common.ui.CBorder;
import goryachev.common.ui.CButton;
import goryachev.common.ui.CCheckBox;
import goryachev.common.ui.CPanel;
import goryachev.common.ui.CScrollPane;
import goryachev.common.ui.Dialogs;
import goryachev.common.ui.Menus;
import goryachev.common.ui.Theme;
import goryachev.common.util.CKit;
import goryachev.common.util.UserException;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/** Allows to enter almost all key strokes, except for Tab and Modifier keys. */
public class StorageEntryDialog
	extends BaseDialog
{
	public final CAction cancelAction = new CAction() { public void action() { actionCancel(); } };
	public final CAction okAction = new CAction() { public void action() { actionCommit(); } };
	public final JTextField keyField;
	public final JTextArea valueField;
	public final CScrollPane scroll;
	private StorageEntry entry;
	

	protected StorageEntryDialog(StorageEditor parent, StorageEntry en)
	{
		super(parent, "StorageEntryDialog", true);
		setTitle(en == null ? "Add Key" : "Modify " + en.getKey());
		setMinimumSize(350, 180);
		setSize(700, 500);

		keyField = new JTextField();
		
		valueField = new JTextArea();
		valueField.setFont(Theme.monospacedFont());
		valueField.setLineWrap(true);
		valueField.setWrapStyleWord(true);
		
		scroll = new CScrollPane(valueField, false);
		scroll.setBorder(Theme.BORDER_FIELD);
		
		CPanel p = new CPanel();
		p.setBorder(new CBorder(10, 10, 0, 10));
		p.setLayout
		(
			new double[]
			{
				CPanel.PREFERRED,
				CPanel.FILL
			},
			new double[]
			{
				CPanel.PREFERRED,
				CPanel.FILL
			},
			10, 5
		);

		int ix = 0;
		p.add(0, ix, p.label("Key:"));
		p.add(1, ix, keyField);
		ix++;
		p.add(0, ix, p.labelTopAligned("Value:"));
		p.add(1, ix, scroll);
		
		BasePanel bp = new BasePanel();
		bp.setCenter(p);
		bp.buttons().add(new CButton(Menus.Cancel, cancelAction));
		bp.buttons().add(new CButton(Menus.OK, okAction, true));
		
		setCenter(bp);
		
		this.entry = en;
		// TODO set entry
	}
	

	public static StorageEntry open(StorageEditor parent, StorageEntry en)
	{
		StorageEntryDialog d = new StorageEntryDialog(parent, en);		
		d.open();
		return d.entry;
	}
	
	
	protected void actionCommit()
	{
		try
		{
			String k = keyField.getText();
			if(CKit.isBlank(k))
			{
				throw new UserException("Please enter a valid key.");
			}
			// TODO check if already exists only if entry==null
			
			String v = valueField.getText();
			
			if(entry == null)
			{
				entry = new StorageEntry(k, v);
			}
			else
			{
				entry.setValue(v);
			}
			
			close();
		}
		catch(Exception e)
		{
			Dialogs.err(this, e);
		}
	}
	
	
	protected void actionCancel()
	{
		close();
	}
}