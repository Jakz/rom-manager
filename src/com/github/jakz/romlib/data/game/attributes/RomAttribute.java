package com.github.jakz.romlib.data.game.attributes;

import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.ui.i18n.Text;


public enum RomAttribute implements Attribute
{
  ROM_NAME(Text.ROM_INFO_FILE_NAME),
  
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
  
  SIZE(Text.ROM_INFO_SIZE) {
    public String prettyValue(Object value)
    {
      RomSize size = (RomSize)value;
      return String.format("%s (%s)", size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BITS), size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BYTES));
    }
  }
  
  ;
  
  RomAttribute(Class<?> clazz, Text caption)
  {
    this.clazz = clazz;
    this.caption = caption;
  }
  
  RomAttribute(Text caption)
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
}
