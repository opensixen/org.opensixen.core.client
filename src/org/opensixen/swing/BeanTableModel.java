/**
 * 
 */
package org.opensixen.swing;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.swing.event.TableModelListener;

import org.opensixen.interfaces.IBeanProvider;
import org.opensixen.interfaces.OTableModel;
import org.opensixen.model.ColumnDefinition;

/**
 * @author harlock
 * @param <T>
 *
 */
public class BeanTableModel implements OTableModel {

	private ColumnDefinition[] columnDefinitions;
	
	private IBeanProvider beanProvider;
	
	private Object[] model;
	
	private HashMap<String, Method> readMethods = new HashMap<String, Method>();
	private HashMap<String, Method> writeMethods = new HashMap<String, Method>();
	
	

	public BeanTableModel(IBeanProvider beanProvider, ColumnDefinition[] columnDefinitions) {
		super();
		this.beanProvider = beanProvider;
		this.columnDefinitions = columnDefinitions;
		model = beanProvider.getModel();
		if (model != null)	{
			inspect();
		}
		
	}

	/**
	 * Inspecciona el modelo para conocer sus propiedades.
	 */
	private void inspect()	{
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(beanProvider.getModelClass(), Introspector.USE_ALL_BEANINFO);			
		} catch (IntrospectionException e) {
			throw new RuntimeException("No puedo crear el TableModel", e);
		}
		
		PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
		for (PropertyDescriptor descriptor: propDesc)	{
			Method read = descriptor.getReadMethod();
			Method write = descriptor.getWriteMethod();
			String name = descriptor.getName();
			readMethods.put(name, read);
			writeMethods.put(name, write);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String name = columnDefinitions[columnIndex].getName();
		Method read = readMethods.get(name);
		if (read == null)	{
			return null;
		}
		try {
			return read.invoke(model[rowIndex],(Object[])null);
		} catch (Exception e) {			
			e.printStackTrace();
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
		Method write = writeMethods.get(name);
		Object[] values = {value};
		try {			
			write.invoke(model[rowIndex], values);
		} catch (Exception e) {			
			e.printStackTrace();
		} 		
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

	/* (non-Javadoc)
	 * @see org.opensixen.interfaces.OTableModel#reload()
	 */
	@Override
	public void reload() {
		beanProvider.reload();
		this.model = beanProvider.getModel();		
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
		if (model != null)	{
			inspect();
		}
	}
	
	

	

}
