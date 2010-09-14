/**
 * 
 */
package org.opensixen.swing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.compiere.apps.search.Info_Column;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.opensixen.model.ColumnDefinition;
import org.opensixen.model.POFactory;
import org.opensixen.model.QParam;
import org.opensixen.model.X_C_Order_Header_v;

/**
 * @author harlock
 *
 */
public class OrderTableModel extends POTableModel {

	PO[] orders;
	
	/**
	 * @param ctx
	 */
	public OrderTableModel(Properties ctx, MQuery query) {
		super(ctx, query);
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableModel#getModel()
	 */
	@Override
	protected PO[] getModel(MQuery query) {
			
		if (orders == null)	{
			orders = getOrders(query);			
		}
		
		return orders;
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableModel#getInfoColumns()
	 */
	@Override
	public ColumnDefinition[] getColumnDefinitions() {
		ColumnDefinition[] columns = {
				//new ColumnDefinition(X_C_Order_Header_v.COLUMNNAME_C_Order_ID, "ID", Integer.class), 
				new ColumnDefinition(X_C_Order_Header_v.COLUMNNAME_DateOrdered, "Fecha", Timestamp.class),
				new ColumnDefinition(X_C_Order_Header_v.COLUMNNAME_Name, "Entidad", String.class),
				new ColumnDefinition( X_C_Order_Header_v.COLUMNNAME_DocumentType, "Tipo",  String.class),
				new ColumnDefinition(X_C_Order_Header_v.COLUMNNAME_TotalLines, "Neto", BigDecimal.class)		
		};
		
		return columns;
	
	}

	/**
	 * Devuelve un PO[] con los pedidos;
	 * @param query
	 * @return
	 */
	private PO[] getOrders(MQuery query)	{
		String where = query.getWhereClause(false);
		ArrayList<QParam> params = new ArrayList<QParam>();
		params.add(new QParam(X_C_Order_Header_v.COLUMNNAME_IsSOTrx, true));
		params.add(new QParam(X_C_Order_Header_v.COLUMNNAME_AD_Client_ID, Env.getAD_Client_ID(ctx)));
		int AD_Org_ID = Env.getAD_Org_ID(ctx); 

		if (AD_Org_ID != 0)	{
			params.add(new QParam(X_C_Order_Header_v.COLUMNNAME_AD_Org_ID, AD_Org_ID));
		}
		
		if (!where.equals("()"))	{
			where = getWhere(query);
			params.add(new QParam(where));
		}
		
		
		QParam[] qp = null;
		if (params.size() > 0)	{
			qp = params.toArray(new QParam[params.size()]);
		}
		
		List<X_C_Order_Header_v> list = POFactory.getList(ctx, X_C_Order_Header_v.class, qp);
		
		if (list == null)	{
			return null;
		}
		
		PO[] o = new PO[list.size()];
		return list.toArray(o);
	}
	
}
