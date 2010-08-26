/**
 * 
 */
package org.opensixen.swing;

import java.util.Properties;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.compiere.apps.search.Info_Column;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.opensixen.interfaces.OTableModel;
import org.opensixen.model.ColumnDefinition;

/**
 * @author harlock
 *
 */
public abstract class POTableModel implements OTableModel {

	protected Properties ctx;
	private PO[] model;
	private ColumnDefinition[] columnDefinitions;
	private MQuery query;
	
	public POTableModel(Properties ctx, MQuery query) {
		super();
		this.ctx = ctx;
		this.query = query;
		this.model = getModel(query);
		this.columnDefinitions =  getColumnDefinitions();
	}

	
	public void setQuery(MQuery query)	{
		this.query = query;
		reload();
	}
	
	public void reload()	{
		this.model = null;
		this.model = getModel(query);
	}
	
	public static String getWhere(MQuery query)	{
		String where = query.getWhereClause(false);
		
		// A veces se olvida de no hacer qualifed...
		
		where = where.replaceAll(query.getTableName()+".", "");
		return where;
	}
	
	
	protected abstract PO[] getModel(MQuery query);

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (model == null)	{
			return 0;
		}
		return model.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnDefinitions.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return columnDefinitions[columnIndex].getTitle();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnDefinitions[columnIndex].getClazz();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PO po = model[rowIndex];
		return po.get_Value(columnDefinitions[columnIndex].getName());
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub		
	}
	
}
