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

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.jakz.romlib.data.assets.AssetPacker;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.ui.SearchPanel;
import com.pixbits.lib.ui.FileTransferHandler;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.data.romset.GameSetManager;
import jack.rm.files.Organizer;
import jack.rm.gui.gameinfo.InfoPanel;
import jack.rm.gui.gamelist.CountPanel;
import jack.rm.gui.gamelist.GameCellRenderer;
import jack.rm.gui.gamelist.GameListModel;
import jack.rm.gui.gamelist.GameListPanel;
import jack.rm.i18n.Text;
import jack.rm.plugins.OperationalPlugin;
import jack.rm.plugins.PluginRealType;

public class MainFrame extends JFrame implements WindowListener, Mediator
{	
	private static final long serialVersionUID = 1L;
	
	private final GameSetManager setManager;
	
	private GameSet set = null;
	
	 //menu
  final private JMenuBar menu = new JMenuBar();
  
  final JMenu romsMenu = new JMenu(Text.MENU_ROMS_TITLE.text());
  final JMenu romsExportSubmenu = new JMenu(Text.MENU_ROMS_EXPORT.text());

  final private ViewMenu viewMenu = new ViewMenu(this);
  
  final JMenu toolsMenu = new JMenu(Text.MENU_TOOLS_TITLE.text());
	
	final JMenu langMenu = new JMenu(Text.MENU_LANGUAGE_TITLE.text());
	final JMenu helpMenu = new JMenu(Text.MENU_HELP_TITLE.text());
	
	//menu File
	final JMenuItem miRoms[] = new JMenuItem[6];
	
	
	//menu Tools
	final JMenuItem miTools[] = new JMenuItem[3];
		
	private final JComboBox<GameSet> cbRomSets = new JComboBox<>();
	
	final MenuListener menuListener = new MenuListener();
		
	final private CardLayout layout = new CardLayout();
	final private JPanel cardMain = new JPanel(new BorderLayout());
	final public LogPanel logPanel = new LogPanel();
	final private ConsolePanel consolePanel = new ConsolePanel();
	
	final private GameListPanel gameListPanel = new GameListPanel(this);
	final private CountPanel countPanel = new CountPanel(gameListPanel.model());
	
	final private SearchPanel searchPanel = new SearchPanel(() -> rebuildGameList());
	final private InfoPanel infoPanel = new InfoPanel();
	final private OptionsFrame optionsFrame = new OptionsFrame(Main.manager);
		
	final private TextOutputFrame textFrame = new TextOutputFrame();
	
	private GameSet lastSet = null;
	final private ItemListener romSetListener = e -> {
    if (e.getStateChange() == ItemEvent.DESELECTED)
    {
      lastSet = (GameSet)e.getItem();
    }
    else if (e.getStateChange() == ItemEvent.SELECTED)
    {
      GameSet set = cbRomSets.getItemAt(cbRomSets.getSelectedIndex());    
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
	  searchPanel.toggle(hasSearcher ? set.helper().searcher() : null);
	  
    buildMenu(set);
	}

	public MainFrame(GameSetManager manager)
	{
		this.setManager = manager;
	    
	
				
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

		JPanel left = new JPanel(new BorderLayout());
		left.add(cbRomSets, BorderLayout.NORTH);
		left.add(gameListPanel, BorderLayout.CENTER);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.add(left);
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
	  GameSet current = cbRomSets.getSelectedIndex() != -1 ? cbRomSets.getItemAt(cbRomSets.getSelectedIndex()) : null;
	  cbRomSets.setSelectedIndex(-1);
	  
	  cbRomSets.removeAllItems();
	  
	  List<Platform> platforms = Platform.sortedValues();
	  List<GameSet> sets = GlobalSettings.settings.getEnabledProviders().stream().map(setManager::byIdent).collect(Collectors.toList());
	  
	  platforms.forEach(s -> {
	    sets.stream().filter(rs -> rs.platform().equals(s)).forEach(cbRomSets::addItem);
	  });
	  
	  if (current != null && sets.contains(current))
	    cbRomSets.setSelectedItem(current);
	  else if (cbRomSets.getItemCount() == 0)
	    gameListPanel.clearEverything();
	}
	
	private void exportList(Predicate<Game> predicate)
	{
    StringBuilder builder = new StringBuilder();
    set.stream().filter(predicate).map(r -> r.getTitle()).sorted().forEach(r -> builder.append(r).append('\n'));
    textFrame.showWithText(this, builder.toString());
	}

	private void buildMenu(final GameSet set)
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
	      Organizer.organize(set);
	      rebuildGameList();
	    });
	    romsMenu.add(renameRoms);
	    
	    JMenuItem cleanupRoms = new JMenuItem(Text.MENU_ROMS_CLEANUP.text());
	    cleanupRoms.addActionListener(e -> Organizer.cleanup(set));
	    romsMenu.add(cleanupRoms);
	    
	    romsMenu.addSeparator();
    
	    romsMenu.add(romsExportSubmenu);
    
      JMenuItem exportFavorites = new JMenuItem("Export favourites");
      exportFavorites.addActionListener( e -> { exportList(r -> r.isFavourite()); });
      romsExportSubmenu.add(exportFavorites);
      
      JMenuItem exportFound = new JMenuItem(Text.MENU_ROMS_EXPORT_FOUND.text());
      exportFound.addActionListener( e -> { exportList(r -> r.getStatus().isComplete()); });
      romsExportSubmenu.add(exportFound);
      
      JMenuItem exportMissing = new JMenuItem(Text.MENU_ROMS_EXPORT_MISSING.text());
      exportMissing.addActionListener( e -> { exportList(r -> !r.getStatus().isComplete()); });
      romsExportSubmenu.add(exportMissing);
  
      romsMenu.addSeparator();
      
      MenuElement.addListeners();
	  }
    
	  JMenuItem menuExit = new JMenuItem(Text.MENU_ROMS_EXIT.text());
    romsMenu.add(menuExit);
    menuExit.addActionListener(e -> java.lang.System.exit(0));
    
    viewMenu.clear();
    viewMenu.rebuild(set);

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
          item.addActionListener(ee -> p.execute(set));
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
	
	public void romSetLoaded(GameSet set)
	{
	  this.set = set;
	  
	  if (set != null)
	    GlobalSettings.settings.markCurrentProvider(set.ident());
	  
	  buildMenu(set);
	  
	  countPanel.gameSetLoaded(set);
	  
	  searchPanel.activate(false);
	  searchPanel.resetFields(set);
	  searchPanel.activate(true);
	  
	  searchPanel.toggle(set.getSettings().plugins.getEnabledPlugin(PluginRealType.SEARCH) != null ? set.helper().searcher() : null);
	  
    cbRomSets.removeItemListener(romSetListener);
    cbRomSets.setSelectedItem(set);
    cbRomSets.addItemListener(romSetListener);
    
    infoPanel.romSetLoaded(set);
    optionsFrame.romSetLoaded(set);
  
    gameListPanel.clearSelection();
	  rebuildGameList();
	}
	
	public void updateInfoPanel(Game rom)
	{
	  infoPanel.updateFields(rom);
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

	
	@Override public void rebuildGameList()
	{
    synchronized (gameListPanel)
    {
  	  gameListPanel.backupSelection();
      
      List<Game> data = set.stream().collect(Collectors.toList());
      gameListPanel.setData(data);
  
      Predicate<Game> predicate = searchPanel.buildSearchPredicate().and(viewMenu.buildPredicate());
      
      gameListPanel.filterData(predicate);
      gameListPanel.sortData(viewMenu.buildSorter());
          
      gameListPanel.restoreSelection();
      
      SwingUtilities.invokeLater( () -> {
        gameListPanel.refresh();
        countPanel.update();
      });
    }
	}
	
	@Override
  public void refreshGameList(int row)
  {
    gameListPanel.refresh(row);
  }
  
	@Override
  public void refreshGameList()
  {
    gameListPanel.refresh();
  }
	
	@Override
	public void refreshGameListCurrentSelection()
	{
	  gameListPanel.refreshCurrentSelection();
	}
  
  @Override
  public void setInfoPanelContent(Game game)
  {
    if (game == null)
      infoPanel.resetFields();
    else
      infoPanel.updateFields(game);
  }
}