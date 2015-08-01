package jack.rm.data.rom;

public interface RomInterface
{
  void setAttribute(Attribute key, Object value);
  public <T> T getAttribute(Attribute key);
}
