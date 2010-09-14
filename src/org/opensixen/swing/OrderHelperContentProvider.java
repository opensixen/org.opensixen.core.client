/**
 * 
 */
package org.opensixen.swing;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.compiere.model.GridTab;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.opensixen.model.X_C_Order_Header_v;



/**
 * @author harlock
 *
 */
public class OrderHelperContentProvider extends AbstractTableHelperContentProvider {

	private OrderTableModel model;

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getTableModel()
	 */
	@Override
	public TableModel getTableModel(MQuery query) {
		model = new OrderTableModel(ctx, query);
		return model;
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#initTable()
	 */
	@Override
	public void initTable() {
		// TODO Auto-generated method stub
		
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
		String cmd = e.getActionCommand();
		
		if (cmd.equals("Find"))	{
			GridTab gt = getPanel().getGridController().getMTab();
			MQuery query = gt.getQuery();
			String filter = query.getWhereClause(false);
			if (filter.equals("()"))	{
				return;
			}
									
			model.setQuery(query);
			System.out.println("Filter: " + filter);
		}
		
		
		
	}

	
	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// Obtenemos el registro sobre el que pulso
		int index = table.getSelectedRow();
		if (index == -1)	{
			return;
		}

		// Cargamos el modelo
		PO[] o = model.getModel(null);
		X_C_Order_Header_v	 order = (X_C_Order_Header_v) o[index];
		if (order == null)	{		
			return;
		}
		
		selectRecord(order.getC_Order_ID());
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getPosition()
	 */
	@Override
	public int getPosition() {
		return HelperContentPanel.POSITION_TOP;
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getTabName()
	 */
	@Override
	public String getTabName() {
		return "Pedidos";
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#isPriority()
	 */
	@Override
	public boolean isPriority() {
		return false;
	}

}
