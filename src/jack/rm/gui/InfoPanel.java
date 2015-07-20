package jack.rm.gui;

import jack.rm.*;
import jack.rm.data.*;
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
    INTERNAL_NAME(9, Text.ROM_INFO_INTERNAL_NAME),
    SERIAL       (10, Text.ROM_INFO_SERIAL),
    GROUP        (11, Text.ROM_INFO_GROUP),
    DUMP_DATE    (12, Text.ROM_INFO_DUMP_DATE),
    COMMENT      (13, Text.ROM_INFO_COMMENT),
    FILENAME     (14, Text.ROM_INFO_PATH),
    PATH         (15, Text.ROM_INFO_PATH)
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
		
		imgScreen = new JLabel();
		imgScreen.setHorizontalAlignment(SwingConstants.CENTER);
		
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
		imgTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
		imgs.add(imgTitle);
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
				revalidate();
				repaint();
			}
		});
		

	}
	
	ImageIcon loadImage(Rom rom, String type)
	{
		Path path = null;
		int w,h;
		long crc = -1L;
		
		if (rom == null)
			path = Paths.get("data/images/missing.png");
		
		if (type.equals("title"))
		{
			if (path == null)
				path = set.assetPath(Asset.SCREEN_TITLE, rom);
			w = set.screenTitle.width;
			h = set.screenTitle.height;
			
			if (rom != null)
				crc = rom.imgCRC1;
		}
		else
		{
			if (path == null)
				path = set.assetPath(Asset.SCREEN_GAMEPLAY, rom);
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
			
			return new ImageIcon(bi);
		}
		else
		{
			ImageIcon i = new ImageIcon("data/images/missing.png");
			
			Image img = i.getImage();
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 0, 0, w, h, null);
			
			return new ImageIcon(bi);
		}
	}
	
	public void resetFields()
	{
		for (int t = 0; t < fields.length; ++t)
		{
			fields[t].setText("");
		}
		imgTitle.setIcon(loadImage(null,"title"));
		imgScreen.setIcon(loadImage(null,"game"));
	}
	
	public void updateFields(Rom rom)
	{
		this.rom = rom;
		
		this.setVisible(true);
		fields[Field.TITLE.index].setText(rom.title);
		fields[Field.PUBLISHER.index].setText(rom.publisher);
		fields[Field.GROUP.index].setText(rom.group);
		fields[Field.DUMP_DATE.index].setText(rom.date);
		fields[Field.SIZE.index].setText(rom.size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BITS)+" ("+rom.size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BYTES)+")");
		//fields[5].setText(StringManager.getGenre(rom.genre));
		fields[Field.LOCATION.index].setText(rom.location.fullName);
		fields[Field.INTERNAL_NAME.index].setText(rom.internalName);
		fields[Field.SERIAL.index].setText(rom.serial);
		fields[Field.CRC.index].setText(Long.toHexString(rom.crc).toUpperCase());
		fields[Field.LANGUAGES.index].setText(rom.languagesAsString());
		//fields[11].setText(rom.getClonesString());
		fields[Field.SAVE_TYPE.index].setText(rom.getSave().toString());
		fields[Field.COMMENT.index].setText(rom.info);
		
		RomPath romPath = rom.getPath();
		
    fields[Field.FILENAME.index].setText(romPath != null ? romPath.file().getFileName().toString() : "");
		fields[Field.PATH.index].setText(romPath != null ? romPath.file().getParent().toString() : "");
		
		imgTitle.setIcon(loadImage(rom,"title"));
		imgScreen.setIcon(loadImage(rom,"game"));
		
		if (rom.status == RomStatus.NOT_FOUND)
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
