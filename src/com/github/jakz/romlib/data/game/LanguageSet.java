package com.github.jakz.romlib.data.game;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class LanguageSet implements Iterable<Language>
{
  private final Set<Language> languages;
  
  public LanguageSet()
  {
    languages = new TreeSet<>();
  }
  
  public int size() { return languages.size(); }
  public void add(Language language) { languages.add(language); }
    
  public boolean includes(Language language) { return languages.contains(language); }
  
  public boolean isJust(Language language)
  {
    return languages.size() == 1 && languages.stream().findFirst().get() == language;
  }
  
  public Iterator<Language> iterator() { return languages.iterator(); }
  public Stream<Language> stream() { return languages.stream(); }
}
