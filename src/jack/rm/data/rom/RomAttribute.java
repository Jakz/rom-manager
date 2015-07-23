package jack.rm.data.rom;

import java.util.Set;
import jack.rm.data.Language;
import jack.rm.data.RomSize;
import jack.rm.i18n.Text;

public enum RomAttribute
{
  NUMBER(Text.ROM_INFO_TITLE),
  TITLE(Text.ROM_INFO_TITLE),
  PUBLISHER(Text.ROM_INFO_PUBLISHER),
  GROUP(Text.ROM_INFO_GROUP),
  DATE(Text.ROM_INFO_DUMP_DATE),
  COMMENT(Text.ROM_INFO_COMMENT),
  LOCATION(Text.ROM_INFO_LOCATION),
  LANGUAGE(Text.ROM_INFO_LANGUAGES) { 
    @SuppressWarnings("unchecked") public String prettyValue(Object value) { 
      return Language.asString((Set<Language>)value); 
      }
  },
  SIZE(Text.ROM_INFO_SIZE) {
    public String prettyValue(Object value)
    {
      RomSize size = (RomSize)value;
      return String.format("%s (%s)", size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BITS), size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BYTES));
    }
  },
  SERIAL(Text.ROM_INFO_SERIAL),
  SAVE_TYPE(Text.ROM_INFO_SAVE_TYPE),
  
  CRC(Text.ROM_INFO_CRC)
  {
    public String prettyValue(Object value) {
      return Long.toHexString((Long)value).toUpperCase();
    }
  },
  SHA1(Text.ROM_INFO_SHA1)
  {
    public String prettyValue(Object value) {
      byte[] digest = (byte[])value;
      return javax.xml.bind.DatatypeConverter.printHexBinary(digest);
    }
  },
  MD5(Text.ROM_INFO_MD5)
  {
    public String prettyValue(Object value) {
      byte[] digest = (byte[])value;
      return javax.xml.bind.DatatypeConverter.printHexBinary(digest);
    }
  },
  
  
  FILENAME(Text.ROM_INFO_FILENAME),
  PATH(Text.ROM_INFO_PATH)
  ;
  
  RomAttribute(Text caption)
  {
    this.caption = caption;
  }
  
  public final Text caption;
  public String prettyValue(Object value) { return value.toString(); } 
}
