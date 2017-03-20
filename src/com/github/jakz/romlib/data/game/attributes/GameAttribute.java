package com.github.jakz.romlib.data.game.attributes;

import com.github.jakz.romlib.data.game.GameSize;
import com.github.jakz.romlib.data.game.Genre;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.LanguageSet;

import jack.rm.i18n.Text;

public enum GameAttribute implements Attribute
{
  NUMBER(Integer.class, Text.ROM_INFO_NUMBER),
  IMAGE_NUMBER(Integer.class, Text.ROM_INFO_IMAGE_NUMBER),
  TITLE(String.class, Text.ROM_INFO_TITLE),
  DESCRIPTION(String.class, Text.ROM_INFO_DESCRIPTION),
  PUBLISHER(Text.ROM_INFO_PUBLISHER),
  GROUP(Text.ROM_INFO_GROUP),
  DATE(Text.ROM_INFO_DUMP_DATE),
  COMMENT(String.class, Text.ROM_INFO_COMMENT),
  LOCATION(Text.ROM_INFO_LOCATION),
  LANGUAGE(Text.ROM_INFO_LANGUAGES) { 
    public String prettyValue(Object value) { 
      return Language.asString((LanguageSet)value); 
      }
  },
  SIZE(Text.ROM_INFO_SIZE) {
    public String prettyValue(Object value)
    {
      GameSize size = (GameSize)value;
      return String.format("%s (%s)", size.toString(GameSize.PrintStyle.LONG, GameSize.PrintUnit.BITS), size.toString(GameSize.PrintStyle.LONG, GameSize.PrintUnit.BYTES));
    }
  },
  SERIAL(Text.ROM_INFO_SERIAL),
  SAVE_TYPE(Text.ROM_INFO_SAVE_TYPE),
  
  CRC(Text.ROM_INFO_CRC)
  {
    public String prettyValue(Object value) {
      return String.format("%08X", (Long)value);
      //return Long.toHexString((Long)value).toUpperCase();
    }
  },
  
  SHA1(byte[].class, Text.ROM_INFO_SHA1)
  {
    public String prettyValue(Object value) {
      byte[] digest = (byte[])value;
      return javax.xml.bind.DatatypeConverter.printHexBinary(digest);
    }
  },
  MD5(byte[].class, Text.ROM_INFO_MD5)
  {
    public String prettyValue(Object value) {
      byte[] digest = (byte[])value;
      return javax.xml.bind.DatatypeConverter.printHexBinary(digest);
    }
  },
  
  GENRE(Genre.class, Text.ROM_INFO_GENRE),
  
  TAG(String.class, Text.ROM_INFO_TAG),
  
  VERSION(Text.ROM_INFO_VERSION),
  
  LICENSED(Text.ROM_INFO_LICENSED),
  
  FILENAME(Text.ROM_INFO_FILENAME),
  PATH(Text.ROM_INFO_PATH)
  ;
  
  GameAttribute(Class<?> clazz, Text caption)
  {
    this.clazz = clazz;
    this.caption = caption;
  }
  
  GameAttribute(Text caption)
  {
    this(null, caption);
  }
  
  private final Class<?> clazz; 
  private final Text caption;
  
  public String toString() { return this.name().toLowerCase(); }
  
  @Override
  public String prettyValue(Object value) { return value != null ? value.toString() : null; }

  @Override
  public Class<?> getClazz() { return clazz; }

  @Override
  public String getCaption() { return caption.text(); } 
}
