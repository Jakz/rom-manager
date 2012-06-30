package jack.rm.gui;

import jack.rm.*;
import jack.rm.Downloader.ArtDownloaderTask;
import jack.rm.data.*;
import jack.rm.data.set.RomSet;
import jack.rm.i18n.Text;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

public class InfoPanel extends JPanel implements ActionListener
{
	final JLabel[] labels = new JLabel[Field.values().length];
	final private JLabel[] fields = new JLabel[14];
	
	final private JPanel pFields = new JPanel();
	final private JPanel pTotal = new JPanel();
	
	final private JLabel imgTitle, imgScreen;
	
	final private JButton downloadButton = new JButton("Download ROM");
	final private JButton artButton = new JButton("Download Art");
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
		imgTitle.setHorizontalAlignment(SwingConstants.CENTER);
		
		imgScreen = new JLabel();
		imgScreen.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel subField1 = new JPanel(), subField2 = new JPanel();
		GroupLayout gl1 = new GroupLayout(subField1);
		subField1.setLayout(gl1);
		subField1.setPreferredSize(new Dimension(600,200));
		GroupLayout gl2 = new GroupLayout(subField2);
		subField2.setLayout(gl2);
		
		pFields.setLayout(new BorderLayout());
		pFields.add(subField1, BorderLayout.WEST);
		pFields.add(subField2, BorderLayout.CENTER);
		
		gl1.setAutoCreateGaps(true);
		gl1.setAutoCreateContainerGaps(true);
		gl2.setAutoCreateGaps(true);
		gl2.setAutoCreateContainerGaps(true);
				
		Font f = new Font("null", Font.BOLD, 14);
		
		for (int t = 0; t < labels.length; ++t)
		{
			labels[t] = new JLabel(Field.forIndex(t).title+":");
			fields[t] = new JLabel();
			fields[t].setFont(f);
			//fields[t].setEditable(false);
			fields[t].setBackground(new Color(220,220,220));
			labels[t].setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		GroupLayout.SequentialGroup hGroup1 = gl1.createSequentialGroup();
		GroupLayout.SequentialGroup hGroup2 = gl2.createSequentialGroup();
		
		GroupLayout.ParallelGroup pg1 = gl1.createParallelGroup();
		GroupLayout.ParallelGroup pg2 = gl2.createParallelGroup();
		
		for (int i = 0; i < 7; ++i)
		{
			pg1.addComponent(labels[i]);
			pg2.addComponent(labels[7+i]);
		}
		
		hGroup1.addGroup(pg1); hGroup2.addGroup(pg2);
		
		pg1 = gl1.createParallelGroup();
		pg2 = gl2.createParallelGroup();
		
		for (int i = 0; i < 7; ++i)
		{
			pg1.addComponent(fields[i]);
			pg2.addComponent(fields[7+i]);
		}
		
		hGroup1.addGroup(pg1); hGroup2.addGroup(pg2);
		
		gl1.setHorizontalGroup(hGroup1);
		gl2.setHorizontalGroup(hGroup2);
		
		GroupLayout.SequentialGroup vGroup1 = gl1.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup2 = gl2.createSequentialGroup();
		
		for (int i = 0; i < 7; ++i)
		{
			pg1 = gl1.createParallelGroup(Alignment.BASELINE);
			pg1.addComponent(labels[i]);
			pg1.addComponent(fields[i]);
			vGroup1.addGroup(pg1);
			
			pg2 = gl2.createParallelGroup(Alignment.BASELINE);
			pg2.addComponent(labels[7+i]);
			pg2.addComponent(fields[7+i]);
			vGroup2.addGroup(pg2);
		}
		
		gl1.setVerticalGroup(vGroup1);
		gl2.setVerticalGroup(vGroup2);
		
		
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
		buttons.add(artButton);
		downloadButton.addActionListener(this);
		artButton.addActionListener(this);
		
		pTotal.add(buttons);
		
		this.add(pTotal);
	}
	
	public void setScreenSizes(final Dimension title, final Dimension game)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				imgTitle.setPreferredSize(new Dimension(title.width+30,title.height));
				imgScreen.setPreferredSize(new Dimension(game.width,game.height));
				revalidate();
				repaint();
			}
		});
		

	}
	
	ImageIcon loadImage(Rom rom, String type)
	{
		String path = null;
		int w,h;
		long crc = -1L;
		
		if (rom == null)
			path = "images/missing.png";
		
		if (type.equals("title"))
		{
			if (path == null)
				path = RomSet.current.titleImage(rom);
			w = RomSet.current.screenTitle.width;
			h = RomSet.current.screenTitle.height;
			
			if (rom != null)
				crc = rom.imgCRC1;
		}
		else
		{
			if (path == null)
				path = RomSet.current.gameImage(rom);
			w = RomSet.current.screenGame.width;
			h = RomSet.current.screenGame.height;
			
			if (rom != null)
				crc = rom.imgCRC2;
		}
		

		File f = new File(path);
		if (f.exists() && (!Main.pref.booleanSetting("check-art-crc") || crc == Scanner.computeCRC(f)))
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
		
		if (rom.hasTitleArt() && rom.hasGameArt())
			artButton.setEnabled(false);
		else
			artButton.setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == downloadButton)
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
		else if (e.getSource() == artButton)
		{
			Rom r = rom;
			Main.downloader.pool.submit(new Downloader.TwinArtDownloaderTask(r));
		}
	}
}
