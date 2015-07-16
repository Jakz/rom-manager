package jack.rm.data.rom;

import jack.rm.data.RomSave;

public interface RomWithSaveMixin<T extends RomSave<?>> extends RomInterface
{
  default T getSave() { return getAttribute(RomAttribute.SAVE_TYPE); }
  default void setSave(T value) { setAttribute(RomAttribute.SAVE_TYPE, value); }
}
