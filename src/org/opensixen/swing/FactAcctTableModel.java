/**
 * 
 */
package org.opensixen.swing;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MFactAcct;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.opensixen.model.ColumnDefinition;
import org.opensixen.model.GroupDefinition;
import org.opensixen.model.GroupVariable;
import org.opensixen.model.I_V_Fact_Acct;
import org.opensixen.model.MVFactAcct;
import org.opensixen.model.POFactory;
import org.opensixen.model.QParam;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class FactAcctTableModel extends POTableModel {
	
	List<MVFactAcct> list;
	private CLogger log = CLogger.getCLogger(getClass());
	
	/**
	 * @param ctx
	 * @param query
	 */
	public FactAcctTableModel(Properties ctx, MQuery query) {
		super(ctx, query);
	}

	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.OTableModel#getColumnDefinitions()
	 */
	@Override
	public ColumnDefinition[] getColumnDefinitions() {
		ColumnDefinition[] cols = {
				//new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_JournalNo, "Asiento", Integer.class),
				//new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_DateAcct, "Fecha", Timestamp.class ),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Value, "Cuenta" ,  String.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Name, "Nombre", String.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Description, "Descripcion", String.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Debe, "Debe", BigDecimal.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Haber, "Haber", BigDecimal.class) };
		return cols;
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
		String[] columns = { I_V_Fact_Acct.COLUMNNAME_JournalNo };
		GroupVariable[] footer = {
				new GroupVariable(I_V_Fact_Acct.COLUMNNAME_Debe, GroupVariable.SUM),
				new GroupVariable(I_V_Fact_Acct.COLUMNNAME_Haber,GroupVariable.SUM) };
		def.setGroupColumns(columns);
		def.setFooterVariables(footer);
		definitions.add(def);
		return definitions;
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.POTableModel#getModel(org.compiere.model.MQuery)
	 */
	@Override
	protected PO[] getModel() {		
		
		// Obtenemos el asiento correspondiente al documento.
		QParam[] params = {new QParam(getQuery().getWhereClause(false))};
		list = POFactory.getList(ctx, MVFactAcct.class, params);
		if (list == null)	{
			return new PO[0];
		}						
		return list.toArray(new MVFactAcct[list.size()]);
	}
	
	public String getTableDescription()	{
		if (isEmpty())	{
			log.severe("Not initialized or not records");
			return null;
		}
		
		PO po = list.get(0);
		return po.get_ValueAsString(MVFactAcct.COLUMNNAME_TR_TableName);				
	}
	
	public int getJournalNO()	{
		if (isEmpty())	{
			log.severe("Not initialized or not records");
			return 0;
		}
		
		PO po = list.get(0);
		return po.get_ValueAsInt(MVFactAcct.COLUMNNAME_JournalNo);
	}

	
	public Timestamp getDateAcct()	{
		if (isEmpty())	{
			log.severe("Not initialized or not records");
			return null;
		}
		
		PO po = list.get(0);
		return (Timestamp) po.get_Value(MVFactAcct.COLUMNNAME_DateAcct);
	}
	
	
	public int getDocumentNO()	{
		if (isEmpty())	{
			log.severe("Not initialized or not records");
			return 0;
		}
		
		PO po = list.get(0);
		return po.get_ValueAsInt(MVFactAcct.COLUMNNAME_DocumentNo);
	}

	
	public boolean isEmpty()	{
		if (list== null || list.size() == 0 )	{
			return true;
		}
		return false;
	}
	
	
}
