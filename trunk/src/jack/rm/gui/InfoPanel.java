package jack.rm.gui;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;
import jack.rm.i18n.Text;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

public class InfoPanel extends JPanel implements ActionListener
{
	final JLabel[] labels = new JLabel[Field.values().length];
	final private JTextField[] fields = new JTextField[14];
	
	final private JPanel pFields = new JPanel();
	final private JPanel pTotal = new JPanel(new BorderLayout());
	
	final private JLabel imgTitle, imgScreen;
	
	final private JButton downloadButton = new JButton("Download ROM");
	final private JPanel buttons = new JPanel();
	
	private Rom rom;
	
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
		COMMENT      (13, Text.ROM_INFO_COMMENT)
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
	
	public InfoPanel()
	{
		downloadButton.setEnabled(false);
		
		imgTitle = new JLabel();
		imgTitle.setPreferredSize(new Dimension(RomSet.current.screenTitle.width+30,RomSet.current.screenTitle.height));
		imgTitle.setHorizontalAlignment(SwingConstants.CENTER);
		
		imgScreen = new JLabel();
		imgScreen.setPreferredSize(new Dimension(RomSet.current.screenGame.width+30,RomSet.current.screenGame.height));
		imgScreen.setHorizontalAlignment(SwingConstants.CENTER);
				
		pFields.setLayout(new BoxLayout(pFields,BoxLayout.PAGE_AXIS));
		
		for (int t = 0; t < labels.length; ++t)
		{
			labels[t] = new JLabel(Field.forIndex(t).title);
			fields[t] = new JTextField(20);
			fields[t].setEditable(false);
			fields[t].setBackground(new Color(220,220,220));
			labels[t].setHorizontalAlignment(SwingConstants.RIGHT);
			JPanel tmpPanel = new JPanel(new GridLayout(1,2));
			tmpPanel.add(labels[t]);
			tmpPanel.add(fields[t]);
			pFields.add(tmpPanel);
		}
		
		JPanel imgs = new JPanel();
		imgTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
		imgs.add(imgTitle);
		imgs.add(imgScreen);
		
		pTotal.add(imgs, BorderLayout.NORTH);
		JPanel pFields2 = new JPanel(new BorderLayout());
		pFields2.add(pFields, BorderLayout.NORTH);
		pTotal.add(pFields2, BorderLayout.CENTER);
		
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		buttons.add(downloadButton);
		downloadButton.addActionListener(this);
		
		pTotal.add(buttons, BorderLayout.SOUTH);
		
		this.add(pTotal);
	}
	
	ImageIcon loadImage(Rom rom, String type)
	{
		String path = null;
		int w,h;
		
		if (rom == null)
			path = "images/missing.png";
		
		if (type.equals("title"))
		{
			if (path == null)
				path = RomSet.current.titleImage(rom);
			w = RomSet.current.screenTitle.width;
			h = RomSet.current.screenTitle.height;
		}
		else
		{
			if (path == null)
				path = RomSet.current.gameImage(rom);
			w = RomSet.current.screenGame.width;
			h = RomSet.current.screenGame.height;
		}
		

		File f = new File(path);
		if (f.exists())
		{
			ImageIcon i = new ImageIcon(path);
			
			Image img = i.getImage();
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			g.drawImage(img, 0, 0, w, h, null);
			
			return new ImageIcon(bi);
		}
		else
		{
			ImageIcon i = new ImageIcon("images/missing.png");
			
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
		fields[Field.SIZE.index].setText(rom.size.bitesAsStringLong()+" ("+rom.size.mbytesAsString()+")");
		//fields[5].setText(StringManager.getGenre(rom.genre));
		fields[Field.LOCATION.index].setText(rom.location.fullName);
		fields[Field.INTERNAL_NAME.index].setText(rom.internalName);
		fields[Field.SERIAL.index].setText(rom.serial);
		fields[Field.CRC.index].setText(Long.toHexString(rom.crc).toUpperCase());
		fields[Field.LANGUAGES.index].setText(rom.languagesAsString());
		//fields[11].setText(rom.getClonesString());
		fields[Field.SAVE_TYPE.index].setText(rom.save+rom.saveType());
		fields[Field.COMMENT.index].setText(rom.info);
		
		imgTitle.setIcon(loadImage(rom,"title"));
		imgScreen.setIcon(loadImage(rom,"game"));
		
		if (rom.status == RomStatus.NOT_FOUND)
			downloadButton.setEnabled(true);
		else
			downloadButton.setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			Desktop.getDesktop().browse(new URI(RomSet.current.downloadURL(rom)));
		}
		catch (Exception ee)
		{
			ee.printStackTrace();
		}
	}
}
