package jack.rm.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import jack.rm.Main;
import jack.rm.data.romset.GameSetManager;

public class GlobalSettingsView extends JFrame
{
  public final RomSetManagerView setManager;
  public final RomSetOrderView setOrdering;
  
  private final JTabbedPane tabs;
  
  
  public GlobalSettingsView(GameSetManager manager)
  {
    this.getContentPane().setPreferredSize(new Dimension(800,600));
    
    tabs = new JTabbedPane();
    
    setManager = new RomSetManagerView(manager);
    setOrdering = new RomSetOrderView(manager);
    
    tabs.addTab("Set Order", setOrdering);
    tabs.addTab("Set Management", setManager);
    
    this.getContentPane().add(tabs);
    
    setTitle("Global Settings");
    pack();
  }
  
  void showMe()
  {
    setOrdering.showMe();
    setLocationRelativeTo(Main.mainFrame);
    setVisible(true);
  }
  
}
