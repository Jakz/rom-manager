package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.pixbits.lib.log.LogBuffer;

import jack.rm.Main;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public class LogPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	class LogTableModel extends AbstractTableModel
	{
	  final List<LogBuffer.Entry> messages = new ArrayList<>();
	  final String[] columnNames = {"Source", "Target", "Message"};
	  final Class<?>[] columnClasses = {LogSource.class, LogTarget.class, String.class};
	  
	  
	  @Override public String getColumnName(int i) { return columnNames[i]; }
	  @Override public Class<?> getColumnClass(int i) { return columnClasses[i]; }
	  @Override public int getColumnCount() { return columnNames.length; }
	  @Override public boolean isCellEditable(int r, int c) { return false; }
	  
	  @Override public int getRowCount() { return messages.size(); }
	  
	  @Override public Object getValueAt(int r, int c)
	  {
	    LogBuffer.Entry m = messages.get(r);
	    
	    switch (c)
	    {
	      case 0: return m.scope;
	      case 1: return m.attrbute;
	      case 2: return m.message;
	      default: return null;
	    }
	  }
	  
	  void populate()
	  {
	    messages.clear();
	    Main.logBuffer.stream().forEach(messages::add);
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

	      LogBuffer.Entry msg = messages.get(r);
	      
	      if (msg.level.isWarning())
	        label.setForeground(new Color(255,153,0));
	      else if (msg.level.isError())
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
		  Main.logBuffer.wipe();
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
