package com.pixbits.plugin.gui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class PluginArgumentEditor implements TableCellEditor
{
  private final TableCellEditor inner;
  private final Class<?> clazz;
  
  public PluginArgumentEditor(Class<?> clazz, TableCellEditor inner)
  {
    this.inner = inner;
    this.clazz = clazz;
  }

  @Override public boolean isCellEditable(EventObject e) { return inner.isCellEditable(e); }
  @Override public boolean shouldSelectCell(EventObject e) { return inner.shouldSelectCell(e); }
  @Override public boolean stopCellEditing() { return inner.stopCellEditing(); }
  @Override public void cancelCellEditing() { inner.cancelCellEditing(); }
  @Override public void addCellEditorListener(CellEditorListener l) { inner.addCellEditorListener(l); }
  @Override public void removeCellEditorListener(CellEditorListener l) { inner.removeCellEditorListener(l); }

  @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
  {
    return inner.getTableCellEditorComponent(table, value, isSelected, row, column);
  }
  
  @Override
  public Object getCellEditorValue()
  {
    Object v = inner.getCellEditorValue();
    
    try
    {
    
      if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE))
        return Integer.valueOf((String)v);
      else
        return v;
    }
    catch (NumberFormatException e)
    {
      return null;
    }
  }
}
