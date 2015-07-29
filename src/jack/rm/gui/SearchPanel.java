package jack.rm.gui;

import jack.rm.data.*;
import jack.rm.data.rom.RomAttribute;
import jack.rm.i18n.Text;
import javax.swing.*;
import javax.swing.event.*;

import com.pixbits.gui.JPlaceHolderTextField;
import com.pixbits.parser.SimpleParser;

import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
	
	final SimpleParser parser = new SimpleParser();
	
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
  
  private void setComboBoxSelectedBackground(JComboBox<?> comboBox)
  {
    Object child = comboBox.getAccessibleContext().getAccessibleChild(0);
    javax.swing.plaf.basic.BasicComboPopup popup = (javax.swing.plaf.basic.BasicComboPopup)child;
    JList<?> list = popup.getList();
    list.setSelectionBackground(new Color(164,171,184)); //TODO: hacked
  }
	  
	public SearchPanel(MainFrame mainFrame)
	{
    parser.addWhiteSpace(' ');
    parser.addQuote('\"');
	  
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
	
	private boolean isSearchArg(String[] tokens, String... vals)
	{
	  boolean firstMatch = tokens[0].equals(vals[0]);  
	  return firstMatch && Arrays.stream(vals, 1, vals.length).anyMatch( v -> v.equals(tokens[1]));
	}

	public Predicate<Rom> buildPredicate(String token)
	{
	  if (token.contains(":"))
	  {
	    String[] tokens = token.split(":");
	    if (tokens.length == 2)
	    {
	      if (tokens[1].startsWith("\""))
	        tokens[1] = tokens[1].substring(1);
	      if (tokens[1].endsWith("\""))
	        tokens[1] = tokens[1].substring(0, tokens[1].length()-1);
	      
	      if (isSearchArg(tokens, "is", "favorite", "favourite", "fav"))
	        return r -> r.isFavourite();
	      else if (tokens[0].equals("genre"))
	        return r -> {
            Genre genre = r.getAttribute(RomAttribute.GENRE);
            Genre sgenre = Genre.forName(tokens[1]);
            
            return genre != null && sgenre != null && genre == sgenre;
	        };
	        else if (tokens[0].equals("save"))
	        {
	          String[] itokens = tokens[1].split(" ");
	          
	          return r -> {
	            RomSave<? >save = r.getAttribute(RomAttribute.SAVE_TYPE);            
	            return save != null && Arrays.stream(itokens).allMatch(s -> save.toString().toLowerCase().contains(s.toLowerCase()));   
	          };
	        }
	    }    
	  }
	  else
	  {
	    return r -> r.getTitle().toLowerCase().contains(token);
	  }
	  
	  return null;
	}
	
	public Predicate<Rom> buildSearchPredicate()
	{
	  Predicate<Rom> predicate = r -> true;
	  
	  List<String> tokens = new ArrayList<>();
	  Consumer<String> callback = s -> tokens.add(s); 

	  parser.setCallback(callback);
	  parser.reset(new java.io.ByteArrayInputStream(freeSearchField.getText().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
	  
	  try {
	    parser.parse();
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
	  
	  for (String token : tokens)
	  {
	    boolean negated = false;
	    
	    if (token.startsWith("!"))
	    {
	      negated = true;
	      token = token.substring(1);
	    }
	    
	    Predicate<Rom> cpredicate = buildPredicate(token);
	    
	    if (cpredicate != null)
	    {
	      if (negated)
	        predicate = predicate.and(cpredicate.negate());
	      else
	        predicate = predicate.and(cpredicate);
	    }
	  }

	  Location location = locations.getItemAt(locations.getSelectedIndex());
    Language language = languages.getItemAt(languages.getSelectedIndex());

    if (location != null)
      predicate = predicate.and(r -> r.getAttribute(RomAttribute.LOCATION).equals(location));
    
    if (language != null)
      predicate = predicate.and(r -> r.getLanguages().contains(language));
	  
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