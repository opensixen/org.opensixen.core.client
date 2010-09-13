/**
 * 
 */
package org.opensixen.swing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.compiere.apps.search.Info_Column;
import org.compiere.model.MColumn;
import org.compiere.model.MQuery;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.opensixen.interfaces.OTableModel;
import org.opensixen.model.ColumnDefinition;
import org.opensixen.model.GroupDefinition;
import org.opensixen.model.GroupVariable;

/**
 * @author harlock
 *
 */
public abstract class POTableModel implements OTableModel {

	protected Properties ctx;
	private PO[] model;
	private ColumnDefinition[] columnDefinitions;
	private List<GroupDefinition> groupDefinitions;

	/** Index map	*/
	HashMap<Integer, GroupIndex> groupIndexMap;
	
	private MQuery query;
	
	public POTableModel(Properties ctx, MQuery query) {
		super();
		this.ctx = ctx;
		this.query = query;
		this.model = getModel(query);
		this.columnDefinitions =  getColumnDefinitions();
		this.groupDefinitions = getGroupDefinitions();
		if (groupDefinitions != null)	{
			indexGroups();
		}
		
		// if model is void, not inspect
		if (model.length == 0)	{
			return;
		}
		PO po = model[0];
		MTable mtable = MTable.get(ctx, po.get_Table_ID());
		
		// Check columns title
		for (int i=0; i < columnDefinitions.length; i++)	{
			if (columnDefinitions[i].getTitle() == null)	{
				MColumn column = mtable.getColumn(columnDefinitions[i].getName());
				columnDefinitions[i].setTitle(column.get_Translation(columnDefinitions[i].getName()));
			}
		}
		
	}

	public void setQuery(MQuery query)	{
		this.query = query;
		reload();
	}
	
	public void reload()	{
		this.model = null;
		this.model = getModel(query);
	}
	
	public static String getWhere(MQuery query)	{
		String where = query.getWhereClause(false);
		
		// A veces se olvida de no hacer qualifed...
		
		where = where.replaceAll(query.getTableName()+".", "");
		return where;
	}
	
	
	protected abstract PO[] getModel(MQuery query);

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (model == null)	{
			return 0;
		}
		return model.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnDefinitions.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return columnDefinitions[columnIndex].getTitle();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnDefinitions[columnIndex].getClazz();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PO po = model[rowIndex];
		String colName = columnDefinitions[columnIndex].getName();
		if (po != null)	{		
			return po.get_Value(colName);
		}
		
		if (groupIndexMap.containsKey(rowIndex))	{
			GroupIndex group = groupIndexMap.get(rowIndex);
			if (group.containsFooterVariableValue(colName))	{
				return group.getFooterVariableValue(colName);
			}
			return null;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub		
	}
	
	/**
	 * Get group definitions
	 * @return
	 */
	protected List<GroupDefinition> getGroupDefinitions() {
		return null;
	}
	
	/**
	 * Retrun true if row is a group variable group
	 * @param row
	 * @return
	 */
	public boolean isGroupVariableRow(int row)	{
		if (groupIndexMap == null)	{
			return false;
		}
		return groupIndexMap.containsKey(row);
	}
	
	
	/**
	 * Create index for the groups 
	 */
	private void indexGroups() {
		
		// New array, with records and null rows for group header and footer variables.
		ArrayList<PO> poArray = new ArrayList<PO>();
		HashMap<String, GroupIndex> currentIndex = new HashMap<String, GroupIndex>();
		groupIndexMap = new HashMap<Integer, GroupIndex>();
		
		
		// For each row
		for (int index =0; index < model.length; index++)	{
			PO po= model[index];
			poArray.add(po);
			
			// Search group columns changes
			for (GroupDefinition definitions:groupDefinitions)	{							
				for (String column:definitions.getGroupColumns())	{
					Object current = po.get_Value(column);
					
					// If index not contain this column, is first ocurrence
					if (!currentIndex.containsKey(column))	{
						// Start position of index in size -1
						GroupIndex i = new GroupIndex(poArray.size()-1);
						i.groupVariableValue = current;
						currentIndex.put(column, i);
					}
					
					
					// If not change, calc the values to the the footer or header varaibles
					if (current.equals(currentIndex.get(column).groupVariableValue)) {
						// Calc footer variables
						for (GroupVariable var: definitions.getFooterVariables())	{
							BigDecimal c_value = (BigDecimal) currentIndex.get(column).getFooterVariableValue(var.getName());
							BigDecimal n_value = c_value.add((BigDecimal) po.get_Value(var.getName()));
							currentIndex.get(column).setFooterVariableValue(var.getName(), n_value);							
						}
					}
					
					// If value change
					if (!current.equals(currentIndex.get(column).groupVariableValue) || index == model.length -1)	{
						// set to property in GroupIndex to the current row
						GroupIndex i = currentIndex.get(column);
						i.to = poArray.size() -1;
						
						// Add a null PO in the array
						poArray.add(null);
						
						// Add the GroupIndex to the global groupIndexMap
						groupIndexMap.put(poArray.size()-1, i);
						
						// Delete the current groupIndex
						currentIndex.remove(column);																																								
					}					
					
				}
			}
		}
		
		model = poArray.toArray(model);
		
	}	
}

class GroupIndex	{
	public int from;
	public int to;
	
	public Object groupVariableValue = null;
	
	public HashMap<String, Object> headerVariablesValue = new HashMap<String, Object>(); 
	
	public HashMap<String, Object> footerVariablesValue = new HashMap<String, Object>();
	
	public GroupIndex(int from) {
		super();
		this.from = from;
	}
	
	public void setHeaderVariableValue (String variable, Object value)	{
		headerVariablesValue.put(variable, value);
	}
	
	public void setFooterVariableValue (String variable, Object value)	{
		footerVariablesValue.put(variable, value);
	}
		
	
	public Object getHeaderVariableValue(String variable)	{
		if (!headerVariablesValue.containsKey(variable))	{
			return Env.ZERO;
		}
		return headerVariablesValue.get(variable);
	}

	public Object getFooterVariableValue(String variable)	{
		if (!footerVariablesValue.containsKey(variable))	{
			return Env.ZERO;
		}
		return footerVariablesValue.get(variable);
	}

	
	public boolean containsHeaderVariableValue(String variable)	{
		return headerVariablesValue.containsKey(variable); 
	}

	public boolean containsFooterVariableValue(String variable)	{
		return footerVariablesValue.containsKey(variable);
	}

}



