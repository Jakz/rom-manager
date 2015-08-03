package jack.rm.gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import jack.rm.data.attachment.Attachment;
import jack.rm.data.attachment.AttachmentType;
import jack.rm.data.rom.Rom;

public class AttachmentTable extends JPanel
{
  Rom rom;
  
  private class AttachmentTableModel extends AbstractTableModel
  {
    
    private final String[] columnNames = {"Name", "Type", "Subtype"};
    private final Class<?>[] columnClasses = {String.class, AttachmentType.class, AttachmentType.Subtype.class};
    
    @Override public int getColumnCount() { return columnClasses.length; }
    @Override public String getColumnName(int i) { return columnNames[i]; }
    @Override public int getRowCount() { return rom != null ? rom.getAttachments().size() : 0; }
    
    @Override public Object getValueAt(int r, int c)
    {
      if (rom == null)
        return null;
      
      Attachment attach = rom.getAttachments().get(r);
      
      switch (c)
      {
        case 0: return attach.getDescription(); 
        case 1: return attach.getType();
        case 2: return attach.getSubType();
        default: return null;
      }
    }
    
    @Override public boolean isCellEditable(int r, int c) { return true; }
    
    @Override public void setValueAt(Object v, int r, int c)
    {
      Attachment attach = rom.getAttachments().get(r);
      
      switch (c)
      {
        case 0: attach.setDescription((String)v);
        case 1: attach.setType((AttachmentType)v);
        case 2: attach.setSubType((AttachmentType.Subtype)v);
      }
    }
    
    
  }
  
  private JTable table;
}
