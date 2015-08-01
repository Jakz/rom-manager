package jack.rm.data.rom;

public class CustomRomAttribute implements Attribute
{
  private Class<?> clazz;
  private String caption;
  private String name;
  
  CustomRomAttribute() { }
  CustomRomAttribute(Class<?> clazz, String name, String caption)
  {
    this.clazz = clazz;
    this.name = name;
    this.caption = caption;
  }
  
  public String prettyValue(Object value) { return value.toString(); }
  public Class<?> getClazz() { return clazz; }
  public String getCaption() { return caption; }

}