package jack.rm.gui;

import java.awt.Component;
import java.util.Arrays;

import jack.rm.Main;
import jack.rm.data.console.System;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class RomSetManagerView extends JFrame
{
  private final JList<System> systemList;
  private final DefaultListModel<System> systemModel;
  
  @SuppressWarnings("rawtypes")
  private class SystemListCellRenderer implements ListCellRenderer<System>
  {
    ListCellRenderer renderer;

    SystemListCellRenderer(ListCellRenderer renderer)
    {
      this.renderer = renderer;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<? extends System> list, System value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label = (JLabel)renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      label.setIcon(value.icon != null ? value.icon.getIcon() : null);
      label.setText(value.name);
    
      return label;
    }
  }
  
  RomSetManagerView()
  {
    systemModel = new DefaultListModel<>();
    Arrays.stream(System.values()).forEach(s -> systemModel.addElement(s));
    
    systemList = new JList<>();
    systemList.setModel(systemModel);
    systemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    systemList.setCellRenderer(new SystemListCellRenderer(new DefaultListCellRenderer()));
    
    JScrollPane systemScrollPane = new JScrollPane(systemList);
    
    getContentPane().add(systemScrollPane);
    
    setTitle("Rom Sets Management");
    pack();
  }
  
  void showMe()
  {
    setLocationRelativeTo(Main.mainFrame);
    setVisible(true);
  }
}
