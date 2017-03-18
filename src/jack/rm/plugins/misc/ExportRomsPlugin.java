package jack.rm.plugins.misc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.pixbits.lib.plugin.ExposedParameter;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomList;
import jack.rm.data.romset.RomSet;
import jack.rm.files.BackgroundOperation;
import jack.rm.files.RomSetWorker;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.OperationalPlugin;
import jack.rm.plugins.PluginRealType;

public class ExportRomsPlugin extends ActualPlugin implements OperationalPlugin, BackgroundOperation
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.MISC; }

  
  @ExposedParameter(name="Export Path", description="This is the path in which to export roms", params="directories")
  Path path; 
  
  @Override public void execute(RomList list)
  {
    new CopyWorker(list.set, this, b -> {}).execute();
  }
  
  static Predicate<Rom> buildFilterPredicate()
  {
    Set<Rom> visibleRoms = new HashSet<>();
    
    int count = Main.mainFrame.list.getModel().getSize();
    for (int i = 0; i < count; ++i)
    {
      Rom rom = Main.mainFrame.list.getModel().getElementAt(i);
      if (rom.status != RomStatus.MISSING)
        visibleRoms.add(rom);
    }
    
    return r -> visibleRoms.contains(r);
  }
  
  public class CopyWorker extends RomSetWorker<ExportRomsPlugin>
  {
    public CopyWorker(RomSet romSet, ExportRomsPlugin plugin, Consumer<Boolean> callback)
    {
      super(romSet, plugin, buildFilterPredicate(), callback);
    }

    @Override
    public void execute(Rom r)
    {
      try
      {
        Files.copy(r.getHandle().path(), path.resolve(r.getHandle().path().getFileName()));  
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
  
  @Override public String getTitle() { return "Exporting roms"; }
  @Override public String getProgressText() { return "Exporting..."; }
  @Override public String getMenuCaption() { return "Export Selection"; }
  @Override public String getSubmenuCaption() { return "Export"; }
}
