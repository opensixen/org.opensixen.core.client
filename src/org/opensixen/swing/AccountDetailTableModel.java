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
public class AccountDetailTableModel extends POTableModel {

	private int C_ElementValue_ID;
	private Timestamp from, to;
	
	//private Properties ctx;
	
	/**
	 * @param ctx
	 */
	public AccountDetailTableModel(Properties ctx, int C_ElementValue_ID, Timestamp from, Timestamp to) {
		super(ctx);
		
		this.C_ElementValue_ID = C_ElementValue_ID;
		this.from = from;
		this.to = to;
		
		initTableModel();		
	}

	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.OTableModel#getColumnDefinitions()
	 */
	@Override
	public ColumnDefinition[] getColumnDefinitions() {
		ColumnDefinition[] cols = {				
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_DateAcct, "Fecha", Timestamp.class ),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_JournalNo, "Asiento", Integer.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Value, "Cuenta" ,  String.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Name, "Nombre", String.class),
				//new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_Description, "Descripcion", String.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_TR_TableName, "Documento", String.class),
				new ColumnDefinition(I_V_Fact_Acct.COLUMNNAME_DocumentNo, "Doc. No", String.class),
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
		String[] columns = { I_V_Fact_Acct.COLUMNNAME_C_ElementValue_ID };
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
		QParam[] params = { new QParam(MVFactAcct.COLUMNNAME_C_ElementValue_ID, C_ElementValue_ID)		};
		String[] order = {MVFactAcct.COLUMNNAME_DateAcct + " desc", MVFactAcct.COLUMNNAME_JournalNo + " desc"};
		List<MVFactAcct> list = POFactory.getList(ctx, MVFactAcct.class, params , order, null);		
		
		return list.toArray(new PO[list.size()]);
	}
}
