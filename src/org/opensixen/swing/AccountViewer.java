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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JTable;

import org.compiere.acct.AcctViewer;
import org.compiere.apps.ADialog;
import org.compiere.apps.AEnv;
import org.compiere.model.MQuery;
import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CFrame;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.opensixen.model.I_V_Fact_Acct;
import org.opensixen.model.MVFactAcct;
import org.opensixen.osgi.interfaces.IAccountViewer;

/**
 * 
 * 
 * @author Eloy Gomez Indeos Consultoria http://www.indeos.es
 * 
 */
public class AccountViewer extends CFrame implements IAccountViewer {

	/** Logger */
	private static CLogger log = CLogger.getCLogger(AcctViewer.class);

	private static final long serialVersionUID = 1L;
	private int AD_Client_ID;


	private Properties ctx;

	private SimpleDateFormat df;

	private CButton bRePost;

	private CCheckBox forcePost;

	public AccountViewer() {
		super(Msg.getMsg(Env.getCtx(), "AcctViewer"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		df = new SimpleDateFormat("dd-MM-yy");
	}

	private void jbInit(int AD_Table_ID, int record_ID) {
		CPanel container = new CPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.getContentPane().add(container);

		// Main fact panel
		CPanel mainRecordPanel = getJournalViewer(AD_Table_ID, record_ID);
		if (mainRecordPanel != null) {
			container.add(mainRecordPanel);
		}

		// Get related records
		List<TableRecordPair> related = FactRelation.getRelated(ctx,
				AD_Table_ID, record_ID);

		if (related == null) {
			return;
		}

		// Related Fact Acct (Contra asientos)
		CPanel relatedLabelPanel = new CPanel();
		container.add(relatedLabelPanel);
		CLabel relatedLabel = new CLabel("Contrapartidas");
		relatedLabel.setFontBold(true);
		relatedLabelPanel.setBackground(Color.LIGHT_GRAY);
		relatedLabelPanel.add(relatedLabel);

		for (TableRecordPair record : related) {
			CPanel relatedPanel = getJournalViewer(record.getAD_Table_ID(),
					record.getRecord_ID());
			if (relatedPanel != null) {
				container.add(relatedPanel);
			}
		}

	}

	/**
	 * Create a panel with a Journal (asiento)
	 * @param AD_Table_ID
	 * @param record_ID
	 * @return
	 */
	private CPanel getJournalViewer(final int AD_Table_ID, final int record_ID) {
		CPanel container = new CPanel();
		final FactAcctTableModel tableModel = new FactAcctTableModel(Env.getCtx(),	getQuery(AD_Table_ID, record_ID));
		StringBuffer title = new StringBuffer();
		StringBuffer docDescription = new StringBuffer(); 
		
		if (!tableModel.isEmpty()) {
			title.append("Asiento ").append(tableModel.getJournalNO()).append(" ");
			title.append(df.format(new Date(tableModel.getDateAcct().getTime())));
			
			docDescription.append(tableModel.getTableDescription()).append(" ")	.append(tableModel.getDocumentNO());
		}
		// Create Title String
		
		container.setBorder(BorderFactory.createTitledBorder(title.toString()));
		container.setLayout(new BorderLayout());

		CPanel headerPanel = new CPanel();
		container.add(headerPanel, BorderLayout.NORTH);
		CLabel l = new CLabel();

		l.setText(docDescription.toString());
		l.setFontBold(true);
		headerPanel.add(l);

		// Add the table for main fact_acct
		final OTable table = new OTable(Env.getCtx());

		table.setModel(tableModel);

		table.setupTable();
		
		table.setFillsViewportHeight(true);
		table.setPreferredScrollableViewportSize(new Dimension(800, 80));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.autoSize(true);
		CScrollPane scrollPane = new CScrollPane(table);
		container.add(scrollPane, BorderLayout.CENTER);

		table.packAll();
		
		table.addMouseListener(new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}			
			@Override
			public void mouseExited(MouseEvent e) {}			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)	{
					int index = table.rowAtPoint(e.getPoint());
					MVFactAcct fact = (MVFactAcct) tableModel.getValueAt(index);
					AccountDetailViewer viewer = new AccountDetailViewer(ctx, fact.getC_ElementValue_ID(), fact.getDateAcct(), fact.getDateAcct());
				}
				
			}
		});

		CPanel footerPanel = new CPanel();
		footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		bRePost = new CButton();
		bRePost.setText(Msg.getMsg(Env.getCtx(), "RePost"));
		bRePost.setToolTipText(Msg.getMsg(Env.getCtx(), "RePostInfo"));
		bRePost.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRePost(AD_Table_ID, record_ID);
				tableModel.reload();
				
			}
		});

		forcePost = new CCheckBox();
		forcePost.setText(Msg.getMsg(Env.getCtx(), "Force"));
		forcePost.setToolTipText(Msg.getMsg(Env.getCtx(), "ForceInfo"));

		footerPanel.add(forcePost);
		footerPanel.add(bRePost);
		container.add(footerPanel, BorderLayout.SOUTH);
		return container;

	}

	/**
	 * Generate MQuery with AD_Table_ID and record_ID
	 */
	private MQuery getQuery(int AD_Table_ID, int record_ID) {

		MQuery query = new MQuery(I_V_Fact_Acct.Table_Name);
		query.addRestriction(I_V_Fact_Acct.COLUMNNAME_AD_Client_ID,
				MQuery.EQUAL, AD_Client_ID);
		query.addRestriction("ad_table_id", MQuery.EQUAL, AD_Table_ID);
		query.addRestriction(I_V_Fact_Acct.COLUMNNAME_Record_ID, MQuery.EQUAL,
				record_ID);
		return query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opensixen.osgi.interfaces.IAccountViewer#view(int, int, int)
	 */
	@Override
	public void view(int AD_Client_ID, int AD_Table_ID, int record_ID) {
		this.AD_Client_ID = AD_Client_ID;
		this.ctx = Env.getCtx();

		log.info("AD_Table_ID=" + AD_Table_ID + ", Record_ID=" + record_ID);

		jbInit(AD_Table_ID, record_ID);
		pack();
		setVisible(true);
	}

	/**
	 * RePost Record
	 */
	private void actionRePost(int AD_Table_ID, int record_ID) {
		if (AD_Table_ID != 0
				&& record_ID != 0
				&& ADialog.ask(Env.getWindowNo(getContentPane()), this,
						"PostImmediate?")) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			boolean force = forcePost.isSelected();
			String error = AEnv.postImmediate(
					Env.getWindowNo(getContentPane()), AD_Client_ID,
					AD_Table_ID, record_ID, force);
			setCursor(Cursor.getDefaultCursor());
			if (error != null) {
				ADialog.error(0, this, "PostingError-N", error);
			}
		}
	} // actionRePost

}
