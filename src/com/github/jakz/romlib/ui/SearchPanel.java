package com.github.jakz.romlib.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.ui.i18n.Text;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.LocationSet;
import com.pixbits.lib.searcher.SearcherInterface;
import com.pixbits.lib.ui.elements.JPlaceHolderTextField;

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
  
  public void activate(boolean active)
  {
    this.active = active;
  }
  
  public void toggle(SearcherInterface<Game> searcher)
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
		
		locations.setRenderer(new LocationCellRenderer());
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
	
	public void resetFields(GameSet set)//  SearcherInterface<Game> searcher, final RomSize.Set set)
	{
		this.searcher = set.helper().searcher();
	  
		final boolean supportSizeSet = set.hasFeature(Feature.FINITE_SIZE_SET);

	  SwingUtilities.invokeLater(() -> {
	    freeSearchField.setText("");
	    sizes.setSelectedIndex(-1);
	    locations.setSelectedIndex(-1);
	    languages.setSelectedIndex(-1);
	    
		  sizes.setVisible(supportSizeSet);
		  if (supportSizeSet)
		  {
		    sizes.removeAllItems();
		    sizes.addItem(null);
		    for (RomSize s : set.sizeSet())
		      sizes.addItem(s);		    
		  }
		  
      Set<Language> usedLanguages = new HashSet<>();
      LocationSet usedLocations = new LocationSet();
      
      /* add all found languages to used languages set */
      set.stream()
        .flatMap(game -> game.getLanguages().stream())
        .forEach(usedLanguages::add);
      
      languages.removeAllItems();
      languages.addItem(null);
      usedLanguages.stream()
        .sorted((l1,l2) -> l1.fullName.compareTo(l2.fullName))
        .forEach(languages::addItem);
      
      /* add all found locations to used locations set */
      set.stream()
        .map(game -> game.getLocation())
        .forEach(locations -> usedLocations.add(locations));
      
      locations.removeAllItems();
      locations.addItem(null);
      Arrays.stream(Location.values())
        .filter(location -> usedLocations.is(location))
        .sorted((l1,l2) -> {
          if (!(l1.isComposite() ^ l2.isComposite()))
            return l1.fullName.compareTo(l2.fullName);
          else if (!l1.isComposite())
            return -1;
          else if (!l2.isComposite())
            return 1;
          else
            return 0;
        })
        .forEach(locations::addItem);
		});
	}
}