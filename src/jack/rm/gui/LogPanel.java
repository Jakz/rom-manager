package jack.rm.gui;

import javax.swing.table.*;

import jack.rm.log.*;

import java.util.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	class LogTableModel extends AbstractTableModel
	{
	  final List<LogMessage> messages = new ArrayList<>();
	  final String[] columnNames = {"Source", "Target", "Message"};
	  final Class<?>[] columnClasses = {LogSource.class, LogTarget.class, String.class};
	  
	  
	  @Override public String getColumnName(int i) { return columnNames[i]; }
	  @Override public Class<?> getColumnClass(int i) { return columnClasses[i]; }
	  @Override public int getColumnCount() { return columnNames.length; }
	  @Override public boolean isCellEditable(int r, int c) { return false; }
	  
	  @Override public int getRowCount() { return messages.size(); }
	  
	  @Override public Object getValueAt(int r, int c)
	  {
	    LogMessage m = messages.get(r);
	    
	    switch (c)
	    {
	      case 0: return m.source;
	      case 1: return m.target;
	      case 2: return m.message;
	      default: return null;
	    }
	  }
	  
	  void populate()
	  {
	    messages.clear();
	    
	    List<LogMessage> tmessages = Log.get();
	    
	    for (LogMessage msg : tmessages)
	      messages.add(msg);
	  }
	  
	  void fireChanges()
	  {
	    fireTableDataChanged();
	  }
	  
	  class Renderer extends DefaultTableCellRenderer
	  {
	    @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c)
	    {
	      JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);

	      LogMessage msg = messages.get(r);
	      
	      if (msg.type == LogType.WARNING)
	        label.setForeground(new Color(255,153,0));
	      else if (msg.type == LogType.ERROR)
	        label.setForeground(new Color(180,0,0));
	      else
	        label.setForeground(table.getForeground());
	      
	      return label;
	    }
	  } 
	}
	
	private final JTable table;
	private final LogTableModel model;
	private final JButton clearButton = new JButton("Clear");
	
	LogPanel()
	{
		model = new LogTableModel();
				
		table = new JTable(model);
		
		LogTableModel.Renderer tableRenderer = model.new Renderer();
		
		for (int i = 0; i < table.getColumnCount(); ++i)
		  table.getColumnModel().getColumn(i).setCellRenderer(tableRenderer);

		JScrollPane scroll = new JScrollPane(table);
		this.setLayout(new BorderLayout());
		this.add(scroll,BorderLayout.CENTER);
		
		clearButton.addActionListener(e -> {
		  Log.wipe(); 
		  populate();
		});
		
		JPanel lower = new JPanel(new GridLayout(1,5));
		lower.add(new JLabel());
		lower.add(new JLabel());
		lower.add(clearButton);
		lower.add(new JLabel());
		lower.add(new JLabel());
		
		this.add(lower, BorderLayout.SOUTH);
	}
	
	public void populate()
	{
	  model.populate();
	  model.fireChanges();
	}
}
