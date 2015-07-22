package jack.rm.data.rom;

public enum RomAttribute
{
  NUMBER,
  TITLE,
  PUBLISHER,
  GROUP,
  DATE,
  COMMENT,
  LOCATION,
  LANGUAGE,
  SIZE,
  SERIAL,
  CRC,
  SAVE_TYPE,
  
  ;
  
  public <T> String prettyValue(T value) { return value.toString(); } 
}
