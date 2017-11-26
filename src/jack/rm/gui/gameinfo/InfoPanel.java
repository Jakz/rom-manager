package jack.rm.gui.gameinfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetData;
import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.game.attributes.RomAttribute;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.ui.Icon;

import jack.rm.Main;
import jack.rm.data.romset.GameSetManager;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.data.romset.Settings;
import jack.rm.gui.Mediator;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.types.RomDownloaderPlugin;
import net.miginfocom.swing.MigLayout;

public class InfoPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private GameSet set = null;
	
	private final JPanel imagesPanel;
	
	enum Mode
	{
	  VIEW,
	  EDIT
	};
	
	Mode mode;
	
	private AttributeField buildField(Attribute attribute, boolean isReal)
	{
	  if (attribute == GameAttribute.LANGUAGE)
	    return new LanguageAttributeField(this, attribute, isReal);
	  else if (attribute == GameAttribute.LOCATION)
      return new LocationAttributeField(this, attribute, isReal);
	  else if (attribute.getClazz() != null && attribute.getClazz().isEnum())
	    return new EnumAttributeField(this, attribute, isReal);
	  else
	    return new TextAttributeField(this, attribute, isReal);
	}
	
	private java.util.List<AttributeField> fields;

	final private JPanel pFields = new JPanel();
	final private JPanel pTotal = new JPanel();
	final private AttachmentTable attachments = new AttachmentTable();
	final private ClonesEnumPanel clonesTable;
	final private RomTable romTable;
	
	private AssetImage[] images;
	
	final private JButton downloadButton = new JButton("Download ROM");
	final private JButton assetsButton = new JButton("Download Assets");
	final private JButton openFolderButton = new JButton("Open Folder");
	final private JButton openArchiveButton = new JButton("Open Archive");
	
	final private JToggleButton editButton;
	final private JButton resetCustomFieldsButton;
	final private JButton addCustomFieldButton;
	final private JPopupMenu customPopup;
	
	final private JPanel buttons = new JPanel();
	
	private boolean showAttachmentsTable;
	private boolean showClonesTable;
		
	Game game;
	
	private class AssetImage
	{
	  final Asset asset;
	  final JLabel image;
	  
	  AssetImage(Asset asset)
	  {
	    this.asset = asset;
	    
	    image = new JLabel();
	    image.setHorizontalAlignment(SwingConstants.CENTER);
	    image.setBorder(BorderFactory.createLineBorder(Color.black));
	    image.setForeground(Color.RED);
	    image.setFont(image.getFont().deriveFont(30.0f));
	  }
	}
		
	public InfoPanel(Mediator mediator)
	{	  
	  clonesTable = new ClonesEnumPanel(mediator);
	  romTable = new RomTable();

	  
	  editButton = new JToggleButton(Icon.EDIT.getIcon());
    editButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    editButton.setToolTipText("Switch between edit and normal mode");
    
    editButton.addActionListener(e -> {
      if (game != null)
      {    
        mode = editButton.isSelected() ? Mode.EDIT : Mode.VIEW;
        
        for (AttributeField field : fields)
        {
          if (mode == Mode.EDIT)
            field.enableEdit();
          else
            field.finishEdit();
        }
      }
    });
    
    resetCustomFieldsButton = new JButton(Icon.DELETE.getIcon());
    resetCustomFieldsButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    resetCustomFieldsButton.setToolTipText("Reset all custom attributes to default");
    resetCustomFieldsButton.addActionListener( e -> fields.stream().forEach(AttributeField::clearCustomAttribute) );
    
    addCustomFieldButton = new JButton(Icon.ADD.getIcon());
    addCustomFieldButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    addCustomFieldButton.setToolTipText("Add a custom attribute to the romset");    
    
    customPopup = new JPopupMenu();
    
    addCustomFieldButton.addMouseListener(new MouseAdapter(){
      public void mousePressed(MouseEvent e) {
        addCustomFieldButton.doClick();
        customPopup.show(e.getComponent(), e.getX(), e.getY());
      }
    });

	  openFolderButton.setEnabled(false);
		openArchiveButton.setEnabled(false);
	  assetsButton.setEnabled(false);
	  downloadButton.setEnabled(false);
		
    imagesPanel = new JPanel();
		
    pFields.setLayout(new BorderLayout());
		JPanel pFields2 = new JPanel(new BorderLayout());
		pFields2.add(pFields, BorderLayout.NORTH);
		
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		buttons.add(downloadButton);
		buttons.add(assetsButton);
		buttons.add(openFolderButton);
		buttons.add(openArchiveButton);
		downloadButton.addActionListener(this);
		assetsButton.addActionListener(this);
		openFolderButton.addActionListener(this);
		openArchiveButton.addActionListener(this);
		
    pTotal.setLayout(new BoxLayout(pTotal, BoxLayout.PAGE_AXIS));

		
		this.add(pTotal);
	}
	
	public void toggleAttachmentsTable(boolean visible) { showAttachmentsTable = visible; }
	
	void buildMainLayout()
	{
	  pTotal.removeAll();
    pTotal.add(imagesPanel);
    JPanel pFields2 = new JPanel(new BorderLayout());
    pFields2.add(pFields, BorderLayout.NORTH);
    pTotal.add(pFields2);
    
    if (!set.hasFeature(Feature.SINGLE_ROM_PER_GAME))
      pTotal.add(new JScrollPane(romTable));
    
    if (showClonesTable)
      pTotal.add(clonesTable);
    
    pTotal.add(buttons);
   
    if (showAttachmentsTable)
      pTotal.add(attachments);
    
    revalidate();
	}
	
	public void buildPopupMenu()
	{
	  customPopup.removeAll();
	  
	  JMenu embedded = new JMenu("Embedded");
	  customPopup.add(embedded);
	  JMenu custom = new JMenu("Custom");
	  customPopup.add(custom);
	  
	  Attribute[] cattributes = new Attribute[] {
	    GameAttribute.GENRE,
	    GameAttribute.TAG,
	    GameAttribute.EXPORT_TITLE
	  };
	  
    MyGameSetFeatures helper = set.helper();
	  Settings settings = helper.settings();
	  
	  List<Attribute> enabledAttribs = settings.getRomAttributes();
	  
	  Stream<Attribute> eattributes = Arrays.stream(set.getSupportedAttributes());
	  
	  Runnable menuItemPostAction = () -> {
	    buildMainLayout();
	    buildFields();
	    pFields.revalidate();
	    updateFields(game);
	    buildPopupMenu();
	  };
	  
	  for (Attribute cattrib : cattributes)
	  {
	    JMenuItem item = null;
	    if (enabledAttribs.contains(cattrib))
	    {
	      item = new JMenuItem("Remove \'"+cattrib.getCaption()+"\'");
	      item.addActionListener(e -> {
	        settings.getRomAttributes().remove(cattrib);
	        set.stream().forEach(r -> r.clearCustomAttribute(cattrib));
	        menuItemPostAction.run();
	      });
	    }
	    else
	    {
	      item = new JMenuItem("Add \'"+cattrib.getCaption()+"\'");
	      item.addActionListener(e -> {
	        settings.getRomAttributes().add(cattrib);
          menuItemPostAction.run();
	      });
	    }
	    
	    custom.add(item);
	  }
	  
	  eattributes.forEach(eattrib -> {
      JMenuItem item = null;
      if (enabledAttribs.contains(eattrib))
      {
        item = new JMenuItem("Hide \'"+eattrib.getCaption()+"\'");
        item.addActionListener(e -> {
          settings.getRomAttributes().remove(eattrib);
          menuItemPostAction.run();
        });
      }
      else
      {
        item = new JMenuItem("Show \'"+eattrib.getCaption()+"\'");
        item.addActionListener(e -> {
          List<Attribute> newAttributes = Arrays.stream(set.getSupportedAttributes())
          .filter(ee -> enabledAttribs.contains(ee) || ee == eattrib).collect(Collectors.toList());

          enabledAttribs.stream().filter(ee -> !Arrays.asList(set.getSupportedAttributes()).contains(ee)).forEach(newAttributes::add);       
          enabledAttribs.clear();
          enabledAttribs.addAll(newAttributes);
          
          menuItemPostAction.run();
        });
      }
      
      embedded.add(item);
	  });
	}
	
	void buildFields()
	{
    MyGameSetFeatures helper = set.helper();
	  Settings settings = helper.settings();
	  List<Attribute> attributes = settings.getRomAttributes();
    
    fields = attributes.stream().map( a -> buildField(a, true) ).collect(Collectors.toList());
    
    /* add file name and path attributes only if there is a single rom per game */
    if (set.hasFeature(Feature.SINGLE_ROM_PER_GAME))
    {
      // TODO: hardcoded for now
      fields.add(buildField(RomAttribute.CRC, false));

      fields.add(buildField(GameAttribute.PATH, false));
    }
        
    pFields.removeAll();
    
    pFields.setLayout(new MigLayout());
    
    for (AttributeField field : fields)
    {     
      pFields.add(field.title, "span 4");

      if (field.deleteButton != null)
      {
        pFields.add(field.getComponent(), "span 8, growx");
        pFields.add(field.deleteButton, "wrap");
      }
      else
        pFields.add(field.getComponent(), "span 9, growx, wrap");
    }
    
    pFields.add(addCustomFieldButton);
    pFields.add(editButton);
    pFields.add(resetCustomFieldsButton);
	}
		
	public void romSetLoaded(final GameSet set)
	{
		mode = Mode.VIEW;
	  
	  this.set = set;
		
		AssetManager manager = set.getAssetManager();
		Asset[] assets = manager.getSupportedAssets();
		
		showClonesTable = set.hasFeature(Feature.CLONES); // TODO && uiSettings.showClonesTable
		
		buildMainLayout();
		buildPopupMenu();
		
		clonesTable.gameSetLoaded(set);
		
		if (assets.length == 0)
		{
		  images = new AssetImage[0];
		  imagesPanel.removeAll();
		  imagesPanel.revalidate();
		}
		else
		{
      images = new AssetImage[] { new AssetImage(assets[0]), new AssetImage(assets[1]) };
      
    	  SwingUtilities.invokeLater(new Runnable() {
    			@Override
          public void run() {
    				imagesPanel.removeAll();
    				
    		    imagesPanel.add(images[0].image);
    		    imagesPanel.add(Box.createRigidArea(new Dimension(30,0)));
    		    imagesPanel.add(images[1].image);
    		    
    		    for (AssetImage image : images)
    		    {
    		      image.image.setPreferredSize(((Asset.Image)image.asset).getSize());
    		      image.image.revalidate();
    		    }
    		    
    		    imagesPanel.revalidate();
    			}
    		});
		}
		
		buildFields();
	}
	
	void setImage(Game rom, Asset asset, JLabel dest)
	{
		AssetData data = rom.getAssetData(asset);
		
		if (data.isPresent())
		{
		  Asset.Image imageAsset = (Asset.Image)asset;
	    Dimension size = imageAsset.getSize();
	    ImageIcon i = data.asImage();

			Image img = i.getImage();
			BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 0, 0, size.width, size.height, null);
			
			dest.setText("");
			dest.setIcon(new ImageIcon(bi));
		}
		else
		{
			dest.setText("Asset Missing");
			dest.setIcon(null);
		}
	}
	
	public void resetFields()
	{
		for (AttributeField field : fields)
		  field.clear();

		for (AssetImage image : images)
		{
		  image.image.setIcon(null);
		  image.image.setText("");
		}
	}
	
	public void updateFields(Game game)
	{
		this.game = game;
		attachments.setRom(game);
		clonesTable.update(game);
		romTable.update(game);
		
		this.setVisible(true);
		
    for (AttributeField field : fields)
    {
      try { field.setValue(game); }
      catch (NullPointerException e)
      {
        field.clear();  
        //throw new RuntimeException(String.format("Attribute %s of %s is null", field.attrib.name(), rom.getTitle()));
      }
    }

    if (game != null)
    {
      for (AssetImage image : images)
        setImage(game, image.asset, image.image);
		
      // TODO: missing management for INCOMPLETE
    		if (game.getStatus() == GameStatus.MISSING)
    		{
    		  openFolderButton.setEnabled(false);
    		  openArchiveButton.setEnabled(false);
    			
    	    MyGameSetFeatures helper = set.helper();
    		  downloadButton.setEnabled(helper.settings().hasDownloader(set.platform()));
    		}
    		else
    		{
    	    openFolderButton.setEnabled(true);
    	    // TODO: different management for multiple roms per game
    	    /*if (game.getHandle().isArchive())
    	      openArchiveButton.setEnabled(true);*/
    	      
    		  downloadButton.setEnabled(false);
    		}
    		
    }
    
    assetsButton.setEnabled(game != null && !game.hasAllAssets());
	}
	
	@Override
  public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
	  
	  if (src == downloadButton)
		{
			try
			{
		    MyGameSetFeatures helper = set.helper();
			  Set<RomDownloaderPlugin> downloaders = helper.settings().plugins.getEnabledPlugins(PluginRealType.ROM_DOWNLOADER);
				
				URL url = downloaders.stream().filter( p -> p.isPlatformSupported(set.platform())).findFirst().get().getDownloadURL(set.platform(), game);
			  
			  Desktop.getDesktop().browse(url.toURI());
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
			}
		}
	  else if (src == openFolderButton)
	  {
	    Main.openFolder(game.rom().handle().path().getParent().toFile());
	    //TODO: Main.openFolder(rom.getHandle().path().getParent().toFile());
	  }
	  else if (src == openArchiveButton)
	  {
	    //TODO: Main.openFolder(rom.getHandle().path().toFile());
	  }
		else if (src == assetsButton)
		{
			if (game != null)
			  Main.downloader.downloadArt(game);
		}
	}
}
