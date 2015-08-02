package jack.rm.files;

import java.util.function.Consumer;
import java.util.function.Predicate;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;

public abstract class RomSetWorker<T extends BackgroundOperation> extends BackgroundWorker<Rom, T>
{
  protected final RomSet romSet;
  
  public RomSetWorker(RomSet set, T plugin, Predicate<Rom> filter, Consumer<Boolean> callback)
  {
    super(plugin, callback);
    set.list.stream().filter(filter).forEach(r -> this.add(r));
    this.romSet = set;

  }
  
  
  @Override
  public abstract void execute(Rom rom);

}
