/**
 * 
 */
package org.opensixen.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MAllocationHdr;
import org.compiere.model.MAllocationLine;
import org.compiere.model.MTable;
import org.compiere.model.PO;

import org.opensixen.model.POFactory;
import org.opensixen.model.QParam;

/**
 * 
 * 
 * @author Eloy Gomez Indeos Consultoria http://www.indeos.es
 * 
 */
public class FactRelation {

	public static final int C_Invoice=318;
	public static final int C_Payment=335;
	public static final int C_AllocationHdr=735;
	public static final int C_AllocationLine=390;
	
	
	
	private static HashMap<Integer, Integer[]> relations = setupFactRelations();

	public static List<TableRecordPair> getRelated(Properties ctx, int AD_Table_ID, int record_ID) {
		ArrayList<TableRecordPair> relatedList = new ArrayList<TableRecordPair>();
		List<Integer> related = getRelatedTables(AD_Table_ID);

		// If not related tables, then return a clean list
		if (related == null)	{
			return null;
		}
		
		for (Integer table_ID : related) {
			TableRecordPair record = getTableRecordPair(ctx, AD_Table_ID, table_ID, record_ID);
			if (record != null) {
				relatedList.add(record);

				// Get Child tables
				for (Integer childTable_ID : getRelatedTables(table_ID)) {

					if (!related.contains(childTable_ID) && AD_Table_ID != childTable_ID) {
						TableRecordPair childRecord = getTableRecordPair(ctx, table_ID, childTable_ID, record.getRecord_ID());
						if (childRecord != null)	{
							relatedList.add(childRecord);
						}
					}
				}
			}
		}

		return relatedList;
	}

	/**
	 * Get direct relation
	 * @param sourceTable_ID
	 * @param sourceRecord_ID
	 * @return
	 */
	private static TableRecordPair getTableRecordPair(Properties ctx, int sourceTable_ID, int relatedTable_ID, int sourceRecord_ID)		{
		// Special relations:
		if (relatedTable_ID == C_AllocationHdr || sourceTable_ID == C_AllocationHdr)	{
			return getAllocationTableRecordPair(ctx, sourceTable_ID, relatedTable_ID, sourceRecord_ID);
		}
		
		// get Record ID for relatedTable
		String mainRecordTableName = MTable.getTableName(ctx, sourceTable_ID);
		String tableName = MTable.getTableName(ctx, relatedTable_ID);
		Class<PO> clazz = (Class<PO>) MTable.getClass(tableName);
		PO relatedPO = POFactory.get(clazz, new QParam(mainRecordTableName	+ "_ID", sourceRecord_ID));
		
		if (relatedPO == null) {
			return null;			
		}
		return new TableRecordPair(relatedTable_ID, relatedPO.get_ID());

	}
	
	private static TableRecordPair getAllocationTableRecordPair(Properties ctx, int sourceTable_ID, int relatedTable_ID, int sourceRecord_ID)		{
		if (relatedTable_ID == C_AllocationHdr)	{
			// get Record ID for relatedTable
			String mainRecordTableName = MTable.getTableName(ctx, sourceTable_ID);
			QParam[] params = {new QParam(mainRecordTableName	+ "_ID", sourceRecord_ID)};
			
			List<MAllocationLine> lines = POFactory.getList(ctx, MAllocationLine.class, params);
			for(MAllocationLine line:lines)	{
				return new TableRecordPair(C_AllocationHdr, line.getC_AllocationHdr_ID());
			}			
		}
		
		return null;
	}
	
	
	private static List<Integer> getRelatedTables(int AD_Table_ID) {
		ArrayList<Integer> list = new ArrayList<Integer>();

		if (!relations.containsKey(AD_Table_ID)) {
			return list;
		}

		for (Integer rel : relations.get(AD_Table_ID)) {
			list.add(rel);
		}
		return list;
	}

	/**
	 * Create relations between fact_acct tables
	 * 
	 * @return
	 */
	private static  HashMap<Integer, Integer[]> setupFactRelations() {
		HashMap<Integer, Integer[]> relations = new HashMap<Integer, Integer[]>();

		// C_Invoice 318 have payments
		Integer[] rInovice = { C_Payment };
		relations.put(C_Invoice, rInovice);

		// C_Payment 335 have invoices and allocations
		Integer[] rPayment = { C_Invoice, C_AllocationHdr };
		relations.put(C_Payment, rPayment);
		return relations;
	}

}

class TableRecordPair {
	private int AD_Table_ID;

	private int Record_ID;

	public TableRecordPair(int aD_Table_ID, int record_ID) {
		super();
		AD_Table_ID = aD_Table_ID;
		Record_ID = record_ID;
	}

	/**
	 * @return the aD_Table_ID
	 */
	public int getAD_Table_ID() {
		return AD_Table_ID;
	}

	/**
	 * @param aD_Table_ID
	 *            the aD_Table_ID to set
	 */
	public void setAD_Table_ID(int aD_Table_ID) {
		AD_Table_ID = aD_Table_ID;
	}

	/**
	 * @return the record_ID
	 */
	public int getRecord_ID() {
		return Record_ID;
	}

	/**
	 * @param record_ID
	 *            the record_ID to set
	 */
	public void setRecord_ID(int record_ID) {
		Record_ID = record_ID;
	}

}