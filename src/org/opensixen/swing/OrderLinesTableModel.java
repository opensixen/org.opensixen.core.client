/**
 * 
 */
package org.opensixen.swing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.opensixen.model.ColumnDefinition;
import org.opensixen.model.GroupDefinition;
import org.opensixen.model.GroupVariable;
import org.opensixen.model.I_V_Fact_Acct;
import org.opensixen.model.POFactory;
import org.opensixen.model.QParam;
import org.opensixen.model.X_C_Order_Header_v;
import org.opensixen.model.X_C_Order_LineTax_v;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class OrderLinesTableModel extends POTableModel {

	//PO[] lines;
	
	private int C_Order_ID;
	
	/**
	 * @param ctx
	 * @param query
	 */
	public OrderLinesTableModel(Properties ctx, MQuery query) {
		super(ctx, query);
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.POTableModel#getModel(org.compiere.model.MQuery)
	 */
	@Override
	protected PO[] getModel(MQuery query) {				
		return getLines();
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableModel#getInfoColumns()
	 */
	@Override
	public ColumnDefinition[] getColumnDefinitions() {
		ColumnDefinition[] columns = { 
				new ColumnDefinition(X_C_Order_LineTax_v.COLUMNNAME_Name, "Producto", String.class),
				new ColumnDefinition(X_C_Order_LineTax_v.COLUMNNAME_QtyOrdered, "Cantidad", BigDecimal.class),
				new ColumnDefinition(X_C_Order_LineTax_v.COLUMNNAME_LineNetAmt, "Neto", BigDecimal.class)		
		};
		
		return columns;
	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opensixen.report.AbstractDynamicJasperReport#getGroupDefinitions()
	 */
	@Override
	protected List<GroupDefinition> getGroupDefinitions() {
		ArrayList<GroupDefinition> definitions = new ArrayList<GroupDefinition>();

		// Agrupamos por numero de asiento
		GroupDefinition def = new GroupDefinition();
		String[] columns = { X_C_Order_LineTax_v.COLUMNNAME_C_Order_ID };
		GroupVariable[] footer = {
				new GroupVariable(X_C_Order_LineTax_v.COLUMNNAME_LineNetAmt, GroupVariable.SUM)
		};
		
		def.setGroupColumns(columns);
		def.setFooterVariables(footer);
		definitions.add(def);
		return definitions;
	}

	
	private PO[] getLines()	{
		
		QParam[] params = {new QParam(X_C_Order_LineTax_v.COLUMNNAME_C_Order_ID, C_Order_ID),
				new QParam(X_C_Order_LineTax_v.COLUMNNAME_C_OrderLine_ID, QParam.OPER_BIG, 0)
		};
		
		String[] order = {"line"};
		
		List<X_C_Order_LineTax_v> l = POFactory.getList(ctx, X_C_Order_LineTax_v.class, params);
		
		if (l == null)	{
			return null;
		}
		
		PO[] a = l.toArray(new PO[l.size()]);
		return a;
	}

	/**
	 * @return the c_Order_ID
	 */
	public int getC_Order_ID() {
		return C_Order_ID;
	}

	/**
	 * @param c_Order_ID the c_Order_ID to set
	 */
	public void setC_Order_ID(int c_Order_ID) {
		C_Order_ID = c_Order_ID;
	}
	
	
	
}
