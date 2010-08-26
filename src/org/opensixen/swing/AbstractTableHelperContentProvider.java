/**
 * 
 */
package org.opensixen.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.compiere.grid.GridController;
import org.compiere.model.GridTab;
import org.compiere.model.MQuery;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTable;
import org.opensixen.osgi.interfaces.IHelperContentProvider;

/**
 * @author harlock
 *
 */
public abstract class AbstractTableHelperContentProvider implements IHelperContentProvider, MouseListener {
	
	protected OTable table;
	protected Properties ctx;

	public abstract TableModel getTableModel(MQuery query);
	public abstract void initTable();
	private EPanel panel;
	protected GridTab gc;
	/** Indice entre el indice en la tabla y el id del registro	*/ 
	private HashMap<Integer, Integer> indexMap;
	
	protected ArrayList<Integer> selecteds = new ArrayList<Integer>();
	private String keyColumn; 
	
	/**
	 * Inicializamos la tabla con el MQuery del GridController
	 * y añadimos los listeners
	 */
	@Override
	public void initContent(Properties ctx, CPanel parent, EPanel panel) {
		this.ctx = ctx;
		this.panel = panel;
		this.gc = panel.getCurrentTab();
		this.keyColumn = gc.getKeyColumnName();
		
		table = new OTable(ctx);
		table.setModel(getTableModel(gc.getQuery()));
		
		table.setupTable();
		
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.autoSize(true);
		CScrollPane scrollPane = new CScrollPane(table);
		parent.add( scrollPane, BorderLayout.CENTER );
		table.packAll();

		table.addMouseListener(this);
		// Custom table options
		initTable();
	}
	
	private void reindex()	{
		indexMap = new HashMap<Integer, Integer>();
		for (int index=0; index < gc.getRowCount(); index++)	{
			Integer id = (Integer) gc.getValue(index, keyColumn);
			indexMap.put(id, index);
		}
	}
	
	protected void selectRecord(int record_ID)	{				
		// Si lo hemos seleccionado antes no lo añadimos a la consulta
		if (!selecteds.contains(record_ID))	{
			MQuery current = gc.getQuery();
			current.addRestriction(keyColumn + "=" +record_ID, false, 0);
			gc.setQuery(current);
			selecteds.add(record_ID);
			gc.query(false);
			reindex();
		}		
		gc.setCurrentRow(indexMap.get(record_ID));
		gc.dataRefresh();
	}
	
	/**
	 * @return the panel
	 */
	public EPanel getPanel() {
		return panel;
	}
	/* (non-Javadoc)
	 * @see org.opensixen.swing.IPanelListener#disposePerformed()
	 */
	@Override
	public boolean disposePerformed() {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see org.opensixen.swing.IPanelListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
