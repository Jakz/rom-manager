package com.github.jakz.romlib.data.set;

public interface GameSetAttributeInterface
{
  <T> void setAttribute(GameSetAttribute attrib, T value);
  <T> T getAttribute(GameSetAttribute attrib);
  
  default void setName(String name) { setAttribute(GameSetAttribute.NAME, name); }
  default String getName() { return getAttribute(GameSetAttribute.NAME); }
  
  default void setDescription(String description) { setAttribute(GameSetAttribute.DESCRIPTION, description);  }
  default String getDescription() { return getAttribute(GameSetAttribute.DESCRIPTION); }
  
  default void setAuthor(String author) { setAttribute(GameSetAttribute.AUTHOR, author); }
  default String getAuthor() { return getAttribute(GameSetAttribute.AUTHOR); }
  
  default void setComment(String comment) { setAttribute(GameSetAttribute.COMMENT, comment); }
  default String getComment() { return getAttribute(GameSetAttribute.COMMENT); }
  
  default void setVersion(String version) { setAttribute(GameSetAttribute.VERSION, version); }
  default String getVersion() { return getAttribute(GameSetAttribute.VERSION); }
  
  default String getCaption() { return getAttribute(GameSetAttribute.CAPTION); }
}
