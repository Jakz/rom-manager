package jack.rm.data.rom;

public class CustomRomAttribute implements Attribute
{
  private Class<?> clazz;
  private String caption;
  private String name;
  
  public CustomRomAttribute() { }
  public CustomRomAttribute(String name, String caption, Class<?> clazz)
  {
    this.clazz = clazz;
    this.name = name;
    this.caption = caption;
  }
  
  public String toString() { return name; }
  public String prettyValue(Object value) { return value.toString(); }
  public Class<?> getClazz() { return clazz; }
  public String getCaption() { return caption; }
  public boolean equals(Object o) { return this.getClass().equals(o.getClass()); }

}