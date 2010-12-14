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

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.compiere.model.GridTab;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.opensixen.model.X_C_Order_Header_v;



/**
 * 
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class OrderHelperContentProvider extends AbstractTableHelperContentProvider {

	private OrderTableModel model;

	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getTableModel()
	 */
	@Override
	public TableModel getTableModel(MQuery query) {
		GridTab gt = getPanel().getGridController().getMTab();
		query.addRestriction(X_C_Order_Header_v.COLUMNNAME_IsSOTrx, MQuery.EQUAL, Env.isSOTrx(ctx, gt.getWindowNo()));	
		
		model = new OrderTableModel(ctx, query);
		
		
		//model.setSoTrx();		
		
		return model;
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#initTable()
	 */
	@Override
	public void initTable() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.IPanelListener#disposePerformed()
	 */
	@Override
	public boolean disposePerformed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.opensixen.swing.IPanelListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		if (cmd.equals("cmd_select_record"))	{
			return;
		}
		
		if (cmd.equals("Find"))	{
			GridTab gt = getPanel().getGridController().getMTab();
			MQuery query = gt.getQuery();
			String filter = query.getWhereClause(false);
			if (filter.equals("()"))	{
				return;
			}
									
			model.setQuery(query);
			System.out.println("Filter: " + filter);
		}
		
		model.reload();
		updateUI();
		
		
	}

	
	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// Obtenemos el registro sobre el que pulso
		int index = table.getSelectedRow();
		if (index == -1)	{
			return;
		}

		// Cargamos el modelo
		//PO[] o = model.getModel(null);
		 
		X_C_Order_Header_v	 order = (X_C_Order_Header_v) model.getValueAt(index);
		if (order == null)	{		
			return;
		}
		
		selectRecord(order.getC_Order_ID());
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getPosition()
	 */
	@Override
	public int getPosition() {
		return HelperContentPanel.POSITION_TOP;
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#getTabName()
	 */
	@Override
	public String getTabName() {
		return "Pedidos";
	}



	/* (non-Javadoc)
	 * @see org.opensixen.swing.AbstractTableHelperContentProvider#isPriority()
	 */
	@Override
	public boolean isPriority() {
		return false;
	}

}
