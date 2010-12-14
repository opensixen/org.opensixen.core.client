 /******* BEGIN LICENSE BLOCK *****
 * Versión: GPL 2.0/CDDL 1.0/EPL 1.0
 *
 * Los contenidos de este fichero están sujetos a la Licencia
 * Pública General de GNU versión 2.0 (la "Licencia"); no podrá
 * usar este fichero, excepto bajo las condiciones que otorga dicha 
 * Licencia y siempre de acuerdo con el contenido de la presente. 
 * Una copia completa de las condiciones de de dicha licencia,
 * traducida en castellano, deberá estar incluida con el presente
 * programa.
 * 
 * Adicionalmente, puede obtener una copia de la licencia en
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Este fichero es parte del programa opensiXen.
 *
 * OpensiXen es software libre: se puede usar, redistribuir, o
 * modificar; pero siempre bajo los términos de la Licencia 
 * Pública General de GNU, tal y como es publicada por la Free 
 * Software Foundation en su versión 2.0, o a su elección, en 
 * cualquier versión posterior.
 *
 * Este programa se distribuye con la esperanza de que sea útil,
 * pero SIN GARANTÍA ALGUNA; ni siquiera la garantía implícita 
 * MERCANTIL o de APTITUD PARA UN PROPÓSITO DETERMINADO. Consulte 
 * los detalles de la Licencia Pública General GNU para obtener una
 * información más detallada. 
 *
 * TODO EL CÓDIGO PUBLICADO JUNTO CON ESTE FICHERO FORMA PARTE DEL 
 * PROYECTO OPENSIXEN, PUDIENDO O NO ESTAR GOBERNADO POR ESTE MISMO
 * TIPO DE LICENCIA O UNA VARIANTE DE LA MISMA.
 *
 * El desarrollador/es inicial/es del código es
 *  FUNDESLE (Fundación para el desarrollo del Software Libre Empresarial).
 *  Indeos Consultoria S.L. - http://www.indeos.es
 *
 * Contribuyente(s):
 *  Eloy Gómez García <eloy@opensixen.org> 
 *
 * Alternativamente, y a elección del usuario, los contenidos de este
 * fichero podrán ser usados bajo los términos de la Licencia Común del
 * Desarrollo y la Distribución (CDDL) versión 1.0 o posterior; o bajo
 * los términos de la Licencia Pública Eclipse (EPL) versión 1.0. Una 
 * copia completa de las condiciones de dichas licencias, traducida en 
 * castellano, deberán de estar incluidas con el presente programa.
 * Adicionalmente, es posible obtener una copia original de dichas 
 * licencias en su versión original en
 *  http://www.opensource.org/licenses/cddl1.php  y en  
 *  http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * Si el usuario desea el uso de SU versión modificada de este fichero 
 * sólo bajo los términos de una o más de las licencias, y no bajo los 
 * de las otra/s, puede indicar su decisión borrando las menciones a la/s
 * licencia/s sobrantes o no utilizadas por SU versión modificada.
 *
 * Si la presente licencia triple se mantiene íntegra, cualquier usuario 
 * puede utilizar este fichero bajo cualquiera de las tres licencias que 
 * lo gobiernan,  GPL 2.0/CDDL 1.0/EPL 1.0.
 *
 * ***** END LICENSE BLOCK ***** */

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