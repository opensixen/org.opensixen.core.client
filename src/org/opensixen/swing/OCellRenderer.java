/**
 * 
 */
package org.opensixen.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Label;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.compiere.grid.ed.VCellRenderer;
import org.compiere.model.GridField;

import com.lowagie.text.Font;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class OCellRenderer extends VCellRenderer {

	private Color colorA = new Color(255, 235, 235);
	private Color colorB = new Color(255, 253, 253);
	
	
	public OCellRenderer(GridField mField) {
		super(mField);
	}

	public OCellRenderer(int displayType) {
		super(displayType);
	}

	/* (non-Javadoc)
	 * @see org.compiere.grid.ed.VCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		// TODO Auto-generated method stub
		Component component =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus,	row, col);

		
		// Setup Background Color		
		//component.setOpaque(true);
		int r = row%2;		
		if (r==0)	{
			component.setBackground(colorA);
		} 	else {
			component.setBackground(colorB);
		}
		
		TableModel tableModel = table.getModel();
		
		if (tableModel instanceof POTableModel)	{
			POTableModel potm = (POTableModel) tableModel;

			// If group variable, set bold
			if (potm.isGroupVariableRow(row))	{
				component.setFont(component.getFont().deriveFont(Font.BOLD));
			}
		}
		return component;
					
	}

	
	
	
}
