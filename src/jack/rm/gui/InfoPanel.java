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
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.net.URL;

public class InfoPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private RomSet set = null;
	
	private final JPanel imagesPanel;
	
	private static class AttributeField
	{
	  private JLabel title;
	  private JLabel value;
	  private RomAttribute attrib;
	  
	  AttributeField(RomAttribute attrib, boolean isReal)
	  {
	    title = new JLabel();
	    value = new JLabel();
	    this.attrib = attrib;
	    
	    if (isReal)
	      value.setFont(value.getFont().deriveFont(Font.BOLD, 14.0f));
	    
	    value.setBackground(new Color(220,220,220));
	    
	    title.setHorizontalAlignment(SwingConstants.RIGHT);
	    title.setText(attrib.caption.text());
	  }
	  
	  void setValue(Rom rom)
	  {
	    if (attrib == RomAttribute.PATH)
	      value.setText(rom.getPath() != null ? rom.getPath().file().getParent().toString() : "");
	    else if (attrib == RomAttribute.FILENAME)
	      value.setText(rom.getPath() != null ? rom.getPath().file().getFileName().toString() : "");
	    else
	      value.setText(attrib.prettyValue(rom.getAttribute(attrib)));
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
	    pFields.add(field.title);
	    pFields.add(field.value, "wrap");
	  }
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
