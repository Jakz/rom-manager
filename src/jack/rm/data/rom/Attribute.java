package jack.rm.data.rom;

public interface Attribute
{

  String prettyValue(Object value);

  Class<?> getClazz();

  String getCaption();

}