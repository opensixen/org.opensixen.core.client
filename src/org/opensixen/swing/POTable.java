/**
 * 
 */
package org.opensixen.swing;

import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.compiere.grid.ed.VCellEditor;
import org.compiere.grid.ed.VCellRenderer;
import org.compiere.grid.ed.VHeaderRenderer;
import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.opensixen.model.ColumnDefinition;
import org.opensixen.model.POFactory;

/**
 * @author harlock
 * 
 */
public abstract class POTable extends OTable implements ActionListener {

	private static CLogger log = CLogger.getCLogger(POTable.class);

	private MTable table;

	public POTable(Properties ctx) {
		super(ctx);
		// Creamos un objeto vacio para obtener la tabla
		PO po = POFactory.get(getPOClass(), 0);

		table = MTable.get(ctx, po.get_TableName());
	}

	protected abstract <T extends PO> Class<T> getPOClass();	
	/**
	 * Setup Multi-Row Table (add fields)
	 * 
	 * @param aPanel
	 *            Panel
	 * @param mTab
	 *            Model Tab
	 * @param table
	 *            JTable
	 * @return size
	 */
	@Override
	public int setupTable() {
		POTableModel tableModel = (POTableModel) getModel();
		 ColumnDefinition[] columnDefinitions = tableModel.getColumnDefinitions();
		
		if (columnDefinitions == null) {
			return -1;
		}

		int size = columnDefinitions.length;
		TableColumnModel tcm = getColumnModel();
		if (size != tcm.getColumnCount())
			throw new IllegalStateException("TableColumn Size <> TableModel");

		for (int i = 0; i < size; i++) {
			ColumnDefinition definition = columnDefinitions[i];
			MColumn m_column = table.getColumn(definition.getName());

			GridField mField = createField(m_column.getAD_Column_ID(), false);
			TableColumn tc = tcm.getColumn(i);
			tc.setMinWidth(30);
			
			if (mField == null)	{
				int displayType = m_column.getAD_Reference_ID();
				tc.setCellRenderer(new VCellRenderer(displayType));
				// TODO : CellEditor
				
				tc.setHeaderValue(definition.getTitle());
				tc.setHeaderRenderer(new VHeaderRenderer(displayType));
			}
			else {
				
				tc.setCellRenderer(new VCellRenderer(mField));
				VCellEditor ce = new VCellEditor(mField);
				tc.setCellEditor(ce);
				//
				tc.setHeaderValue(mField.getHeader());
				tc.setPreferredWidth(Math.max(mField.getDisplayLength(), 30));
				tc.setHeaderRenderer(new VHeaderRenderer(mField.getDisplayType()));
				// Enable Button actions in grid
				if (mField.getDisplayType() == DisplayType.Button) {
					ce.setActionListener(this);
				}			
			}
		} // found field

		return size;

	} // setupVTable

	private GridField createField(int AD_Column_ID, boolean readOnly) {
		
		String sql = "SELECT * FROM AD_Field_v WHERE AD_Column_ID=?"
			+ " ORDER BY IsDisplayed DESC, SeqNo";
		if (!Env.isBaseLanguage(getCtx(), "AD_Tab"))
			sql = "SELECT * FROM AD_Field_vt WHERE AD_Column_ID=?"
				+ " AND AD_Language='" + Env.getAD_Language(getCtx()) + "'"
				+ " ORDER BY IsDisplayed DESC, SeqNo";
		
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			pstmt.setInt(1, AD_Column_ID);
			ResultSet rs = pstmt.executeQuery();
			// Solo nos interesa un resultado, aunque pueden haber muchos
			if (rs.next())
			{
				GridFieldVO vo = GridFieldVO.create(getCtx(), 1, 1, 
					1, 1, readOnly, rs);
				
				GridField field = new GridField(vo);
				return field;
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		return null;
	}

}
