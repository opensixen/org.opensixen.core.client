/**
 * 
 */
package org.opensixen.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.swing.BoxLayout;

import org.compiere.apps.ADialog;
import org.compiere.swing.CButton;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTextField;
import org.compiere.util.Msg;
import org.opensixen.interfaces.IBeanProvider;
import org.opensixen.model.ColumnDefinition;
import org.opensixen.p2.P2;
import org.opensixen.p2.RepositoryModel;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class ManageRepositories extends CDialog {

	private CTextField fName;
	private CTextField fURI;
	private Properties ctx;
	private CButton addBtn;
	private CButton removeBtn;
	private OTable table;
	private RepositoryBeanProvider modelProvider;
	private BeanTableModel tableModel;
	
	
	private RepositoryModel selected;

	public ManageRepositories(Properties ctx, Dialog owner) throws HeadlessException {
		super(owner);
		this.ctx = ctx;
		jbInit();
		pack();
	}

	private void jbInit()	{
		setLayout(new BorderLayout(10, 10));
		
		
		CPanel topPanel = new CPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		add(topPanel, BorderLayout.NORTH);
		
		CPanel formPanel = new CPanel();
		formPanel.setLayout(new GridLayout(2, 2, 5, 5));
		topPanel.add(formPanel);
		
		CLabel label = new CLabel(Msg.getMsg(ctx, "Name"));
		formPanel.add(label);		
		fName = new CTextField(20);
		formPanel.add(fName);
		
		label = new CLabel(Msg.getMsg(ctx, "URL"));
		formPanel.add(label);
		fURI = new CTextField(20);
		formPanel.add (fURI);
		
		CPanel topBtnPanel = new CPanel();
		topPanel.add(topBtnPanel);
		addBtn = new CButton(Msg.getMsg(ctx, "Add"));
		addBtn.addActionListener(this);
		topBtnPanel.add(addBtn);
		
		removeBtn = new CButton(Msg.getMsg(ctx, "Remove"));
		removeBtn.addActionListener(this);
		topBtnPanel.add(removeBtn);
		
		
		CPanel mainPanel = new CPanel();
		add(mainPanel, BorderLayout.CENTER);
		
		table = new OTable(ctx);
		modelProvider = new RepositoryBeanProvider();
		tableModel = new BeanTableModel(modelProvider, getColumnDefinitions());
		table.setModel(tableModel);
		table.setupTable();
		table.addMouseListener(this);
		
		CScrollPane scroll = new CScrollPane(table);		
		mainPanel.add(scroll);
		
	}
		
	/**
	 * Get column Definitions
	 * @return
	 */
	public ColumnDefinition[] getColumnDefinitions() {
		ColumnDefinition[] cols = {				
				new ColumnDefinition("name", Msg.getMsg(ctx, "Name"), String.class ),
				new ColumnDefinition("location", Msg.getMsg(ctx, "URL"), String.class ) };
		return cols;
	}
		
	/* (non-Javadoc)
	 * @see org.compiere.swing.CDialog#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addBtn))	{
			if (fName.getText() != null && fURI.getText() != null)	{
				URI uri;
				try {
					uri = new URI(fURI.getText());
				} catch (URISyntaxException e1) {
					ADialog.error(-1, this, "Invalid URL");
					return;
				}
				P2.get().addRepository(uri, fName.getText());
			}
		}
		
		else if (e.getSource().equals(removeBtn))	{
			if (selected != null)	{
				P2.get().removeRepository(selected.getLocation());
			}
		}
		refresh();
	}

	/* (non-Javadoc)
	 * @see org.compiere.swing.CDialog#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int index = table.getSelectedRow();
		if (index != -1)	{
			selected = (RepositoryModel) modelProvider.getModel()[index];
		}
		
		refresh();
	}
	
	public void refresh()	{
		if (selected != null)	{
			fName.setText(selected.getName());
			fURI.setText(selected.getLocation().toString());
		}
		
		modelProvider.reload();
		table.repaint();
	}
	
	
	
	
}

class RepositoryBeanProvider implements IBeanProvider {

	private RepositoryModel[] model;
	
	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.IBeanProvider#getModel()
	 */
	@Override
	public Object[] getModel() {
		if (model == null)	{
			load();
		}
		
		return model;
	}

	private void load()	{
		model = P2.get().getAllRepositoryModel();
	}
	
	
	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.IBeanProvider#reload()
	 */
	@Override
	public void reload() {
		load();		
	}

	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.IBeanProvider#getModelClass()
	 */
	@Override
	public Class getModelClass() {
		return RepositoryModel.class;
	}

}

