/**
 * 
 */
package org.opensixen.swing;

import java.math.BigDecimal;
import java.util.Properties;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.compiere.grid.VTable;
import org.compiere.grid.ed.VCellRenderer;
import org.compiere.grid.ed.VHeaderRenderer;
import org.compiere.util.DisplayType;
import org.opensixen.interfaces.OTableModel;
import org.opensixen.model.ColumnDefinition;

/**
 * @author harlock
 *
 */
public class OTable extends VTable {

	private static final long serialVersionUID = 1L;
	private Properties ctx;
	
	
	public OTable(Properties ctx)	{
		this.ctx = ctx;
	}
	
	public Properties getCtx()	{
		return ctx;
	}
	
	public int setupTable() {
		OTableModel tableModel = (OTableModel) getModel();
		 ColumnDefinition[] columnDefinitions = tableModel.getColumnDefinitions();
		
		if (columnDefinitions == null) {
			return -1;
		}

		int size = columnDefinitions.length;
		TableColumnModel tcm = getColumnModel();
		if (size != tcm.getColumnCount())
			throw new IllegalStateException("TableColumn Size <> TableModel");

		for (int i=0; i < columnDefinitions.length; i++)	{
			ColumnDefinition definition = columnDefinitions[i];
			TableColumn tc = tcm.getColumn(i);
			tc.setMinWidth(30);
			int displayType = definition.getDisplayType();
			
			// Si no hay displayType, establecemos String
			if (displayType == -1)	{
				displayType = DisplayType.String;
			}
			
			tc.setCellRenderer(new OCellRenderer(displayType));
			// TODO : CellEditor
			
			tc.setHeaderValue(definition.getTitle());
			tc.setHeaderRenderer(new VHeaderRenderer(displayType));
		}
		
				
		
		return size;
	}
	
	/* (non-Javadoc)
	 * @see org.compiere.grid.VTable#getColorCode(int)
	 */
	@Override
	public int getColorCode(int row) {
		return 1;
	}
	
}


