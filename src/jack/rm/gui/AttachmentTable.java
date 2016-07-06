package jack.rm.gui;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.pixbits.gui.FileTransferHandler;
import com.pixbits.stream.StreamException;

import jack.rm.data.attachment.Attachment;
import jack.rm.data.attachment.AttachmentType;
import jack.rm.data.rom.Rom;

public class AttachmentTable extends JPanel implements FileTransferHandler.Listener
{
  Rom rom;
  
  private class AttachmentTableModel extends AbstractTableModel
  {
    
    private final String[] columnNames = {"Name", "Type", "Subtype", "Description"};
    private final Class<?>[] columnClasses = {String.class, AttachmentType.class, AttachmentType.Subtype.class, String.class};
    
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
        case 0: return attach.getName(); 
        case 1: return attach.getType();
        case 2: return attach.getSubType();
        case 3: return attach.getDescription();
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
        case 3: attach.setDescription((String)v);
      }
    }
    
    void fireChanges()
    {
      this.fireTableDataChanged();
    }
    
  }
  
  private JTable table;
  private AttachmentTableModel model;
  
  public void filesDropped(Path[] files)
  {
    if (rom == null)
      return;
    
    List<Attachment> attachments = rom.getAttachments();
   
    try
    {
    
    for (Path file : files)
    {
      if (attachments.stream().anyMatch(StreamException.rethrowPredicate(a -> Files.isSameFile(file, a.getPath()))))
        continue;
        
      Attachment attachment = new Attachment(rom, file);
      attachments.add(attachment);
      model.fireChanges();
    }
    
    }
    catch (Exception e)
    {
      e.printStackTrace();
      // TODO: log
    }
  }
  
  public AttachmentTable()
  {
    this.setLayout(new BorderLayout());
    model = new AttachmentTableModel();
    table = new JTable(model);
    JScrollPane pane = new JScrollPane(table);
    pane.setPreferredSize(new Dimension(600,200));
    this.add(pane);
    pane.setTransferHandler(new FileTransferHandler(this));
  }
  
  void setRom(Rom rom)
  {
    this.rom = rom;
    model.fireChanges();
  }
}
