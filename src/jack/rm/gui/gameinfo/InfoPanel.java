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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetData;
import com.github.jakz.romlib.data.assets.AssetKind;
import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.assets.AssetType;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.game.attributes.RomAttribute;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.ui.Icon;
import com.pixbits.lib.io.FileUtils;

import jack.rm.Main;
import jack.rm.data.romset.GameSetManager;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.data.romset.Settings;
import jack.rm.gui.Mediator;
import jack.rm.gui.resources.CartridgeTemplate;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.types.DataFetcherPlugin;
import jack.rm.plugins.types.RomDownloaderPlugin;
import net.miginfocom.swing.MigLayout;

public class InfoPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final Mediator mediator;
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
	  else if (attribute.getType() != null && attribute.getType().isEnum())
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
		
	final private String[] buttonLabels = new String[] { "Download ROM", "Download Assets", "Open Folder", "Open Archive", "Forget Status" };
	final private JButton[] buttons;
    final private JPanel buttonsPanel = new JPanel();
	
	final private JToggleButton editButton;
	final private JButton resetCustomFieldsButton;
	final private JButton addCustomFieldButton;
	final private JButton assetsButton;
	final private JPopupMenu customPopup;
	final private JPopupMenu assetsPopup;
	
	
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
	  this.mediator = mediator;
	  
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
    assetsPopup = new JPopupMenu();
    assetsButton = new JButton("Assets");
    assetsButton.setToolTipText("Choose visible asset slots");
    assetsButton.addActionListener(e -> {
      buildAssetsPopup();
      assetsPopup.show(assetsButton, 0, assetsButton.getHeight());
    });

    addCustomFieldButton.addMouseListener(new MouseAdapter(){
      public void mousePressed(MouseEvent e) {
        addCustomFieldButton.doClick();
        customPopup.show(e.getComponent(), e.getX(), e.getY());
      }
    });
       
    buttons = new JButton[buttonLabels.length];
    buildButtons();
    
    imagesPanel = new JPanel();
		
    pFields.setLayout(new BorderLayout());
		JPanel pFields2 = new JPanel(new BorderLayout());
		pFields2.add(pFields, BorderLayout.NORTH);

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
      {
        JScrollPane pane = new JScrollPane(romTable);
        pane.setPreferredSize(new Dimension(400, 200));
        pTotal.add(pane);
      }
      
      if (showClonesTable)
        pTotal.add(clonesTable);
      
      pTotal.add(buttonsPanel);
     
      if (showAttachmentsTable)
        pTotal.add(attachments);
    
    revalidate();
	}
	
	public void buildButtons()
	{
    buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
    for (int i = 0; i < buttonLabels.length; ++i)
    {
      buttons[i] = new JButton(buttonLabels[i]);
      buttonsPanel.add(buttons[i]);
      buttons[i].setEnabled(false);
    }
    buttonsPanel.add(assetsButton);
    
    buttons[0].addActionListener(e -> {
      try
      {
        MyGameSetFeatures helper = set.helper();
        Set<RomDownloaderPlugin> downloaders = helper.settings().getEnabledPluginsOfType(PluginRealType.ROM_DOWNLOADER);
        
        URL url = downloaders.stream().filter( p -> p.isPlatformSupported(set.platform())).findFirst().get().getDownloadURL(set.platform(), game);
        
        Desktop.getDesktop().browse(url.toURI());
      }
      catch (Exception ee)
      {
        ee.printStackTrace();
      }
    });
    
    buttons[1].addActionListener(e -> {
      if (game != null)
      {
        MyGameSetFeatures helper = set.helper();
        List<DataFetcherPlugin> plugins = helper.settings()
            .<DataFetcherPlugin>getEnabledPluginsOfType(PluginRealType.DATA_FETCHER)
            .stream()
            .filter(DataFetcherPlugin::supportsAssetDownload)
            .sorted((left, right) -> left.getInfo().name.compareToIgnoreCase(right.getInfo().name))
            .collect(Collectors.toList());

        if (plugins.size() == 1)
        {
          plugins.get(0).searchAssetsForGame(game, AssetType.IMAGE);
        }
        else if (!plugins.isEmpty())
        {
          JPopupMenu sources = new JPopupMenu();
          for (DataFetcherPlugin plugin : plugins)
          {
            JMenuItem source = new JMenuItem(plugin.getInfo().name);
            source.setToolTipText(plugin.getInfo().description);
            source.addActionListener(event -> plugin.searchAssetsForGame(game, AssetType.IMAGE));
            sources.add(source);
          }
          sources.show(buttons[1], 0, buttons[1].getHeight());
        }
      }
  
      //if (game != null)
      //  Main.downloader.downloadArt(game);
    });
    
    buttons[2].addActionListener(e -> {
      Main.openFolder(game.rom().handle().path().getParent().toFile());
      //TODO: Main.openFolder(rom.getHandle().path().getParent().toFile());
    });
    
    buttons[3].addActionListener(e -> {
      //TODO: Main.openFolder(rom.getHandle().path().toFile());
    });
    
    buttons[4].addActionListener(e -> {
      if (game != null)
      {
        game.forgetStatus();
        SwingUtilities.invokeLater(() -> mediator.repaint());
      }
    });
	}

	private void buildAssetsPopup()
	{
	  assetsPopup.removeAll();

	  if (set == null)
	    return;

	  MyGameSetFeatures helper = set.helper();
	  Settings settings = helper.settings();
	  List<AssetKind> addedKinds = new java.util.ArrayList<>();

	  for (Asset asset : set.getAssetManager().getSupportedAssets())
	  {
	    AssetKind kind = asset.getKind();
	    if (kind == AssetKind.UNKNOWN || addedKinds.contains(kind))
	      continue;

	    JCheckBoxMenuItem item = new JCheckBoxMenuItem(kind.getCaption(), settings.isAssetKindVisible(kind));
	    item.addActionListener(e -> {
	      settings.setAssetKindVisible(kind, item.isSelected());
	      rebuildAssetImages();
	      layoutImages();
	      updateFields(game);
	    });
	    assetsPopup.add(item);
	    addedKinds.add(kind);
	  }

    boolean supportsCartridge = Arrays.stream(set.getAssetManager().getSupportedAssets())
        .anyMatch(asset -> asset.getKind() == AssetKind.CARTRIDGE);
    if (supportsCartridge)
    {
      assetsPopup.addSeparator();
      JMenu templates = new JMenu("Cartridge/media style");
      ButtonGroup group = new ButtonGroup();
      JRadioButtonMenuItem automatic = new JRadioButtonMenuItem("Automatic for platform",
          settings.getCartridgeTemplateId() == null);
      automatic.addActionListener(e -> {
        settings.setCartridgeTemplateId(null);
        settings.setRenderCartridgeTemplate(true);
        settings.setAssetKindVisible(AssetKind.CARTRIDGE, true);
        rebuildAssetImages();
        layoutImages();
        updateFields(game);
      });
      group.add(automatic);
      templates.add(automatic);

      List<CartridgeTemplate> recommended = CartridgeTemplate.recommendedFor(set.platform());
      if (!recommended.isEmpty())
      {
        templates.addSeparator();
        for (CartridgeTemplate template : recommended)
          addCartridgeTemplateItem(templates, group, template, settings);
      }

      templates.addSeparator();
      for (CartridgeTemplate template : CartridgeTemplate.values())
        if (!recommended.contains(template))
          addCartridgeTemplateItem(templates, group, template, settings);

      assetsPopup.add(templates);
    }
	}

  private void addCartridgeTemplateItem(JMenu menu, ButtonGroup group, CartridgeTemplate template, Settings settings)
  {
    JRadioButtonMenuItem item = new JRadioButtonMenuItem(template.caption(),
        template.id().equals(settings.getCartridgeTemplateId()));
    item.addActionListener(e -> {
      settings.setCartridgeTemplateId(template.id());
      settings.setRenderCartridgeTemplate(true);
      settings.setAssetKindVisible(AssetKind.CARTRIDGE, true);
      rebuildAssetImages();
      layoutImages();
      updateFields(game);
    });
    group.add(item);
    menu.add(item);
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

		showClonesTable = set.hasFeature(Feature.CLONES); // TODO && uiSettings.showClonesTable

		buildMainLayout();
		buildPopupMenu();

		clonesTable.gameSetLoaded(set);

		rebuildAssetImages();
		layoutImages();

		buildFields();
	}

	private void rebuildAssetImages()
	{
	  AssetManager manager = set.getAssetManager();
	  MyGameSetFeatures helper = set.helper();
	  Settings settings = helper.settings();

	  images = Arrays.stream(manager.getSupportedAssets())
	      .filter(asset -> asset.getType() == AssetType.IMAGE)
	      .filter(asset -> asset.getKind() == AssetKind.UNKNOWN || settings.isAssetKindVisible(asset.getKind()))
	      .map(AssetImage::new)
	      .toArray(AssetImage[]::new);
	}

	private void layoutImages()
	{
	  imagesPanel.removeAll();

	  for (int i = 0; i < images.length; ++i)
	  {
	    if (i > 0)
	      imagesPanel.add(Box.createRigidArea(new Dimension(30,0)));

	    imagesPanel.add(images[i].image);
	  }

	  for (AssetImage image : images)
	  {
	    image.image.setPreferredSize(((Asset.Image)image.asset).getSize());
	    image.image.revalidate();
	  }

	  imagesPanel.revalidate();
	  imagesPanel.repaint();
	}

  public void refreshAssetView()
  {
    if (set == null)
      return;

    rebuildAssetImages();
    layoutImages();

    if (game != null)
      updateFields(game);
	}

	void setImage(Game rom, Asset asset, JLabel dest)
	{
		AssetData data = rom.getAssetData(asset);
		restoreAssetData(rom, asset, data);

    MyGameSetFeatures helper = set.helper();
    Settings settings = helper.settings();
    if (asset.getKind() == AssetKind.CARTRIDGE && settings.shouldRenderCartridgeTemplate())
    {
      CartridgeTemplate template = CartridgeTemplate.byId(settings.getCartridgeTemplateId())
          .orElseGet(() -> CartridgeTemplate.defaultFor(set.platform())
              .orElse(CartridgeTemplate.COMPACT_WHITE_CARD));
      Image label = null;
      if (data.isPresent())
      {
        ImageIcon labelIcon = data.asImage();
        if (labelIcon != null)
          label = labelIcon.getImage();
      }

      Dimension size = ((Asset.Image)asset).getSize();
      BufferedImage rendered = template.layout().render(label, size);
      dest.setText("");
      dest.setIcon(new ImageIcon(rendered));
      dest.setToolTipText(template.caption() + (label == null ? " (label missing)" : ""));
      return;
    }

		if (data.isPresent())
		{
		  Asset.Image imageAsset = (Asset.Image)asset;
	    Dimension size = imageAsset.getSize();
	    ImageIcon i = data.asImage();

			Image img = i.getImage();
			BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			int imageWidth = i.getIconWidth();
			int imageHeight = i.getIconHeight();
			double scale = Math.min(size.width / (double)imageWidth, size.height / (double)imageHeight);
			int drawWidth = Math.max(1, (int)(imageWidth * scale));
			int drawHeight = Math.max(1, (int)(imageHeight * scale));
			int x = (size.width - drawWidth) / 2;
			int y = (size.height - drawHeight) / 2;
			g.drawImage(img, x, y, drawWidth, drawHeight, null);

			dest.setText("");
			dest.setIcon(new ImageIcon(bi));
			dest.setToolTipText(asset.getKind().getCaption());
		}
		else
		{
			dest.setText("Asset Missing");
			dest.setIcon(null);
			dest.setToolTipText(null);
		}
	}

	private void restoreAssetData(Game rom, Asset asset, AssetData data)
	{
	  try
	  {
	    if (!data.getPath().equals(Paths.get(".")) && data.isPresent())
	      return;

	    Path path = Paths.get(Asset.safeName(rom.getCorrectName()) + ".png");
	    data.setPath(path);

	    Path finalPath = data.getFinalPath();
	    if (Files.exists(finalPath))
	      data.setCRC(FileUtils.calculateCRCFast(finalPath));
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
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
      
      buttons[4].setEnabled(game.getStatus() != GameStatus.MISSING);
		
      // TODO: missing management for INCOMPLETE
    		if (game.getStatus() == GameStatus.MISSING)
    		{
    		  buttons[2].setEnabled(false);
    		  buttons[3].setEnabled(false);
    			
    	    MyGameSetFeatures helper = set.helper();
    		  buttons[0].setEnabled(helper.settings().hasDownloader(set.platform()));
    		}
    		else
    		{
    		  buttons[3].setEnabled(true);
    	    // TODO: different management for multiple roms per game
    	    /*if (game.getHandle().isArchive())
    	      openArchiveButton.setEnabled(true);*/
    	      
    	    buttons[0].setEnabled(false);
    	    
    	    
    		}
    		
    }
    
    buttons[1].setEnabled(game != null);
    //buttons[1].setEnabled(game != null && !game.hasAllAssets());
	}
}
