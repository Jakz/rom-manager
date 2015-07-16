package jack.rm.gui;

import jack.rm.*;
import jack.rm.data.*;
import jack.rm.data.set.*;
import jack.rm.i18n.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.function.Predicate;
import java.awt.*;

public class MainFrame extends JFrame implements WindowListener
{	
	private static final long serialVersionUID = 1L;
	
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
	final private JScrollPane listPane = new JScrollPane(list);
	
	private final JComboBox<RomSet<? extends Rom>> cbRomSets = new JComboBox<>();
	
	final MenuListener menuListener = new MenuListener();
		
	final private CardLayout layout = new CardLayout();
	final private JPanel cardMain = new JPanel(new BorderLayout());
	final public ConsolePanel cardConsole = new ConsolePanel();
	
	final private CountPanel countPanel = new CountPanel(romListModel);
	final private SearchPanel searchPanel = new SearchPanel(this);
	
	final private ItemListener romSetListener = e -> {
    if (e.getStateChange() == ItemEvent.SELECTED)
      RomSetManager.loadSet(cbRomSets.getItemAt(cbRomSets.getSelectedIndex()));
	};
		
	public MainFrame()
	{
		initMenu();
		
		
		list.setModel(romListModel);
		list.setCellRenderer(new RomCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setFixedCellHeight(16);
		list.setBackground(Color.WHITE);
		list.getSelectionModel().addListSelectionListener(new ListListener());
		listPane.setPreferredSize(new Dimension(230,500));		
		
		this.setTransferHandler(new FileTransferHandler(new FileDropperListener()));
		
		//fileDropper = new FileDrop(list, new FileDropperListener());
		
		menu.add(romsMenu);
		menu.add(viewMenu);
		menu.add(toolsMenu);
		//menu.add(langMenu);
		//menu.add(Box.createHorizontalGlue());
		//menu.add(helpMenu);
		
		setJMenuBar(menu);
		
		for (RomSet<? extends Rom> rs : RomSetManager.sets())
			cbRomSets.addItem(rs);
		
		cbRomSets.addItemListener(romSetListener);

		JPanel romListPanel = new JPanel(new BorderLayout());
		romListPanel.add(cbRomSets, BorderLayout.NORTH);
		romListPanel.add(listPane, BorderLayout.CENTER);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.add(romListPanel);
		split.add(Main.infoPanel);
		split.setDividerLocation(400);
		
		cardMain.add(split, BorderLayout.CENTER);
		JPanel south = new JPanel(new GridLayout(1,2));
		south.add(countPanel);
		south.add(searchPanel);
		cardMain.add(south, BorderLayout.SOUTH);
		
		setLayout(layout);
		this.add(cardMain,"Main");
		this.add(cardConsole,"Console");
		
		list.setSelectedIndex(0);

		this.setPreferredSize(new Dimension(1280,700));
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		pack();
		setTitle("Rom Manager v0.6 - build 51");
	}
	
	private void initMenu()
	{	
		romsMenu.add(MenuElement.ROMS_SCAN_FOR_ROMS.item);
    romsMenu.add(MenuElement.ROMS_SCAN_FOR_NEW_ROMS.item);
    romsMenu.add(MenuElement.ROMS_RENAME.item);
    romsMenu.add(romsExportSubmenu);
    romsExportSubmenu.add(MenuElement.ROMS_EXPORT_FOUND.item);
    romsExportSubmenu.add(MenuElement.ROMS_EXPORT_MISSING.item);
    romsMenu.addSeparator();
    romsMenu.add(MenuElement.ROMS_EXIT.item);
    
    viewMenu.add(MenuElement.VIEW_SHOW_CORRECT.item);
    viewMenu.add(MenuElement.VIEW_SHOW_UNORGANIZED.item);
    viewMenu.add(MenuElement.VIEW_SHOW_NOT_FOUND.item);
    
    toolsMenu.add(MenuElement.TOOLS_DOWNLOAD_ART.item);
    toolsMenu.add(MenuElement.TOOLS_OPTIONS.item);
    toolsMenu.add(MenuElement.TOOLS_SHOW_CONSOLE.item);
		
		/*miRoms[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		miRoms[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		//miRoms[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
		
		miView[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		miView[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		miView[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));*/
	}
			
	class ListListener implements ListSelectionListener
	{
		@Override
    public void valueChanged(ListSelectionEvent e)
		{	
			if (e.getValueIsAdjusting())
				return;
			

			System.out.println("event");
			
	     StackTraceElement[] asd = Thread.currentThread().getStackTrace();
	      Arrays.stream(asd).forEach( ee -> System.out.println(ee.toString()));
				
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			
			if (lsm.getMinSelectionIndex() == -1)
			{
				Main.infoPanel.resetFields();
				return;
			}
			
			Rom rom = Main.mainFrame.romListModel.getElementAt(lsm.getMinSelectionIndex());
			
			Main.infoPanel.updateFields(rom);
		}
	}

	void toggleConsole(boolean flag)
	{
		if (flag)
		{
			cardConsole.populate();
		  layout.last(this.getContentPane());
		}
		else
			layout.first(this.getContentPane());
	}
	
	public void romSetLoaded(RomSet<?> set)
	{
	  searchPanel.activate(false);
	  searchPanel.resetFields(RomSize.mapping.values().toArray(new RomSize[RomSize.mapping.size()]));
	  searchPanel.activate(true);
	  
    cbRomSets.removeItemListener(romSetListener);
    cbRomSets.setSelectedItem(set);
    cbRomSets.addItemListener(romSetListener);
    
    Main.infoPanel.setScreenSizes(set.screenTitle,set.screenGame);
  
	  updateTable();
	}
	
	public void updateTable()
	{
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		Arrays.stream(stack).forEach(s -> System.out.println(s));
	  
	  System.out.println("updated table");
	  romListModel.clear();
		RomSet.current.list.stream().filter(searchPanel.buildSearchPredicate()).forEach(romListModel.collector());
	  romListModel.fireChanges();
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
    RomSet.current.list.save();
	}
}