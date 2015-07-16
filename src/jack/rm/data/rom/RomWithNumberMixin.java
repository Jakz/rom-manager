package jack.rm.data.rom;

public interface RomWithNumberMixin extends RomInterface
{
  default int getSave() { return getAttribute(RomAttribute.NUMBER); }
  default void setSave(int value) { setAttribute(RomAttribute.NUMBER, value); }
}
