package jack.rm.gui;

import jack.rm.*;
import jack.rm.assets.Asset;
import jack.rm.assets.AssetManager;
import jack.rm.data.*;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.RomSet;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.downloader.RomDownloaderPlugin;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.net.URL;

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
	
	private class AttributeField implements CaretListener, ActionListener
	{
	  private JLabel title;
	  private JTextField value;
	  
	  private JButton deleteButton;
	  
	  private RomAttribute attrib;
	  private Border defaultBorder;
	  private Color defaultColor;
	  
	  Object parseValue()
	  {
	    if (attrib.clazz == String.class)
	      return value.getText();
	    else if (attrib.clazz == Integer.class)
	    {
	      try {
	        return Integer.parseInt(value.getText());
	      } catch (Exception e) { return null; }
	    }
	    
	    return null;
	  }
	  
	  AttributeField(RomAttribute attrib, boolean isReal)
	  {
	    title = new JLabel();
	    value = new JTextField(40);
	    this.attrib = attrib;
	    
	    if (isReal)
	      value.setFont(value.getFont().deriveFont(Font.BOLD, 14.0f)); 
	    
	    title.setHorizontalAlignment(SwingConstants.RIGHT);
	    title.setText(attrib.caption.text());
	    
	    defaultBorder = value.getBorder();
	    Insets insets = defaultBorder.getBorderInsets(value);
	    value.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
	    value.setEditable(false);
	    Color color = UIManager.getColor("Panel.background");
	    defaultColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
	    value.setBackground(defaultColor);

	    deleteButton = new JButton();
	    deleteButton.setIcon(Icon.DELETE.getIcon());
	    deleteButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
	    deleteButton.setVisible(false);
	    deleteButton.addActionListener( e -> {
	      rom.clearCustomAttribute(attrib);
	      setValue(rom);
	      value.setBackground(Color.WHITE);
	    });
	    
	  }
	  
	  void enableEdit()
	  {
      if (attrib.clazz != null)
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
	    if (attrib.clazz != null)
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
	  
	  void clearCustomAttribute()
	  {
	    if (rom.hasCustomAttribute(attrib))
	    {
	      rom.clearCustomAttribute(attrib);
	      setValue(rom);
	      Main.mainFrame.romListModel.fireChanges(Main.mainFrame.list.getSelectedIndex());
	    }
	    deleteButton.setVisible(false);
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
	      Main.mainFrame.romListModel.fireChanges(Main.mainFrame.list.getSelectedIndex());
	    }
	    else
	      setValue(rom);
	  }
	  
	  void setValue(Rom rom)
	  {
	    if (attrib == RomAttribute.PATH)
	      value.setText(rom.getPath() != null ? rom.getPath().file().getParent().toString() : "");
	    else if (attrib == RomAttribute.FILENAME)
	      value.setText(rom.getPath() != null ? rom.getPath().file().getFileName().toString() : "");
	    else
	      value.setText(attrib.prettyValue(rom.getAttribute(attrib)));

	    deleteButton.setVisible(mode == Mode.EDIT && rom.hasCustomAttribute(attrib));
	  }
	}

  private java.util.List<AttributeField> fields;

	final private JPanel pFields = new JPanel();
	final private JPanel pTotal = new JPanel();
	
	private AssetImage[] images;
	
	final private JButton downloadButton = new JButton("Download ROM");
	final private JButton assetsButton = new JButton("Download Assets");
	final private JButton openFolderButton = new JButton("Open Folder");
	final private JButton openArchiveButton = new JButton("Open Archive");
	
	final private JToggleButton editButton;
	final private JButton resetCustomFieldsButton;
	
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
    
    resetCustomFieldsButton = new JButton(Icon.DELETE.getIcon());
    resetCustomFieldsButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    resetCustomFieldsButton.setToolTipText("Reset all custom attributes to default");
    resetCustomFieldsButton.addActionListener( e -> fields.stream().forEach(AttributeField::clearCustomAttribute) );
    
    editButton.addActionListener(e -> {
      mode = editButton.isSelected() ? Mode.EDIT : Mode.VIEW;
      
      for (AttributeField field : fields)
      {
        if (mode == Mode.EDIT)
          field.enableEdit();
        else
          field.finishEdit();
      }
    });
    
	  
	  openFolderButton.setEnabled(false);
		openArchiveButton.setEnabled(false);
	  
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
		
		this.add(pTotal);
	}
	
	public void romSetLoaded(final RomSet set)
	{
		mode = Mode.VIEW;
	  
	  this.set = set;
		
		AssetManager manager = set.getAssetManager();
		Asset[] assets = manager.getSupportedAssets();
		
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
		
	  RomAttribute[] attributes = set.getSupportedAttributes();
	  
	  fields = Arrays.stream(attributes).map( a -> new AttributeField(a, true) ).collect(Collectors.toList());
	  fields.add(new AttributeField(RomAttribute.FILENAME, false));
	  fields.add(new AttributeField(RomAttribute.PATH, false));
	  	  
	  pFields.removeAll();
	  
	  pFields.setLayout(new MigLayout());
	  
	  for (AttributeField field : fields)
	  {	    
	    pFields.add(field.title, "span 4");

	    if (field.deleteButton != null)
	    {
	      pFields.add(field.value, "span 8, growx");
	      pFields.add(field.deleteButton, "wrap");
	    }
	    else
	      pFields.add(field.value, "span 9, growx, wrap");
	  }
	  
	  pFields.add(editButton);
	  pFields.add(resetCustomFieldsButton);
	}
	
	void setImage(Rom rom, Asset asset, JLabel dest)
	{
		Path path = set.getAssetPath(asset, rom);
		Asset.Image imageAsset = (Asset.Image)asset;

		long crc = rom.getAssetData(asset).getCRC();
		boolean shouldCheckCRC = asset.hasCRC();
		Dimension size = imageAsset.getSize();

		if (Files.exists(path) && (!shouldCheckCRC || crc == Scanner.computeCRC(path)))
		{
			ImageIcon i = new ImageIcon(path.toString());
			
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
		  field.value.setText("");

		for (AssetImage image : images)
		{
		  image.image.setIcon(null);
		  image.image.setText("");
		}
	}
	
	public void updateFields(Rom rom)
	{
		this.rom = rom;
		
		this.setVisible(true);
		
    for (AttributeField field : fields)
    {
      try { field.setValue(rom); }
      catch (NullPointerException e)
      {
        throw new RuntimeException(String.format("Attribute %s of %s is null", field.attrib.name(), rom.getTitle()));
      }
    }

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
		
		assetsButton.setEnabled(!rom.hasAllAssets());

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
			Rom r = rom;
			Main.downloader.downloadArt(r);
		}
	}
}
