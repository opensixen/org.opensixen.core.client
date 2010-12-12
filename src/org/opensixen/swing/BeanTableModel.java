/**
 * 
 */
package org.opensixen.swing;

import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.beanutils.PropertyUtils;
import org.compiere.util.CLogger;
import org.opensixen.interfaces.IBeanProvider;
import org.opensixen.interfaces.OTableModel;
import org.opensixen.model.ColumnDefinition;

/**
 * @author Eloy Gomez
 *
 */
public class BeanTableModel implements OTableModel {

	private CLogger log = CLogger.getCLogger(getClass());
	
	private ColumnDefinition[] columnDefinitions;
	
	private IBeanProvider beanProvider;
	
	private Object[] model;
	
	private ArrayList<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();
	

	public BeanTableModel(IBeanProvider beanProvider, ColumnDefinition[] columnDefinitions) {
		super();
		this.beanProvider = beanProvider;
		this.columnDefinitions = columnDefinitions;
		model = beanProvider.getModel();		
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String name = columnDefinitions[columnIndex].getName();
		try {
			return PropertyUtils.getProperty(model[rowIndex], name);
		}
		catch (Exception e)	{
			log.log(Level.SEVERE, "No se encuentra la propiedad: " + name + "[" + columnIndex + "]", e);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		String name = columnDefinitions[columnIndex].getName();
		try {
			PropertyUtils.setProperty(model[rowIndex], name, value);
		}
		catch (Exception e)	{
			log.log(Level.SEVERE, "No se encuentra la propiedad: " + name + "[" + columnIndex + "]", e);
		}	
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		tableModelListeners.add(l);		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void removeTableModelListener(TableModelListener l) {
		tableModelListeners.remove(l);		
	}

	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.OTableModel#reload()
	 */
	@Override
	public void reload() {
		beanProvider.reload();
		this.model = beanProvider.getModel();
		for (TableModelListener listener:tableModelListeners)	{
			listener.tableChanged(new TableModelEvent(this));
		}
	}

	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.OTableModel#getColumnDefinitions()
	 */
	@Override
	public ColumnDefinition[] getColumnDefinitions() {
		return columnDefinitions;
	}

	/**
	 * @return the beanProvider
	 */
	public IBeanProvider getBeanProvider() {
		return beanProvider;
	}

	/**
	 * @param beanProvider the beanProvider to set
	 */
	public void setBeanProvider(IBeanProvider beanProvider) {
		this.beanProvider = beanProvider;
		model = beanProvider.getModel();
	}
}
