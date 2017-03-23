package jack.rm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Predicate;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.ui.elements.JPlaceHolderTextField;

import jack.rm.i18n.Text;

public class SearchPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	final JLabel[] labels = new JLabel[4];
	final JPlaceHolderTextField freeSearchField = new JPlaceHolderTextField(10, Text.TEXT_SEARCH_IN_TITLE.text());
	final MainFrame mainFrame;
	
	final JComboBox<RomSize> sizes = new JComboBox<>();
	//final JComboBox genres = new JComboBox();
	final JComboBox<Location> locations = new JComboBox<>();
	final JComboBox<Language> languages = new JComboBox<>();
		
	final private SearchListener listener = new SearchListener();
	
	boolean active = false;

	abstract class CustomCellRenderer<T> implements ListCellRenderer<T>
	{
	  private javax.swing.plaf.basic.BasicComboBoxRenderer renderer;
	  
	  CustomCellRenderer()
	  {
	    renderer = new javax.swing.plaf.basic.BasicComboBoxRenderer();
	  }
	  
    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T obj, int index, boolean isSelected, boolean cellHasFocus)
    {
      JLabel label = (JLabel)renderer.getListCellRendererComponent(list, obj, index, isSelected, cellHasFocus);

      customRendering(label, obj);
      return label;
    }
    
    abstract void customRendering(JLabel label, T value);
	}
	
	class LocationCellRenderer extends CustomCellRenderer<Location>
	{
	  @Override
	  void customRendering(JLabel label, Location location)
	  {
      if (location == null)
      {
        label.setText(Text.LOCATION_TITLE.text());
        label.setIcon(null);
      }
      else
      {
        label.setText(location.fullName);
        label.setIcon(location.icon.getIcon());
      }
	  }
	}
	
	class LanguageCellRenderer extends CustomCellRenderer<Language>
	{
	  @Override
	  void customRendering(JLabel label, Language language)
	  {
      if (language == null)
        label.setText(Text.LANGUAGE_TITLE.text());
      else
        label.setText(language.fullName);
      
      if (language != null && language.icon != null)
        label.setIcon(language.icon.getIcon());
      else
        label.setIcon(null);
	  }
	}
	
  class RomSizeCellRenderer extends CustomCellRenderer<RomSize>
  {
    @Override
    void customRendering(JLabel label, RomSize size)
    {
      if (size == null)
        label.setText(Text.SIZE_TITLE.text());
      else
        label.setText(size.toString());
    }
  }
  
  void activate(boolean active)
  {
    this.active = active;
  }
  
  void toggle(boolean enabled)
  {
    freeSearchField.setEnabled(enabled);
    
    if (!enabled)
      freeSearchField.setText("");
    
    freeSearchField.setPlaceholder(enabled ? Text.TEXT_SEARCH_IN_TITLE.text() : "no search plugin present");
  }
  
  private void setComboBoxSelectedBackground(JComboBox<?> comboBox)
  {
    Object child = comboBox.getAccessibleContext().getAccessibleChild(0);
    javax.swing.plaf.basic.BasicComboPopup popup = (javax.swing.plaf.basic.BasicComboPopup)child;
    JList<?> list = popup.getList();
    list.setSelectionBackground(new Color(164,171,184)); //TODO: hacked
  }
	  
	public SearchPanel(MainFrame mainFrame)
	{
	  this.mainFrame = mainFrame;
	  
	  labels[0] = new JLabel();
		labels[1] = new JLabel();
		labels[2] = new JLabel();
		labels[3] = new JLabel();		
		

		setComboBoxSelectedBackground(locations);
		setComboBoxSelectedBackground(languages);
		setComboBoxSelectedBackground(sizes);
		
		locations.addItem(null);
		for (Location l : Location.values())
		  if (l != Location.NONE)
		    locations.addItem(l);
		locations.setRenderer(new LocationCellRenderer());
		
		languages.addItem(null);
		for (Language l : Language.values())
			languages.addItem(l);
		languages.setRenderer(new LanguageCellRenderer());
		
		sizes.setRenderer(new RomSizeCellRenderer());

		freeSearchField.addCaretListener(new FieldListener());
		sizes.addActionListener(listener);
		//genres.addActionListener(listener);
		locations.addActionListener(listener);
		languages.addActionListener(listener);

		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		this.add(labels[0]);
		this.add(freeSearchField);
		this.add(labels[1]);
		this.add(sizes);
		this.add(labels[2]);
		this.add(locations);
		this.add(labels[3]);
		this.add(languages);
		
		active = true;
	}
	
	public Predicate<Game> buildSearchPredicate()
	{
	  Predicate<Game> predicate = GameSet.current.getSearcher().search(freeSearchField.getText());

	  Location location = locations.getItemAt(locations.getSelectedIndex());
    Language language = languages.getItemAt(languages.getSelectedIndex());
    RomSize size = sizes.getItemAt(sizes.getSelectedIndex());
    
    if (location != null)
      predicate = predicate.and(r -> r.getLocation().is(location));
    
    if (language != null)
      predicate = predicate.and(r -> r.getLanguages().includes(language));
    
    if (size != null)
      predicate = predicate.and(r -> r.getSizeInBytes() == size.bytes());
	  
	  return predicate;
	}
	
	public void resetFields(final RomSize[] nsizes)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
      public void run() {
				sizes.removeAllItems();
				sizes.addItem(null);
				for (RomSize s : nsizes)
					sizes.addItem(s);
			}
		});
		
		freeSearchField.setText("");
		sizes.setSelectedIndex(-1);
		locations.setSelectedIndex(-1);
		languages.setSelectedIndex(-1);
	}
	
	class SearchListener implements ActionListener
	{
		@Override
    public void actionPerformed(ActionEvent e)
		{
			if (active)
				mainFrame.updateTable();
				
		}
	}
	
	class FieldListener implements CaretListener
	{
		@Override
    public void caretUpdate(CaretEvent e)
		{
			if (active)
			{
        mainFrame.updateTable();
			}
		}
	}
}