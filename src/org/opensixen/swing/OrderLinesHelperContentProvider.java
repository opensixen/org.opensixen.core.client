/**
 * 
 */
package org.opensixen.swing;

import java.awt.event.ActionEvent;

import javax.swing.table.TableModel;

import org.compiere.model.GridTab;
import org.compiere.model.MQuery;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class OrderLinesHelperContentProvider extends AbstractTableHelperContentProvider {

	private OrderLinesTableModel tableModel;
	
	private int C_Order_ID;

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getTableModel(org.compiere.model.MQuery)
	 */
	@Override
	public TableModel getTableModel(MQuery query) {
		GridTab gt = getPanel().getGridController().getMTab();
		C_Order_ID = gt.getRecord_ID();
		tableModel = new OrderLinesTableModel(ctx, query);
		tableModel.setC_Order_ID(C_Order_ID);
		return tableModel;
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#initTable()
	 */
	@Override
	public void initTable() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getPosition()
	 */
	@Override
	public int getPosition() {
		return HelperContentPanel.POSITION_CENTER;
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getTabName()
	 */
	@Override
	public String getTabName() {
		return "Lineas";
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#isPriority()
	 */
	@Override
	public boolean isPriority() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		GridTab gt = getPanel().getGridController().getMTab();
		if (gt.getTableName().equals("C_Order"))	{ 
			C_Order_ID = gt.getRecord_ID();
		}
		tableModel.setC_Order_ID(C_Order_ID);
		log.warning("Actualizando: " + C_Order_ID);
		tableModel.reload();
		updateUI();
	}
	
	
	

}
