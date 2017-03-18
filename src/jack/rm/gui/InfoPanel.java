package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import jack.rm.Main;
import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.assets.AssetManager;
import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.downloader.RomDownloaderPlugin;
import net.miginfocom.swing.MigLayout;

public class InfoPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private RomSet set = null;
	
	private final JPanel imagesPanel;
	
	private enum Mode
	{
	  VIEW,
	  EDIT
	};
	
	private Mode mode;
	
	private AttributeField buildField(Attribute attribute, boolean isReal)
	{
	  if (attribute.getClazz() != null && attribute.getClazz().isEnum())
	    return new EnumAttributeField(attribute, isReal);
	  else
	    return new TextAttributeField(attribute, isReal);
	}
	
	private class TextAttributeField extends AttributeField implements CaretListener, ActionListener
	{
	  private JTextField value;
	  
	  private Border defaultBorder;
	  private Color defaultColor;
	  
	  JComponent getComponent() { return value; }
	  
	  Object parseValue()
	  {
       if (attrib.getClazz() == String.class)
          return value.getText();
        else if (attrib.getClazz() == Integer.class)
        {
          try {
            return Integer.parseInt(value.getText());
          } catch (Exception e) { return null; }
        }
        
        return null;
	  }
	  
	  TextAttributeField(Attribute attrib, boolean isReal)
	  {
	    super(attrib, isReal);
	    
	    value = new JTextField(40);
      
      if (isReal)
        value.setFont(value.getFont().deriveFont(Font.BOLD, 12.0f)); 

      defaultBorder = value.getBorder();
      Insets insets = defaultBorder.getBorderInsets(value);
      value.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
      value.setEditable(false);
      Color color = UIManager.getColor("Panel.background");
      defaultColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
      value.setBackground(defaultColor);
	  }
	  
	  void attributeCleared()
	  {
      value.setBackground(Color.WHITE);
	  }
	  
	  void enableEdit()
	  {
      if (attrib.getClazz() != null)
      {
        value.setEditable(true);
        value.setBorder(defaultBorder);
        value.setBackground(Color.WHITE);     
        value.addCaretListener(this);
        value.addActionListener(this);
        
        deleteButton.setVisible(rom.hasCustomAttribute(attrib));
      }
	  }
	  
	  void finishEdit()
    {
      if (attrib.getClazz() != null)
      {
        Insets insets = defaultBorder.getBorderInsets(value);
        value.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
        value.setEditable(false);
        value.setBackground(defaultColor);
        value.removeCaretListener(this);
        value.removeActionListener(this);
      
        deleteButton.setVisible(false);
      }
    }
   
    public void caretUpdate(CaretEvent e)
    {
      Object pv = parseValue();
       
      if (pv != null && !pv.equals(rom.getAttribute(attrib)))
        value.setBackground(new Color(255,175,0));
      else
        value.setBackground(Color.WHITE);
    }
     
    public void actionPerformed(ActionEvent e)
    {
      Object pv = parseValue();
       
      if (pv != null && !pv.equals(rom.getAttribute(attrib)))
      {
        rom.setCustomAttribute(attrib, pv);
        value.setBackground(Color.WHITE);
        deleteButton.setVisible(true);
        rom.updateStatus();
        Main.mainFrame.romListModel.fireChanges(Main.mainFrame.list.getSelectedIndex());
      }
      else
        setValue(rom);
    }
    
    void setValue(Rom rom)
    {
      deleteButton.setVisible(mode == Mode.EDIT && rom.hasCustomAttribute(attrib));

      
      if (attrib == RomAttribute.PATH)
        value.setText(rom.getPath() != null ? rom.getPath().toString() : "");
      else if (attrib == RomAttribute.FILENAME)
        value.setText(rom.getPath() != null ? rom.getPath().file().getFileName().toString() : "");
      else
        value.setText(attrib.prettyValue(rom.getAttribute(attrib)));

    }
    
    void clear()
    {
      value.setText("");
    }
	}
	
	private class EnumAttributeField extends AttributeField
	{
	  private JComboBox<Enum<?>> value;
	  private JTextField readValue;
	  private JPanel panel;
	  
	  //@SuppressWarnings("unchecked")
	  EnumAttributeField(Attribute attrib, boolean isReal)
	  {
	    super(attrib, isReal);
	    value = new JComboBox<Enum<?>>();
	    readValue = new JTextField(40);
	    
      Color color = UIManager.getColor("Panel.background");
      Color tmpColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
      readValue.setBackground(tmpColor);
      readValue.setEditable(false);
      Insets insets = readValue.getBorder().getBorderInsets(readValue);
      readValue.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
      
      value.addItemListener(e -> {
	      if (e.getStateChange() == ItemEvent.SELECTED)
	      {
	        rom.setCustomAttribute(attrib, value.getSelectedItem());
	        deleteButton.setVisible(true);
	        rom.updateStatus();
	        readValue.setText(value.getSelectedItem().toString());
	        Main.mainFrame.romListModel.fireChanges(Main.mainFrame.list.getSelectedIndex());
	      }
	    });
	    
	    panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
	    panel.add(readValue);
	    
      if (isReal)
      {
        value.setFont(value.getFont().deriveFont(Font.BOLD, 14.0f));
        readValue.setFont(value.getFont().deriveFont(Font.BOLD, 14.0f)); 
      }
	    
	    try
	    {
	      Enum<?>[] values = (Enum<?>[])attrib.getClazz().getMethod("values").invoke(null);
	      Arrays.sort(values, (o1, o2) -> o1.toString().compareTo(o2.toString()));
	      value.addItem(null);
	      for (Enum<?> v : values)
	        value.addItem(v);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    
	  }
	  
	  public JComponent getComponent() { return panel; }
	  
	  public void clear() { value.setSelectedIndex(-1); }
	  
	  public void attributeCleared()
	  {
	    
	  }
	  
	  public void enableEdit()
	  {
	    panel.remove(readValue);
	    panel.add(value);
      panel.revalidate();
      deleteButton.setVisible(rom.hasCustomAttribute(attrib));

	  }
	  
	  public void finishEdit()
	  {
	    panel.remove(value);
	    panel.add(readValue);
	    panel.revalidate();
	    deleteButton.setVisible(false);
	  }
	  
	  public void setValue(Rom rom)
	  {
	    
	    Object ovalue = rom.getAttribute(attrib);
	    value.setSelectedItem(ovalue);
	    if (ovalue != null)
	    {
	      deleteButton.setVisible(mode == Mode.EDIT && rom.hasCustomAttribute(attrib));
	      readValue.setText(ovalue.toString());
	    }	    
	    else
	    {
	      deleteButton.setVisible(false);
	      readValue.setText("");
	    }
	  }
	  
	  public Object parseValue()
	  {
	    return value.getSelectedItem();
	  }
	}
	
	private abstract class AttributeField
	{
	  protected JLabel title;
	  protected Attribute attrib;

	  protected JButton deleteButton;
	  protected JButton moveUpButton;
	  protected JButton moveDownButton;

	  abstract Object parseValue();
	  
	  abstract void attributeCleared();
	  
	  abstract JComponent getComponent();
	  
	  AttributeField(Attribute attrib, boolean isReal)
	  {
	    this.attrib = attrib;
	    title = new JLabel();
	    title.setHorizontalAlignment(SwingConstants.RIGHT);
	    title.setText(attrib.getCaption());

	    deleteButton = new JButton();
	    deleteButton.setIcon(Icon.DELETE.getIcon());
	    deleteButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
	    deleteButton.setVisible(false);
	    deleteButton.addActionListener( e -> {
	      rom.clearCustomAttribute(attrib);
	      setValue(rom);
	      attributeCleared();
	    });
	    
	    moveUpButton = new JButton();
	    moveUpButton.setIcon(Icon.ARROW_UP.getIcon());
	    moveUpButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
	    moveUpButton.setVisible(false);
	    
	    moveDownButton = new JButton();
	    moveDownButton.setIcon(Icon.ARROW_DOWN.getIcon());
	    moveDownButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
	    moveDownButton.setVisible(false);
	  }
	  
    void clearCustomAttribute()
    {
      if (rom.hasCustomAttribute(attrib))
      {
        rom.clearCustomAttribute(attrib);
        setValue(rom);
        rom.updateStatus();
        Main.mainFrame.romListModel.fireChanges(Main.mainFrame.list.getSelectedIndex());
      }
      deleteButton.setVisible(false);
    }

	  abstract void enableEdit();
	  abstract void finishEdit();
	  

	  
	  abstract void setValue(Rom rom);
	  abstract void clear();
	}
	

  private java.util.List<AttributeField> fields;

	final private JPanel pFields = new JPanel();
	final private JPanel pTotal = new JPanel();
	final private AttachmentTable attachments = new AttachmentTable();
	
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
	
	private Rom rom;
	
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
		
	public InfoPanel()
	{
		editButton = new JToggleButton(Icon.EDIT.getIcon());
    editButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    editButton.setToolTipText("Switch between edit and normal mode");
    
    editButton.addActionListener(e -> {
      if (rom != null)
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

    pFields.setLayout(new BorderLayout());

		pTotal.setLayout(new BoxLayout(pTotal, BoxLayout.PAGE_AXIS));
		
    imagesPanel = new JPanel();
    pTotal.add(imagesPanel);
		
		JPanel pFields2 = new JPanel(new BorderLayout());
		pFields2.add(pFields, BorderLayout.NORTH);
		pTotal.add(pFields2);
		
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		buttons.add(downloadButton);
		buttons.add(assetsButton);
		buttons.add(openFolderButton);
		buttons.add(openArchiveButton);
		downloadButton.addActionListener(this);
		assetsButton.addActionListener(this);
		openFolderButton.addActionListener(this);
		openArchiveButton.addActionListener(this);
		
		pTotal.add(buttons);
		pTotal.add(attachments);
		
		this.add(pTotal);
	}
	
	public void buildPopupMenu()
	{
	  customPopup.removeAll();
	  
	  JMenu embedded = new JMenu("Embedded");
	  customPopup.add(embedded);
	  JMenu custom = new JMenu("Custom");
	  customPopup.add(custom);
	  
	  Attribute[] cattributes = new Attribute[] {
	    RomAttribute.GENRE,
	    RomAttribute.TAG
	  };
	  
	  List<Attribute> enabledAttribs = set.getSettings().getRomAttributes();
	  
	  Stream<Attribute> eattributes = Arrays.stream(set.getSupportedAttributes());
	  
	  Runnable menuItemPostAction = () -> {
	    buildFields();
	    pFields.revalidate();
	    updateFields(rom);
	    buildPopupMenu();
	  };
	  
	  for (Attribute cattrib : cattributes)
	  {
	    JMenuItem item = null;
	    if (enabledAttribs.contains(cattrib))
	    {
	      item = new JMenuItem("Remove \'"+cattrib.getCaption()+"\'");
	      item.addActionListener(e -> {
	        set.getSettings().getRomAttributes().remove(cattrib);
	        set.list.stream().forEach(r -> r.clearCustomAttribute(cattrib));
	        menuItemPostAction.run();
	      });
	    }
	    else
	    {
	      item = new JMenuItem("Add \'"+cattrib.getCaption()+"\'");
	      item.addActionListener(e -> {
	        set.getSettings().getRomAttributes().add(cattrib);
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
          set.getSettings().getRomAttributes().remove(eattrib);
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
    List<Attribute> attributes = set.getSettings().getRomAttributes();
    
    fields = attributes.stream().map( a -> buildField(a, true) ).collect(Collectors.toList());
    fields.add(buildField(RomAttribute.FILENAME, false));
    fields.add(buildField(RomAttribute.PATH, false));
        
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
		
	public void romSetLoaded(final RomSet set)
	{
		mode = Mode.VIEW;
	  
	  this.set = set;
		
		AssetManager manager = set.getAssetManager();
		Asset[] assets = manager.getSupportedAssets();
		
		buildPopupMenu();
		
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
	
	void setImage(Rom rom, Asset asset, JLabel dest)
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
	
	public void updateFields(Rom rom)
	{
		this.rom = rom;
		attachments.setRom(rom);
		
		this.setVisible(true);
		
    for (AttributeField field : fields)
    {
      try { field.setValue(rom); }
      catch (NullPointerException e)
      {
        field.clear();  
        //throw new RuntimeException(String.format("Attribute %s of %s is null", field.attrib.name(), rom.getTitle()));
      }
    }

    if (rom != null)
    {
      for (AssetImage image : images)
        setImage(rom, image.asset, image.image);
		
  		if (rom.status == RomStatus.MISSING)
  		{
  		  openFolderButton.setEnabled(false);
  		  openArchiveButton.setEnabled(false);
  			
  		  downloadButton.setEnabled(RomSet.current.getSettings().hasDownloader(RomSet.current.system));
  		}
  		else
  		{
  	    openFolderButton.setEnabled(true);
  	    if (rom.getPath().isArchive())
  	      openArchiveButton.setEnabled(true);
  	      
  		  downloadButton.setEnabled(false);
  		}
  		
  		assetsButton.setEnabled(rom != null && !rom.hasAllAssets());
    }

	}
	
	@Override
  public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
	  
	  if (src == downloadButton)
		{
			try
			{
				Set<RomDownloaderPlugin> downloaders = RomSet.current.getSettings().plugins.getEnabledPlugins(PluginRealType.ROM_DOWNLOADER);
				
				URL url = downloaders.stream().filter( p -> p.isSystemSupported(RomSet.current.system)).findFirst().get().getDownloadURL(RomSet.current.system, rom);
			  
			  Desktop.getDesktop().browse(url.toURI());
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
			}
		}
	  else if (src == openFolderButton)
	  {
	    Main.openFolder(rom.getPath().file().getParent().toFile());
	  }
	  else if (src == openArchiveButton)
	  {
	    Main.openFolder(rom.getPath().file().toFile());
	  }
		else if (src == assetsButton)
		{
			if (rom != null)
			  Main.downloader.downloadArt(rom);
		}
	}
}
