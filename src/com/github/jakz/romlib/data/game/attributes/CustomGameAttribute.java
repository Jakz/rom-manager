package com.github.jakz.romlib.data.game.attributes;

import com.github.jakz.romlib.ui.i18n.I18N;

public class CustomGameAttribute implements Attribute
{
  private Class<?> clazz;
  private String caption;
  private String identifier;
  
  public CustomGameAttribute() { }
  
  public CustomGameAttribute(String identifier, String caption, Class<?> clazz)
  {
    this.clazz = clazz;
    this.identifier = identifier;
    this.caption = caption;
  }
  
  public CustomGameAttribute(String identifier, I18N caption, Class<?> clazz)
  {
    this(identifier, caption.text(), clazz);
  }
  
  @Override public String prettyValue(Object value) { return value.toString(); }
  @Override public Class<?> getClazz() { return clazz; }
  @Override public String getCaption() { return caption; }
  @Override public String getIdent() { return identifier; }
  
  public static class Boolean extends CustomGameAttribute
  {
    public Boolean(String identifier, I18N caption) { super(identifier, caption, Boolean.class); }
  }
}