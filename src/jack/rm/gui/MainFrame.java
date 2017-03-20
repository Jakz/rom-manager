package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.jakz.romlib.data.platforms.Platform;
import com.pixbits.lib.ui.FileTransferHandler;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.assets.AssetPacker;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomSize;
import jack.rm.data.rom.GameStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.data.romset.RomSetManager;
import jack.rm.i18n.Text;
import jack.rm.plugins.OperationalPlugin;
import jack.rm.plugins.PluginRealType;

public class MainFrame extends JFrame implements WindowListener
{	
	private static final long serialVersionUID = 1L;
	
	private RomSet set = null;
	
	 //menu
  final private JMenuBar menu = new JMenuBar();
  
  final JMenu romsMenu = new JMenu(Text.MENU_ROMS_TITLE.text());
  final JMenu romsExportSubmenu = new JMenu(Text.MENU_ROMS_EXPORT.text());

  final JMenu viewMenu = new JMenu(Text.MENU_VIEW_TITLE.text());
  final JMenu toolsMenu = new JMenu(Text.MENU_TOOLS_TITLE.text());
	
	final JMenu langMenu = new JMenu(Text.MENU_LANGUAGE_TITLE.text());
	final JMenu helpMenu = new JMenu(Text.MENU_HELP_TITLE.text());
	
	//menu File
	final JMenuItem miRoms[] = new JMenuItem[6];
	
	//menu View
	final JCheckBoxMenuItem miView[] = new JCheckBoxMenuItem[3];
	
	//menu Tools
	final JMenuItem miTools[] = new JMenuItem[3];
	
	final RomListModel romListModel = new RomListModel();
	final public JList<Rom> list = new JList<>();
	final private ListListener listListener = new ListListener();
	final private JScrollPane listPane = new JScrollPane(list);
	
	private final JComboBox<RomSet> cbRomSets = new JComboBox<>();
	
	final MenuListener menuListener = new MenuListener();
		
	final private CardLayout layout = new CardLayout();
	final private JPanel cardMain = new JPanel(new BorderLayout());
	final public LogPanel logPanel = new LogPanel();
	final private ConsolePanel consolePanel = new ConsolePanel();
	
	final private CountPanel countPanel = new CountPanel(romListModel);
	final private SearchPanel searchPanel = new SearchPanel(this);
	final private InfoPanel infoPanel = new InfoPanel();
	final private OptionsFrame optionsFrame = new OptionsFrame(Main.manager);
		
	final private TextOutputFrame textFrame = new TextOutputFrame();
	
	private RomSet lastSet = null;
	final private ItemListener romSetListener = e -> {
    if (e.getStateChange() == ItemEvent.DESELECTED)
    {
      lastSet = (RomSet)e.getItem();
    }
    else if (e.getStateChange() == ItemEvent.SELECTED)
    {
      RomSet set = cbRomSets.getItemAt(cbRomSets.getSelectedIndex());    
      try
      {
        Main.loadRomSet(set);
      }
      catch (java.io.FileNotFoundException ee)
      {
        Dialogs.showError("DAT File not found!", "Missing DAT file for set "+set.ident(), Main.mainFrame);
        cbRomSets.setSelectedItem(lastSet);
      } 
      catch (IOException e1)
      {
        e1.printStackTrace();
      }
    }
	};
	
	final private ListCellRenderer<Object> cbRomSetRenderer = new RomSetListCellRenderer();
	
	public void pluginStateChanged()
	{
	  optionsFrame.pluginStateChanged();
	  
	  boolean hasSearcher = set.getSettings().plugins.getEnabledPlugin(PluginRealType.SEARCH) != null;
	  searchPanel.toggle(hasSearcher);
	}

	public MainFrame()
	{
		list.setModel(romListModel);
		list.setCellRenderer(new RomCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setFixedCellHeight(16);
		list.setBackground(Color.WHITE);
		list.getSelectionModel().addListSelectionListener(listListener);
    list.setSelectedIndex(0);
        
    list.addMouseListener(
        new MouseAdapter(){
          @Override
          public void mouseClicked(MouseEvent e){
            if (e.getClickCount() == 2){
              int r = list.getSelectedIndex();
              
              if (r != -1)
              {
                Rom rom = list.getModel().getElementAt(r);
                
                rom.setFavourite(!rom.isFavourite());
                romListModel.fireChanges(r);   
              }
            }
          }
        });

		listPane.setPreferredSize(new Dimension(230,500));		
		
		
		listPane.setTransferHandler(new FileTransferHandler(new FileDropperListener()));
				
		menu.add(romsMenu);
		menu.add(viewMenu);
		menu.add(toolsMenu);
		//menu.add(langMenu);
		//menu.add(Box.createHorizontalGlue());
		//menu.add(helpMenu);
		
		setJMenuBar(menu);

		rebuildEnabledDats();

		cbRomSets.addItemListener(romSetListener);
		cbRomSets.setRenderer(cbRomSetRenderer);

		JPanel romListPanel = new JPanel(new BorderLayout());
		romListPanel.add(cbRomSets, BorderLayout.NORTH);
		romListPanel.add(listPane, BorderLayout.CENTER);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.add(romListPanel);
		split.add(infoPanel);
		split.setDividerLocation(400);
		
		cardMain.add(split, BorderLayout.CENTER);
		JPanel south = new JPanel(new GridLayout(1,2));
		south.add(countPanel);
		south.add(searchPanel);
		cardMain.add(south, BorderLayout.SOUTH);
		
		setLayout(layout);
		getContentPane().add(cardMain, "main");
		getContentPane().add(logPanel, "log");
		getContentPane().add(consolePanel, "console");
		layout.show(getContentPane(), "main");

		this.setPreferredSize(new Dimension(1440,900));
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		buildMenu(null);
			
		pack();
		setTitle("Rom Manager v0.8 - build 161");
	}
	
	public void rebuildEnabledDats()
	{
	  RomSet current = cbRomSets.getSelectedIndex() != -1 ? cbRomSets.getItemAt(cbRomSets.getSelectedIndex()) : null;
	  cbRomSets.setSelectedIndex(-1);
	  
	  cbRomSets.removeAllItems();
	  
	  List<Platform> systems = Platform.sortedValues();
	  List<RomSet> sets = GlobalSettings.settings.getEnabledProviders().stream().map(RomSetManager::byIdent).collect(Collectors.toList());
	  
	  systems.forEach(s -> {
	    sets.stream().filter(rs -> rs.platform.equals(s)).forEach(cbRomSets::addItem);
	  });
	  
	  if (current != null && sets.contains(current))
	    cbRomSets.setSelectedItem(current);
	  else if (cbRomSets.getItemCount() == 0)
	  {
	    romListModel.clear();
	    list.clearSelection();
	  }
	}
	
	private void exportList(Predicate<Rom> predicate)
	{
    StringBuilder builder = new StringBuilder();
    set.list.stream().filter(predicate).map(r -> r.getTitle()).sorted().forEach(r -> builder.append(r).append('\n'));
    textFrame.showWithText(this, builder.toString());
	}

	private void buildMenu(RomSet set)
	{	
		MenuElement.clearListeners();
	  	  
    romsMenu.removeAll();
		
	  if (set != null)
	  {
	    romsMenu.add(MenuElement.ROMS_SCAN_FOR_ROMS.item);
	    romsMenu.add(MenuElement.ROMS_SCAN_FOR_NEW_ROMS.item);
	    romsMenu.addSeparator();
	    
	    JMenuItem renameRoms = new JMenuItem(Text.MENU_ROMS_RENAME.text());
	    renameRoms.addActionListener(e -> {
	      set.list.organize();
	      Main.mainFrame.updateTable();
	    });
	    romsMenu.add(renameRoms);
	    
	    JMenuItem cleanupRoms = new JMenuItem(Text.MENU_ROMS_CLEANUP.text());
	    cleanupRoms.addActionListener(e -> set.cleanup());
	    romsMenu.add(cleanupRoms);
	    
	    romsMenu.addSeparator();
    
	    romsMenu.add(romsExportSubmenu);
    
      JMenuItem exportFavorites = new JMenuItem("Export favourites");
      exportFavorites.addActionListener( e -> { exportList(r -> r.isFavourite()); });
      romsExportSubmenu.add(exportFavorites);
      
      JMenuItem exportFound = new JMenuItem(Text.MENU_ROMS_EXPORT_FOUND.text());
      exportFound.addActionListener( e -> { exportList(r -> r.status != GameStatus.MISSING); });
      romsExportSubmenu.add(exportFound);
      
      JMenuItem exportMissing = new JMenuItem(Text.MENU_ROMS_EXPORT_MISSING.text());
      exportMissing.addActionListener( e -> { exportList(r -> r.status == GameStatus.MISSING); });
      romsExportSubmenu.add(exportMissing);
  
      romsMenu.addSeparator();
      
      MenuElement.addListeners();
	  }
    
	  JMenuItem menuExit = new JMenuItem(Text.MENU_ROMS_EXIT.text());
    romsMenu.add(menuExit);
    menuExit.addActionListener(e -> java.lang.System.exit(0));
    
    if (set != null)
    {    
      JMenuItem[] filters = { MenuElement.VIEW_SHOW_CORRECT.item, MenuElement.VIEW_SHOW_UNORGANIZED.item, MenuElement.VIEW_SHOW_NOT_FOUND.item };
      Arrays.stream(filters).forEach( mi -> {
        viewMenu.add(mi);
        mi.setSelected(true);
      });
    }

    
    toolsMenu.removeAll();
    
    MenuElement.TOOLS_GLOBAL_SETTINGS.item.addActionListener( e -> Main.gsettingsView.showMe());
    toolsMenu.add(MenuElement.TOOLS_GLOBAL_SETTINGS.item);

    if (set != null)
    {
      MenuElement.TOOLS_OPTIONS.item.addActionListener( e -> optionsFrame.showMe() );
      toolsMenu.add(MenuElement.TOOLS_OPTIONS.item);
      
      MenuElement.TOOLS_SHOW_MESSAGES.item.addActionListener( e -> toggleLogPanel(((JMenuItem)e.getSource()).isSelected()));
      toolsMenu.add(MenuElement.TOOLS_SHOW_MESSAGES.item);
      
      MenuElement.TOOLS_CONSOLE.item.addActionListener( e -> toggleConsole(((JMenuItem)e.getSource()).isSelected()));
      toolsMenu.add(MenuElement.TOOLS_CONSOLE.item);
      
      JMenu assetsMenu = new JMenu(Text.MENU_TOOLS_ASSETS.text());
      
      assetsMenu.add(MenuElement.TOOLS_DOWNLOAD_ASSETS.item);
      MenuElement.TOOLS_DOWNLOAD_ASSETS.item.addActionListener( e -> Main.downloader.start() );
      assetsMenu.add(MenuElement.TOOLS_PACK_ASSETS.item);
      MenuElement.TOOLS_PACK_ASSETS.item.addActionListener( e -> AssetPacker.packAssets(set) );
      
      toolsMenu.addSeparator();
      toolsMenu.add(assetsMenu);
      
      JMenu pluginsMenu = new JMenu("Plugins"); // TODO: localize
         
      Map<String, List<OperationalPlugin>> plugins = new TreeMap<>();
      
      set.getSettings().plugins.stream()
      .filter(p -> p.isEnabled() && p instanceof OperationalPlugin)
      .map(p -> (OperationalPlugin)p)
      .forEach(p -> {
        String key = p.getSubmenuCaption();
        plugins.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
      });
      
      plugins.values().forEach(l -> Collections.sort(l, new Comparator<OperationalPlugin>() {
        @Override public int compare(OperationalPlugin o1, OperationalPlugin o2) {
          return o1.getMenuCaption().compareTo(o2.getMenuCaption());
        }
      }));
      
      plugins.entrySet().forEach(e -> {
        JMenu menu = new JMenu(e.getKey());
        e.getValue().forEach(p -> {
          JMenuItem item = new JMenuItem(p.getMenuCaption());
          item.addActionListener(ee -> p.execute(set.list));
          menu.add(item);
        });
        pluginsMenu.add(menu);
      });
      
      if (pluginsMenu.getItemCount() != 0)
      {
        toolsMenu.addSeparator();
        toolsMenu.add(pluginsMenu);
      }
    
    }
	}
			
  class ListListener implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if (e.getValueIsAdjusting())
        return;

      ListSelectionModel lsm = (ListSelectionModel) e.getSource();

      if (lsm.getMinSelectionIndex() == -1)
      {
        infoPanel.resetFields();
        return;
      }

      Rom rom = Main.mainFrame.romListModel.getElementAt(lsm.getMinSelectionIndex());

      infoPanel.updateFields(rom);
    }
  }

	private void toggleLogPanel(boolean flag)
	{
		if (flag)
		{
			logPanel.populate();
			layout.show(getContentPane(), "log");
		}
		else
      layout.show(getContentPane(), "main");
	}
	
	private void toggleConsole(boolean flag)
	{
	   if (flag)
	    {
	      logPanel.populate();
	      layout.show(getContentPane(), "console");
	    }
	    else
	      layout.show(getContentPane(), "main");
	}
	
	public void romSetLoaded(RomSet set)
	{
	  this.set = set;
	  
	  if (set != null)
	    GlobalSettings.settings.markCurrentProvider(set.ident());
	  
	  buildMenu(set);
	  
	  searchPanel.activate(false);
	  searchPanel.resetFields(RomSize.mapping.values().toArray(new RomSize[RomSize.mapping.size()]));
	  searchPanel.activate(true);
	  
    cbRomSets.removeItemListener(romSetListener);
    cbRomSets.setSelectedItem(set);
    cbRomSets.addItemListener(romSetListener);
    
    infoPanel.romSetLoaded(set);
    optionsFrame.romSetLoaded(set);
  
    list.clearSelection();
	  updateTable();
	}
	
	public void updateInfoPanel(Rom rom)
	{
	  infoPanel.updateFields(rom);
	}
	
	public void updateTable()
	{
		Rom current = list.getSelectedValue();
		int index = list.getSelectedIndex();
	  
	  /*StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		Arrays.stream(stack).forEach(s -> System.out.println(s));*/
	  
	  //System.out.println("updated table");
	  romListModel.clear();
	  
	  Predicate<Rom> predicate = searchPanel.buildSearchPredicate().and( r ->
	    r.status == GameStatus.FOUND && MenuElement.VIEW_SHOW_CORRECT.item.isSelected() ||
	    r.status == GameStatus.MISSING && MenuElement.VIEW_SHOW_NOT_FOUND.item.isSelected() ||
	    r.status == GameStatus.UNORGANIZED && MenuElement.VIEW_SHOW_UNORGANIZED.item.isSelected()
	  );
	  
		set.list.stream().filter(predicate).forEach(romListModel.collector());

    if (current != null)     
    {      
      list.clearSelection();
      list.setSelectedValue(current, true);
      
      if (list.getSelectedValue() == null && index != -1)
      {
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
      }
    }
		
    SwingUtilities.invokeLater( () -> {
      romListModel.fireChanges();
    });

		countPanel.update();
	}

	@Override
  public void windowActivated(WindowEvent e) { }
	@Override
  public void windowClosed(WindowEvent e) { }
	@Override
  public void windowDeactivated(WindowEvent e) { }
	@Override
  public void windowIconified(WindowEvent e) { }
	@Override
  public void windowDeiconified(WindowEvent e) { }
	@Override
  public void windowOpened(WindowEvent e) { }
	
	@Override
  public void windowClosing(WindowEvent e)
	{
    GlobalSettings.save();
	  
	  if (set != null)
      set.saveStatus();
	}
}