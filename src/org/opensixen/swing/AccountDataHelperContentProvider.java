/**
 * 
 */
package org.opensixen.swing;

import javax.swing.table.TableModel;

import org.compiere.model.MQuery;
import org.compiere.util.DisplayType;
import org.opensixen.interfaces.IBeanProvider;
import org.opensixen.model.AccountDataBeanProvider;
import org.opensixen.model.ColumnDefinition;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class AccountDataHelperContentProvider extends AbstractTableHelperContentProvider {

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getTableModel(org.compiere.model.MQuery)
	 */
	@Override
	public TableModel getTableModel(MQuery query) {
		IBeanProvider beanProvider = new AccountDataBeanProvider(ctx);
		BeanTableModel tableModel = new BeanTableModel(beanProvider, getColumnDefinitions());	
		return tableModel;
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#initTable()
	 */
	@Override
	public void initTable() {
		// TODO Auto-generated method stub
		
	}
	
	public ColumnDefinition[] getColumnDefinitions() {
		ColumnDefinition[] cols = {
				new ColumnDefinition("value", "Value", 80, DisplayType.String),
				new ColumnDefinition("name", "Nombre", 180, DisplayType.String),				
				new ColumnDefinition("haber", "Haber", 150, DisplayType.Amount) ,
				new ColumnDefinition("debe", "Debe", 150, DisplayType.Amount),
				new ColumnDefinition("saldoInicial", "Inicial", 150, DisplayType.Amount) };
		return cols;
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
		return "Cuentas";
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#isPriority()
	 */
	@Override
	public boolean isPriority() {
		// TODO Auto-generated method stub
		return false;
	}

}
