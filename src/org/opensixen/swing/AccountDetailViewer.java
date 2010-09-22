/**
 * 
 */
package org.opensixen.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JTable;

import org.compiere.swing.CFrame;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 * 
 * 
 * @author Eloy Gomez
 * Indeos Consultoria http://www.indeos.es
 *
 */
public class AccountDetailViewer extends CFrame {

	private int C_ElementValue_ID;
	private Timestamp from, to;
	
	private Properties ctx;
	
	public AccountDetailViewer (Properties ctx, int C_ElementValue_ID, Timestamp from, Timestamp to )	{
		super(Msg.getMsg(Env.getCtx(), "AcctViewer"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.C_ElementValue_ID = C_ElementValue_ID;
		this.from = from;
		this.to = to;
		this.ctx = ctx;
		jbInit();
		pack();
		setVisible(true);
	}
	
	private void jbInit() {
		CPanel container = new CPanel();
		container.setLayout(new BorderLayout(5, 5));
		this.getContentPane().add(container);		
		
		
		
		
		// Create main table
		
		OTable table = new OTable(ctx);
		AccountDetailTableModel tableModel = new AccountDetailTableModel(ctx, C_ElementValue_ID, from, to);
		table.setModel(tableModel);
		
		table.setupTable();
		
		table.setFillsViewportHeight(true);
		table.setPreferredScrollableViewportSize(new Dimension(800, 500));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.autoSize(true);
		CScrollPane scrollPane = new CScrollPane(table);
		container.add(scrollPane, BorderLayout.CENTER);

		table.packAll();
		
	}

	
}
