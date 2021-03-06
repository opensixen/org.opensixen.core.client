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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.compiere.grid.GridController;
import org.compiere.model.GridTab;
import org.compiere.model.MQuery;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTable;
import org.compiere.util.CLogger;
import org.opensixen.osgi.interfaces.IHelperContentProvider;

/**
 * Allow creation of single tab HelperContent
 * 
 * @author Eloy Gomez
 *
 */
public abstract class AbstractTableHelperContentProvider implements IHelperContentProvider, MouseListener {
	
	public static final String COMMAND_SELECT_RECORD="cmd_select_record";
	
	protected CLogger log = CLogger.getCLogger(getClass());
	
	protected OTable table;
	
	protected Properties ctx;

	public abstract TableModel getTableModel(MQuery query);
	public abstract void initTable();
	
	public abstract int getPosition();
	public abstract String getTabName();
	public abstract boolean isPriority();
	
	private EPanel panel;
	protected GridTab gc;
	/** Indice entre el indice en la tabla y el id del registro	*/ 
	private HashMap<Integer, Integer> indexMap;
	
	protected ArrayList<Integer> selecteds = new ArrayList<Integer>();
	private String keyColumn; 
	
	/**
	 * Inicializamos la tabla con el MQuery del GridController
	 * y añadimos los listeners
	 */
	@Override
	public HelperContentPanel[] getPanels (Properties ctx, EPanel panel) {
		HelperContentPanel parent = new HelperContentPanel();
		parent.setLayout(new BorderLayout(5, 5));
		
		// Setup single tab
		parent.setPosition(getPosition());
		parent.setTabName(getTabName());
		parent.setPriority(isPriority());
		
		this.ctx = ctx;
		this.panel = panel;
		this.gc = panel.getCurrentTab();
		this.keyColumn = gc.getKeyColumnName();
		
		table = new OTable(ctx);
		table.setModel(getTableModel(gc.getQuery()));
		
		table.setupTable();
		
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.autoSize(true);
		CScrollPane scrollPane = new CScrollPane(table);
		parent.add( scrollPane, BorderLayout.CENTER );
		table.packAll();

		table.addMouseListener(this);
		// Custom table options
		initTable();
		
		// Return panels
		HelperContentPanel[] panels = {parent};
		return panels;
		
	}
	
	private void reindex()	{
		indexMap = new HashMap<Integer, Integer>();
		for (int index=0; index < gc.getRowCount(); index++)	{
			Integer id = (Integer) gc.getValue(index, keyColumn);
			indexMap.put(id, index);
		}
	}
	
	protected void selectRecord(int record_ID)	{				
		// Si lo hemos seleccionado antes no lo añadimos a la consulta
		if (!selecteds.contains(record_ID))	{
			MQuery current = gc.getQuery();
			current.addRestriction(keyColumn + "=" +record_ID, false, 0);
			gc.setQuery(current);
			selecteds.add(record_ID);
			gc.query(false);
			reindex();
		}		
		
		Integer current = indexMap.get(record_ID);
		if (current == null)	{
			log.severe("Record out of index: " + record_ID);
		}
		
		gc.setCurrentRow(current);
		gc.dataRefresh();
		
		// Fire event
		panel.fireActionPerformed(new ActionEvent(this, record_ID, COMMAND_SELECT_RECORD ));
	}
	
	/**
	 * @return the panel
	 */
	public EPanel getPanel() {
		return panel;
	}
	
	/**
	 * 
	 * @see javax.swing.JTable#updateUI()
	 */
	public void updateUI() {
		table.updateUI();
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
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
