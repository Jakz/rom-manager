package jack.rm.gui;

import jack.rm.*;
import jack.rm.data.*;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.RomSet;
import jack.rm.i18n.Text;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.net.URI;

public class InfoPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private RomSet<?> set = null;
	
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
	  }
	}
	
  private static enum Field
  {
    TITLE        (0, Text.ROM_INFO_TITLE),
    PUBLISHER    (1, Text.ROM_INFO_PUBLISHER),
    LOCATION     (2, Text.ROM_INFO_LOCATION),
    LANGUAGES    (3, Text.ROM_INFO_LANGUAGES),
    SIZE         (4, Text.ROM_INFO_SIZE),
    SAVE_TYPE    (5, Text.ROM_INFO_SAVE_TYPE),
    GENRE        (6, Text.ROM_INFO_GENRE),
    CLONES       (7, Text.ROM_INFO_CLONES),
    CRC          (8, Text.ROM_INFO_CRC),
    /*INTERNAL_NAME(9, Text.ROM_INFO_INTERNAL_NAME),*/
    SERIAL       (9, Text.ROM_INFO_SERIAL),
    GROUP        (10, Text.ROM_INFO_GROUP),
    DUMP_DATE    (11, Text.ROM_INFO_DUMP_DATE),
    COMMENT      (12, Text.ROM_INFO_COMMENT),
    FILENAME     (13, Text.ROM_INFO_PATH),
    PATH         (14, Text.ROM_INFO_PATH)
    ;
     
    public final int index;
    public final String title;
     
    Field(int index, Text text)
    {
      this.index = index;
      this.title = text.text();
    }
     
    static Field forIndex(int index)
    {
      for (Field f : values())
        if (f.index == index)
          return f;
      
      return null;
    }
   }
	
	final JLabel[] labels = new JLabel[Field.values().length];
	final private JLabel[] fields = new JLabel[Field.values().length];
	
	final private JPanel pFields = new JPanel();
	final private JPanel pTotal = new JPanel();
	
	final private JLabel imgTitle, imgScreen;
	
	final private JButton downloadButton = new JButton("Download ROM");
	final private JButton assetsButton = new JButton("Download Assets");
	final private JButton openFolderButton = new JButton("Open Folder");
	final private JButton openArchiveButton = new JButton("Open Archive");
	final private JPanel buttons = new JPanel();
	
	private Rom rom;
		
	public InfoPanel()
	{
		openFolderButton.setEnabled(false);
		openArchiveButton.setEnabled(false);
	  
	  downloadButton.setEnabled(false);
		
		imgTitle = new JLabel();
		imgTitle.setHorizontalAlignment(SwingConstants.CENTER);
    imgTitle.setBorder(BorderFactory.createLineBorder(Color.black));
    imgTitle.setForeground(Color.RED);
    imgTitle.setFont(imgTitle.getFont().deriveFont(30.0f));

		imgScreen = new JLabel();
		imgScreen.setHorizontalAlignment(SwingConstants.CENTER);
		imgScreen.setBorder(BorderFactory.createLineBorder(Color.black));
		imgScreen.setForeground(Color.RED);
		imgScreen.setFont(imgScreen.getFont().deriveFont(30.0f));
		
		JPanel subField1 = new JPanel();
		GroupLayout gl1 = new GroupLayout(subField1);
		subField1.setLayout(gl1);
		
		pFields.setLayout(new BorderLayout());
		pFields.add(subField1, BorderLayout.CENTER);

		gl1.setAutoCreateGaps(true);
		gl1.setAutoCreateContainerGaps(true);
				
		Font f = new Font("null", Font.BOLD, 14);
		
		for (int t = 0; t < labels.length; ++t)
		{
			labels[t] = new JLabel(Field.forIndex(t).title+":");
			fields[t] = new JLabel();
			
			if (t < labels.length-2)
				fields[t].setFont(f);
			//fields[t].setEditable(false);
			fields[t].setBackground(new Color(220,220,220));
			labels[t].setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		GroupLayout.SequentialGroup hGroup1 = gl1.createSequentialGroup();
		
		GroupLayout.ParallelGroup pg1 = gl1.createParallelGroup();
		
		for (int i = 0; i < labels.length; ++i)
		{
			pg1.addComponent(labels[i]);
		}
		
		hGroup1.addGroup(pg1);
		
		pg1 = gl1.createParallelGroup();

		for (int i = 0; i < labels.length; ++i)
		{
			pg1.addComponent(fields[i]);
		}
		
		hGroup1.addGroup(pg1);
		
		gl1.setHorizontalGroup(hGroup1);
		
		GroupLayout.SequentialGroup vGroup1 = gl1.createSequentialGroup();
		
		for (int i = 0; i < labels.length; ++i)
		{
			pg1 = gl1.createParallelGroup(Alignment.BASELINE);
			pg1.addComponent(labels[i]);
			pg1.addComponent(fields[i]);
			vGroup1.addGroup(pg1);
		}
		
		gl1.setVerticalGroup(vGroup1);
			
		JPanel imgs = new JPanel();
		imgs.add(imgTitle);
		imgs.add(Box.createRigidArea(new Dimension(30,0)));
		imgs.add(imgScreen);
		
		pTotal.setLayout(new BoxLayout(pTotal, BoxLayout.PAGE_AXIS));
		pTotal.add(imgs);
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
	
	public void romSetLoaded(final RomSet<?> set)
	{
		this.set = set;
	  
	  SwingUtilities.invokeLater(new Runnable() {
			@Override
      public void run() {
				imgTitle.setPreferredSize(new Dimension(set.screenTitle.width+30,set.screenTitle.height));
				imgScreen.setPreferredSize(new Dimension(set.screenGame.width,set.screenGame.height));
				imgTitle.revalidate();
				imgScreen.revalidate();
			}
		});
		

	}
	
	void setImage(Rom rom, Asset asset, JLabel dest)
	{
		Path path = set.assetPath(asset, rom);
		int w,h;
		long crc = -1L;
		
		if (asset == Asset.SCREEN_TITLE)
		{
			path = set.assetPath(asset, rom);
			w = set.screenTitle.width;
			h = set.screenTitle.height;
			
			if (rom != null)
				crc = rom.imgCRC1;
		}
		else
		{
			w = set.screenGame.width;
			h = set.screenGame.height;
			
			if (rom != null)
				crc = rom.imgCRC2;
		}
		

		if (Files.exists(path) && (!set.getSettings().checkImageCRC || crc == Scanner.computeCRC(path)))
		{
			ImageIcon i = new ImageIcon(path.toString());
			
			Image img = i.getImage();
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 0, 0, w, h, null);
			
			dest.setText("");
			dest.setIcon(new ImageIcon(bi));
		}
		else
		{
			ImageIcon i = new ImageIcon("data/images/missing.png");
			
			Image img = i.getImage();
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 0, 0, w, h, null);
			
			dest.setText("Asset Missing");
			dest.setIcon(null);
		}
	}
	
	public void resetFields()
	{
		for (int t = 0; t < fields.length; ++t)
		{
			fields[t].setText("");
		}
		
		imgTitle.setIcon(null);
		imgScreen.setIcon(null);
		imgTitle.setText("");
		imgTitle.setText("");
	}
	
	public void updateFields(Rom rom)
	{
		this.rom = rom;
		
		this.setVisible(true);
		fields[Field.TITLE.index].setText(rom.getAttribute(RomAttribute.TITLE));
		fields[Field.PUBLISHER.index].setText(rom.getAttribute(RomAttribute.PUBLISHER));
		fields[Field.GROUP.index].setText(rom.getAttribute(RomAttribute.GROUP));
		fields[Field.DUMP_DATE.index].setText(rom.getAttribute(RomAttribute.DATE));
		fields[Field.SIZE.index].setText(rom.size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BITS)+" ("+rom.size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BYTES)+")");
		fields[Field.LOCATION.index].setText(rom.getAttribute(RomAttribute.LOCATION).toString());
		fields[Field.SERIAL.index].setText(rom.serial);
		fields[Field.CRC.index].setText(Long.toHexString(rom.crc).toUpperCase());
		fields[Field.LANGUAGES.index].setText(rom.languagesAsString());
		//fields[11].setText(rom.getClonesString());
		fields[Field.SAVE_TYPE.index].setText(rom.getSave().toString());
		fields[Field.COMMENT.index].setText(rom.getAttribute(RomAttribute.COMMENT));
		
		RomPath romPath = rom.getPath();
		
    fields[Field.FILENAME.index].setText(romPath != null ? romPath.file().getFileName().toString() : "");
		fields[Field.PATH.index].setText(romPath != null ? romPath.file().getParent().toString() : "");
		
    setImage(rom, Asset.SCREEN_TITLE, imgTitle);
    setImage(rom, Asset.SCREEN_GAMEPLAY, imgScreen);
		
		if (rom.status == RomStatus.MISSING)
		{
		  openFolderButton.setEnabled(false);
		  openArchiveButton.setEnabled(false);
			
		  downloadButton.setEnabled(true);
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
				Desktop.getDesktop().browse(new URI(set.downloadURL(rom)));
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
