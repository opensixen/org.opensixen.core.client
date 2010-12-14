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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.compiere.apps.search.Info_Column;
import org.compiere.model.MDocType;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.opensixen.model.ColumnDefinition;
import org.opensixen.model.POFactory;
import org.opensixen.model.QParam;
import org.opensixen.model.X_C_Order_Header_v;

/**
 * 
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class OrderTableModel extends POTableModel {		
	
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
	protected PO[] getModel() {
		return getOrders(getQuery());
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
		ArrayList<QParam> params = new ArrayList<QParam>();
		//params.add(new QParam(X_C_Order_Header_v.COLUMNNAME_IsSOTrx, isSoTrx()));
		params.add(new QParam(X_C_Order_Header_v.COLUMNNAME_AD_Client_ID, Env.getAD_Client_ID(ctx)));
		int AD_Org_ID = Env.getAD_Org_ID(ctx); 

		if (AD_Org_ID != 0)	{
			params.add(new QParam(X_C_Order_Header_v.COLUMNNAME_AD_Org_ID, AD_Org_ID));
		}
		
		if (query != null)	{
			//String where = query.getWhereClause(false);
			String where = getWhere(query);
			if (!where.equals("()") && where.length() > 0)	{				
				params.add(new QParam(where));
			}
		}		
		
		QParam[] qp = null;
		if (params.size() > 0)	{
			qp = params.toArray(new QParam[params.size()]);
		}
		
		String[] order = {X_C_Order_Header_v.COLUMNNAME_DateOrdered + " desc", X_C_Order_Header_v.COLUMNNAME_C_BPartner_ID};		
		
		List<X_C_Order_Header_v> list = POFactory.getList(ctx, X_C_Order_Header_v.class, qp, order, null);
		
		if (list == null)	{
			return null;
		}
		
		PO[] o = new PO[list.size()];
		return list.toArray(o);
	}
}
