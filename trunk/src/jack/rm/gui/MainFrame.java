package jack.rm.gui;

import jack.rm.*;
import jack.rm.data.*;
import jack.rm.data.set.*;
import jack.rm.i18n.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

public class MainFrame extends JFrame
{	
	private static final long serialVersionUID = 1L;

	//menu
	final private JMenuBar menu = new JMenuBar();
	
	final JMenu romsMenu = new JMenu(Text.MENU_ROMS_TITLE.text());
	final JMenu viewMenu = new JMenu(Text.MENU_VIEW_TITLE.text());
	final JMenu toolsMenu = new JMenu(Text.MENU_TOOLS_TITLE.text());
	final JMenu langMenu = new JMenu(Text.MENU_LANGUAGE_TITLE.text());
	final JMenu helpMenu = new JMenu(Text.MENU_HELP_TITLE.text());
	
	//menu File
	final JMenu romsSubmenu = new JMenu(Text.MENU_ROMS_EXPORT.text());
	final JMenuItem miRoms[] = new JMenuItem[5];
	
	//menu View
	final JCheckBoxMenuItem miView[] = new JCheckBoxMenuItem[3];
	
	//menu Tools
	final JMenuItem miTools[] = new JMenuItem[3];
	
	public final RomListModel romListModel = new RomListModel();
	final public JList list = new JList();
	final private JScrollPane listPane = new JScrollPane(list);
	
	public final JComboBox cbRomSets = new JComboBox();
	public final RomSetListener rsListener = new RomSetListener();
	
	final MenuListener menuListener = new MenuListener();
	
	
	final private CardLayout layout = new CardLayout();
	final private JPanel cardMain = new JPanel(new BorderLayout());
	final public ConsolePanel cardConsole = new ConsolePanel();
	
	public MainFrame()
	{
		initMenu();
		
		list.setModel(romListModel);
		list.setCellRenderer(new CellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setFixedCellHeight(16);
		list.setBackground(Color.WHITE);
		list.getSelectionModel().addListSelectionListener(new ListListener());
		listPane.setPreferredSize(new Dimension(230,500));		
		
		menu.add(romsMenu);
		menu.add(viewMenu);
		menu.add(toolsMenu);
		//menu.add(langMenu);
		//menu.add(Box.createHorizontalGlue());
		//menu.add(helpMenu);
		
		setJMenuBar(menu);
		
		for (RomSet rs : RomSetManager.sets())
			cbRomSets.addItem(rs);
		cbRomSets.addActionListener(rsListener);
		
		JPanel romListPanel = new JPanel(new BorderLayout());
		romListPanel.add(cbRomSets, BorderLayout.NORTH);
		romListPanel.add(listPane, BorderLayout.CENTER);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.add(romListPanel);
		split.add(Main.infoPanel);
		split.setDividerLocation(400);
		
		cardMain.add(split, BorderLayout.CENTER);
		JPanel south = new JPanel(new GridLayout(1,2));
		south.add(Main.countPanel);
		south.add(Main.searchPanel);
		cardMain.add(south, BorderLayout.SOUTH);
		
		setLayout(layout);
		this.add(cardMain,"Main");
		this.add(cardConsole,"Console");
		
		list.setSelectedIndex(0);

		this.setPreferredSize(new Dimension(1280,700));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		pack();
		setTitle("Rom Manager v0.6 - build 51");
	}
	
	private void initMenu()
	{	
		Text[] miRomsNames = {Text.MENU_ROMS_SCAN_FOR_ROMS,Text.MENU_ROMS_EXPORT_MISSING,Text.MENU_ROMS_EXPORT_FOUND,Text.MENU_ROMS_EXIT,Text.MENU_ROMS_RENAME};
		
		for (int t = 0; t < 5; ++t)
		{	
			miRoms[t] = new JMenuItem(miRomsNames[t].text());
			miRoms[t].addActionListener(menuListener);
		}

		Text[] miViewNames = {Text.MENU_VIEW_SHOW_CORRECT,Text.MENU_VIEW_SHOW_NOT_FOUND,Text.MENU_VIEW_SHOW_BADLY_NAMED};
		
		for (int t = 0; t < miView.length; ++t)
		{
			miView[t] = new JCheckBoxMenuItem(miViewNames[t].text(),false);
			miView[t].addActionListener(menuListener);
		}
		
		Text[] myToolsNames = {Text.MENU_TOOLS_DOWNLOAD_ART,Text.MENU_TOOLS_OPTIONS};
		for (int t = 0; t < miTools.length-1; ++t)
		{
			miTools[t] = new JMenuItem(myToolsNames[t].text());
			miTools[t].addActionListener(menuListener);
		}
		miTools[miTools.length-1] = new JCheckBoxMenuItem(Text.MENU_TOOLS_SHOW_CONSOLE.text());
		miTools[miTools.length-1].addActionListener(menuListener);
		
		romsSubmenu.add(miRoms[1]);
		romsSubmenu.add(miRoms[2]);
		romsMenu.add(miRoms[0]);
		romsMenu.add(miRoms[4]);
		romsMenu.add(romsSubmenu);
		romsMenu.addSeparator();
		romsMenu.add(miRoms[3]);
		
		viewMenu.add(miView[0]);
		viewMenu.add(miView[1]);
		viewMenu.add(miView[2]);
		
		toolsMenu.add(miTools[0]);
		toolsMenu.add(miTools[1]);
		toolsMenu.addSeparator();
		toolsMenu.add(miTools[2]);
		
		
		miRoms[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		miRoms[4].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		//miRoms[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
		
		miView[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		miView[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		miView[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
	}
			
	class ListListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{	
			if (e.getValueIsAdjusting())
				return;
				
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			
			if (lsm.getMinSelectionIndex() == -1)
			{
				Main.infoPanel.resetFields();
				return;
			}
			
			Rom rom = (Rom)Main.mainFrame.romListModel.getElementAt(lsm.getMinSelectionIndex());
			
			Main.infoPanel.updateFields(rom);
		}
	}
	
	class RomSetListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			RomSetManager.loadSet((RomSet)cbRomSets.getSelectedItem());
		}
	}
	
	void toggleConsole(boolean flag)
	{
		if (flag)
			layout.last(this.getContentPane());
		else
			layout.first(this.getContentPane());
	}
	
	public void updateTable()
	{
		romListModel.fireChanges();
		Main.countPanel.update();
	}
	
	public void updateCbRomSet(RomSet set)
	{
		cbRomSets.removeActionListener(rsListener);
		cbRomSets.setSelectedItem(set);
		cbRomSets.addActionListener(rsListener);
	}
}