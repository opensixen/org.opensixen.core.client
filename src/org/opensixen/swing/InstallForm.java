/**
 * 
 */
package org.opensixen.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.compiere.apps.ADialog;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.opensixen.p2.IUnitModel;
import org.opensixen.p2.P2;
import org.opensixen.p2.P2Updater;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class InstallForm extends CDialog {

	private BasicEventList<IUnitModel> eventList;
	private Properties ctx;
	private CButton installBtn;
	private CComboBox locationsCombo;
	private JTable table;
	private CButton repositoryBtn;
	private CButton updateBtn;
	
	
	public InstallForm(Properties ctx)	{
		this.ctx = ctx;
		jbInit();
		load();
		pack();
	}
	
	
	private void jbInit()	{
		setLayout(new BorderLayout(10, 10));
		
		CPanel topPanel = new CPanel();
		
		add(topPanel, BorderLayout.NORTH);
		
		locationsCombo = new CComboBox(P2.get().getRepositories());
		locationsCombo.addActionListener(this);
		topPanel.add(locationsCombo);
		
		repositoryBtn = new CButton(Msg.getMsg(ctx, "Manage Repositories"));
		repositoryBtn.addActionListener(this);
		topPanel.add(repositoryBtn);
		
		
		CPanel treePanel = new CPanel();
		add(treePanel, BorderLayout.CENTER);
		
		
		// GlazzedList
		eventList = new BasicEventList<IUnitModel>();
		FilterList<IUnitModel> filterList = new FilterList<IUnitModel>(eventList);
		EventTableModel<IUnitModel> tableModel = new  EventTableModel<IUnitModel>(filterList, new IUnitTableFormat());
		table = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(table);
		treePanel.add(scrollPane);
		
		
		CPanel btnPanel = new CPanel();
		add(btnPanel, BorderLayout.SOUTH);
		
		installBtn = new CButton(Msg.getMsg(Env.getAD_Language(ctx), "Install"));
		installBtn.addActionListener(this);
		btnPanel.add(installBtn);
		
		
		updateBtn = new CButton(Msg.getMsg(Env.getAD_Language(ctx), "Update"));
		updateBtn.addActionListener(this);
		btnPanel.add(updateBtn);
	}


	private void load()	{
		URI location = (URI) locationsCombo.getSelectedItem();
		eventList.clear();
		eventList.addAll(P2.get().getAllIUnit(location));		
	}

	
	private void install()	{
		ArrayList<IUnitModel> iunits = new ArrayList<IUnitModel>();
		for (int i=0; i < eventList.size(); i++)	{
			IUnitModel iunit = eventList.get(i);
			if (iunit.isSelected())	{
				iunits.add(iunit);
			}
		}
		
		if (P2.get().install(iunits))	{
			ADialog.info(0, this, "Ok");
		}
		else {
			ADialog.error(0, this, "No se han podido instalar las actualizaciones.");
		}
	}
	
	
	private void update()	{
		P2Updater.startupUpdater();
		ADialog.info(0, this, "Ok");
	}

	/* (non-Javadoc)
	 * @see org.compiere.swing.CDialog#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(installBtn))	{
			install();			
		}
		
		if (e.getSource().equals(repositoryBtn))	{
			ManageRepositories manageRepositories = new ManageRepositories(ctx, this);
			manageRepositories.setVisible(true);
		}
		
		if (e.getSource().equals(locationsCombo))	{
			load();
		}
		
		if (e.getSource().equals(updateBtn))	{
			update();
		}
	}
	
	
	
}

class IUnitTableFormat implements AdvancedTableFormat<IUnitModel>, WritableTableFormat<IUnitModel>	{

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.TableFormat#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.TableFormat#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "";
		case 1:
			return "ID";
		case 2:
			return "Descripcion";
		default:
			throw new RuntimeException("Imposible column index: " + column);
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.TableFormat#getColumnValue(java.lang.Object, int)
	 */
	@Override
	public Object getColumnValue(IUnitModel iunit, int column) {
		switch (column) {
		case 0:
			return new Boolean(iunit.isSelected());
		case 1:
			return iunit.getName();
		case 2:
			return iunit.getDescription();
		default:
			throw new RuntimeException("Imposible column index: " + column);
		}

	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnClass(int)
	 */
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return Boolean.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		default:
			throw new RuntimeException("Imposible column index: " + column);
		}

	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnComparator(int)
	 */
	@Override
	public Comparator getColumnComparator(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.WritableTableFormat#isEditable(java.lang.Object, int)
	 */
	@Override
	public boolean isEditable(IUnitModel baseObject, int column) {
		if (column != 0)	{
			return false;			
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.WritableTableFormat#setColumnValue(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public IUnitModel setColumnValue(IUnitModel baseObject, Object editedValue, int column) {
		if (column != 0)	{	
			return null;
		}
		
		Boolean value = (Boolean) editedValue;
		baseObject.setSelected(value);
		return baseObject;
	}
	
}
