package jack.rm.data.rom;

public interface RomInterface
{
  void setAttribute(RomAttribute key, Object value);
  public <T> T getAttribute(RomAttribute key);
}
