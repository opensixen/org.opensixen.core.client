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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.compiere.apps.ADialog;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.jdesktop.swingx.JXTaskPane;
import org.opensixen.p2.IUnitModel;
import org.opensixen.p2.P2;
import org.opensixen.p2.P2Updater;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class InstallForm extends CDialog {

	private BasicEventList<IUnitModel> eventList;
	private Properties ctx;
	
	/**
	 * Descripción de campos
	 */

	private CButton installBtn;
	
	private CLabel locationLabel = new CLabel();;
	private CComboBox locationsCombo;
	
	private JTable table;
	
	private CButton repositoryBtn;
	private CButton updateBtn;
	
	//Paneles
	
	private JXTaskPane topPanel = new JXTaskPane();
	
	
	public InstallForm(Properties ctx)	{
		this.ctx = ctx;
		jbInit();
		load();
		//autoSize();
		pack();
	}
	
	
	private void jbInit()	{
		setLayout(new BorderLayout(10, 10));
		
		topPanel.setTitle(Msg.translate(Env.getCtx(), "Repositories"));
		topPanel.setLayout(new GridBagLayout());
		
		add(topPanel, BorderLayout.NORTH);
		

		locationsCombo = new CComboBox(P2.get().getRepositories());
		locationsCombo.addActionListener(this);
		locationLabel.setText(Msg.translate(Env.getCtx(), "Repositories"));
		locationLabel.setLabelFor(locationsCombo);
		
		topPanel.add( locationLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 2,2,2,2 ),0,0 ));
		topPanel.add( locationsCombo,new GridBagConstraints( 1,0,1,1,0.3,0.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets( 2,2,2,20 ),0,0 ));

		
		repositoryBtn = new CButton(Msg.getMsg(ctx, "Manage Repositories"));
		repositoryBtn.addActionListener(this);

		topPanel.add( repositoryBtn,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 10,1,2,100 ),0,0 ));

		
		CPanel treePanel = new CPanel();
		add(treePanel, BorderLayout.CENTER);
		
		
		// GlazzedList
		eventList = new BasicEventList<IUnitModel>();
		FilterList<IUnitModel> filterList = new FilterList<IUnitModel>(eventList);
		EventTableModel<IUnitModel> tableModel = new  EventTableModel<IUnitModel>(filterList, new IUnitTableFormat());
		table = new JTable(tableModel);
		
		JScrollPane scrollPane = new JScrollPane(table);
		treePanel.add(scrollPane);
		
		
		CPanel btnPanel = new CPanel();
		add(btnPanel, BorderLayout.SOUTH);
		
		installBtn = new CButton(Msg.getMsg(Env.getAD_Language(ctx), "Install"));
		installBtn.addActionListener(this);
		btnPanel.add(installBtn);
		
		
		updateBtn = new CButton(Msg.getMsg(Env.getAD_Language(ctx), "Update"));
		updateBtn.addActionListener(this);
		btnPanel.add(updateBtn);
	}


	private void load()	{
		URI location = (URI) locationsCombo.getSelectedItem();
		eventList.clear();
		eventList.addAll(P2.get().getAllIUnit(location));		
	}

	
	private void install()	{
		ArrayList<IUnitModel> iunits = new ArrayList<IUnitModel>();
		for (int i=0; i < eventList.size(); i++)	{
			IUnitModel iunit = eventList.get(i);
			if (iunit.isSelected())	{
				iunits.add(iunit);
			}
		}
		
		if (P2.get().install(iunits))	{
			ADialog.info(0, this, Msg.getMsg(Env.getAD_Language(ctx), "OK"));
		}
		else {
			ADialog.error(0, this, Msg.getMsg(Env.getAD_Language(ctx), "Updates are not correctly installed "));
		}
	}
	
	
	private void update()	{
		P2Updater.startupUpdater();
		ADialog.info(0, this, Msg.getMsg(Env.getAD_Language(ctx), "OK"));
	}
	
	/**
	 * Actualiza el combobox de repositorios
	 */
	
	public void refresh(){
		//Eliminamos los items actuales y la posibilidad de que salte el evento
		locationsCombo.removeActionListener(this);
		locationsCombo.removeAllItems();
		
		//Añadimos los repositorios actualizados
		for(URI uri :P2.get().getRepositories()){
			locationsCombo.addItem(uri);
		}
	
		locationsCombo.addActionListener(this);
	}

	/* (non-Javadoc)
	 * @see org.compiere.swing.CDialog#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(installBtn))	{
			install();			
		}
		
		if (e.getSource().equals(repositoryBtn))	{
			ManageRepositories manageRepositories = new ManageRepositories(ctx, this);
			manageRepositories.setVisible(true);
		}
		
		if (e.getSource().equals(locationsCombo))	{
			load();
		}
		
		if (e.getSource().equals(updateBtn))	{
			update();
		}
	}
	
	
}

class IUnitTableFormat implements AdvancedTableFormat<IUnitModel>, WritableTableFormat<IUnitModel>	{

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.TableFormat#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.TableFormat#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "";
		case 1:
			return "ID";
		case 2:
			return "Descripcion";
		default:
			throw new RuntimeException("Imposible column index: " + column);
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.TableFormat#getColumnValue(java.lang.Object, int)
	 */
	@Override
	public Object getColumnValue(IUnitModel iunit, int column) {
		switch (column) {
		case 0:
			return new Boolean(iunit.isSelected());
		case 1:
			return iunit.getName();
		case 2:
			return iunit.getDescription();
		default:
			throw new RuntimeException("Imposible column index: " + column);
		}

	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnClass(int)
	 */
	@Override
	public Class getColumnClass(int column) {
		switch (column) {
		case 0:
			return Boolean.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		default:
			throw new RuntimeException("Imposible column index: " + column);
		}

	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.AdvancedTableFormat#getColumnComparator(int)
	 */
	@Override
	public Comparator getColumnComparator(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.WritableTableFormat#isEditable(java.lang.Object, int)
	 */
	@Override
	public boolean isEditable(IUnitModel baseObject, int column) {
		if (column != 0)	{
			return false;			
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see ca.odell.glazedlists.gui.WritableTableFormat#setColumnValue(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public IUnitModel setColumnValue(IUnitModel baseObject, Object editedValue, int column) {
		if (column != 0)	{	
			return null;
		}
		
		Boolean value = (Boolean) editedValue;
		baseObject.setSelected(value);
		return baseObject;
	}
	
}
