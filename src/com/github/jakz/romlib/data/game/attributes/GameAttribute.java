package com.github.jakz.romlib.data.game.attributes;

import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.RomSize.PrintStyle;
import com.github.jakz.romlib.data.game.RomSize.PrintUnit;
import com.github.jakz.romlib.data.game.VideoFormat;

import java.util.Arrays;

import com.github.jakz.romlib.data.game.Genre;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.LanguageSet;

import jack.rm.i18n.Text;

public enum GameAttribute implements Attribute
{
  NUMBER(Integer.class, Text.ROM_INFO_NUMBER),
  IMAGE_NUMBER(Integer.class, Text.ROM_INFO_IMAGE_NUMBER),
  TITLE(String.class, Text.ROM_INFO_TITLE),
  NORMALIZED_TITLE(String.class, Text.ROM_INFO_TITLE),
  DESCRIPTION(String.class, Text.ROM_INFO_DESCRIPTION),
  PUBLISHER(Text.ROM_INFO_PUBLISHER),
  GROUP(Text.ROM_INFO_GROUP),
  DATE(Text.ROM_INFO_DUMP_DATE),
  COMMENT(String.class, Text.ROM_INFO_COMMENT),
  LOCATION(Text.ROM_INFO_LOCATION),
  LANGUAGE(Text.ROM_INFO_LANGUAGES) { 
    @Override public String prettyValue(Object value) { 
      return Language.asString((LanguageSet)value); 
      }
  },
  SERIAL(Text.ROM_INFO_SERIAL),
  SAVE_TYPE(Text.ROM_INFO_SAVE_TYPE),
  SIZE(Text.GAME_INFO_SIZE) {
    @Override public String prettyValue(Object value) {
      RomSize size = (RomSize)value;
      return size.toString(PrintStyle.LONG, PrintUnit.BITS) + " (" + size.toString(PrintStyle.SHORT, PrintUnit.BYTES) + ")";
    }
  },
  
  GENRE(Genre.class, Text.ROM_INFO_GENRE),
  TAG(String.class, Text.ROM_INFO_TAG),
  EXPORT_TITLE(String.class, Text.GAME_INFO_EXPORT_TITLE),
  
  VIDEO_FORMAT(VideoFormat.class, Text.ROM_INFO_VIDEO_FORMAT),
  
  VERSION(Text.ROM_INFO_VERSION),
  
  LICENSED(Text.ROM_INFO_LICENSED),
  
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
  
  @Override
  public String getIdent() { return this.name().toLowerCase(); }
  
  @Override
  public String prettyValue(Object value) { return value != null ? value.toString() : null; }

  @Override
  public Class<?> getClazz() { return clazz; }

  @Override
  public String getCaption() { return caption.text(); }
  
  public static GameAttribute forIdent(String string)
  {
    return Arrays.stream(values()).filter(a -> a.getIdent().equals(string)).findFirst().get();
  }
}
