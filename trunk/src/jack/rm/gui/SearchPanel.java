package jack.rm.gui;

import jack.rm.data.*;
import jack.rm.i18n.Text;
import jack.rm.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.util.Arrays;

public class SearchPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	final JLabel[] labels = new JLabel[4];
	final JPlaceHolderTextField romName = new JPlaceHolderTextField(10);
	
	final JComboBox sizes = new JComboBox(new String[]{Text.SIZE_TITLE.text()});
	//final JComboBox genres = new JComboBox();
	final JComboBox locations = new JComboBox(new String[]{Text.LOCATION_TITLE.text()});
	final JComboBox languages = new JComboBox(new String[]{Text.LANGUAGE_TITLE.text()});
	
	final private SearchListener listener = new SearchListener();
	
	boolean active = false;
	
	public SearchPanel()
	{
		labels[0] = new JLabel();
		labels[1] = new JLabel();
		labels[2] = new JLabel();
		labels[3] = new JLabel();

		/*labels[0].setFont(new Font(null,Font.PLAIN,9));
		labels[1].setFont(new Font(null,Font.PLAIN,9));
		labels[2].setFont(new Font(null,Font.PLAIN,9));
		labels[3].setFont(new Font(null,Font.PLAIN,9));
		labels[4].setFont(new Font(null,Font.PLAIN,9));*/
		
		for (Location l : Location.values())
			locations.addItem(l);
		
		for (Language l : Language.values())
			languages.addItem(l);

		/*romName.setFont(new Font(null,Font.PLAIN,9));
		sizes.setFont(new Font(null,Font.PLAIN,9));
		genres.setFont(new Font(null,Font.PLAIN,9));
		locations.setFont(new Font(null,Font.PLAIN,9));
		languages.setFont(new Font(null,Font.PLAIN,9));*/
		
		romName.addCaretListener(new FieldListener());
		sizes.addActionListener(listener);
		//genres.addActionListener(listener);
		locations.addActionListener(listener);
		languages.addActionListener(listener);

		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		this.add(labels[0]);
		this.add(romName);
		this.add(labels[1]);
		this.add(sizes);
		this.add(labels[2]);
		this.add(locations);
		this.add(labels[3]);
		this.add(languages);
		
		active = true;
	}
	
	public void resetFields(final RomSize[] nsizes)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sizes.removeAllItems();
				sizes.addItem(Text.SIZE_TITLE.text());
				for (RomSize s : nsizes)
				{
					System.out.println(s.bytes+"  ->  "+s.toString());
					sizes.addItem(s);
				}
			}
		});
		
		romName.setText("");
		sizes.setSelectedIndex(0);
		locations.setSelectedIndex(0);
		languages.setSelectedIndex(0);
	}
	
	class SearchListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (active)
			{
				RomSize size = sizes.getSelectedIndex() > 0 ? (RomSize)sizes.getSelectedItem() : null;
				Location location = locations.getSelectedIndex() > 0 ? (Location)locations.getSelectedItem() : null;
				Language language = languages.getSelectedIndex() > 0 ? (Language)languages.getSelectedItem() : null;
				Main.romList.search(romName.getText(),size,location,language);
			}
				
		}
	}
	
	class FieldListener implements CaretListener
	{
		public void caretUpdate(CaretEvent e)
		{
			if (active)
			{
				RomSize size = sizes.getSelectedIndex() > 0 ? (RomSize)sizes.getSelectedItem() : null;
				Location location = locations.getSelectedIndex() > 0 ? (Location)locations.getSelectedItem() : null;
				Language language = languages.getSelectedIndex() > 0 ? (Language)languages.getSelectedItem() : null;
				Main.romList.search(romName.getText(),size,location,language);
			}
		}
	}
}