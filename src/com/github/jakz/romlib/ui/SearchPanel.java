package com.github.jakz.romlib.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Predicate;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.pixbits.lib.searcher.SearcherInterface;
import com.pixbits.lib.ui.elements.JPlaceHolderTextField;

import jack.rm.i18n.Text;

public class SearchPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final Runnable refreshCallback;
	private SearcherInterface<Game> searcher = s -> t -> true;
	
	final JLabel[] labels = new JLabel[4];
	final JPlaceHolderTextField freeSearchField = new JPlaceHolderTextField(10, Text.TEXT_SEARCH_IN_TITLE.text());
	//final MainFrame mainFrame;
	
	final JComboBox<RomSize> sizes = new JComboBox<>();
	//final JComboBox genres = new JComboBox();
	final JComboBox<Location> locations = new JComboBox<>();
	final JComboBox<Language> languages = new JComboBox<>();
			
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
  
  void toggle(SearcherInterface<Game> searcher)
  {
    this.searcher = searcher;
    
    freeSearchField.setEnabled(searcher != null);
    
    if (searcher == null)
      freeSearchField.setText("");
    
    freeSearchField.setPlaceholder(searcher != null ? Text.TEXT_SEARCH_IN_TITLE.text() : "no search plugin present");
  }
  
  private void setComboBoxSelectedBackground(JComboBox<?> comboBox)
  {
    Object child = comboBox.getAccessibleContext().getAccessibleChild(0);
    javax.swing.plaf.basic.BasicComboPopup popup = (javax.swing.plaf.basic.BasicComboPopup)child;
    JList<?> list = popup.getList();
    list.setSelectionBackground(new Color(164,171,184)); //TODO: hacked
  }
	  
	public SearchPanel(Runnable refreshCallback)
	{
	  this.refreshCallback = refreshCallback;
	  
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
		
		freeSearchField.addCaretListener(e -> { if (active) refreshCallback.run(); } );
		sizes.addActionListener(e -> { if (active) refreshCallback.run(); } );
		//genres.addActionListener(listener);
		locations.addActionListener(e -> { if (active) refreshCallback.run(); } );
		languages.addActionListener(e -> { if (active) refreshCallback.run(); } );

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
	  Predicate<Game> predicate = searcher != null ? searcher.search(freeSearchField.getText()) : g -> true;
    
    Location location = locations.getItemAt(locations.getSelectedIndex());
    if (location != null)
      predicate = predicate.and(r -> r.getLocation().is(location));
    
    Language language = languages.getItemAt(languages.getSelectedIndex());
    if (language != null)
      predicate = predicate.and(r -> r.getLanguages().includes(language));
    
    RomSize size = sizes.getItemAt(sizes.getSelectedIndex());
    if (size != null)
      predicate = predicate.and(r -> r.getSizeInBytes() == size.bytes());
	  
	  return predicate;
	}
	
	public void resetFields(SearcherInterface<Game> searcher, final RomSize.Set set)
	{
		this.searcher = searcher;
	  
	  SwingUtilities.invokeLater(new Runnable() {
			@Override
      public void run() {
				sizes.removeAllItems();
				sizes.addItem(null);
				for (RomSize s : set.values())
					sizes.addItem(s);
			}
		});
		
		freeSearchField.setText("");
		sizes.setSelectedIndex(-1);
		locations.setSelectedIndex(-1);
		languages.setSelectedIndex(-1);
	}
}