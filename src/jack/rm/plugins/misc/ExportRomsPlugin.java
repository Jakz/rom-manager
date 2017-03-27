package jack.rm.plugins.misc;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.concurrent.OperationDetails;
import com.pixbits.lib.plugin.ExposedParameter;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.Main;
import jack.rm.files.RomSetWorker;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.OperationalPlugin;
import jack.rm.plugins.PluginRealType;

public class ExportRomsPlugin extends ActualPlugin implements OperationalPlugin, OperationDetails
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.MISC; }

  
  @ExposedParameter(name="Export Path", description="This is the path in which to export roms", params="directories")
  Path path; 
  
  @Override public void execute(GameSet set)
  {
    new CopyWorker(set, this, b -> {}).execute();
  }
  
  static Predicate<Game> buildFilterPredicate()
  {
    Set<Game> visibleRoms = new HashSet<>();
    
    // TODO: rewrite, list is not accessible anymore
    /*int count = Main.mainFrame.list.getModel().getSize();
    for (int i = 0; i < count; ++i)
    {
      Game rom = Main.mainFrame.getModel().getElementAt(i);
      if (rom.getStatus().isComplete())
        visibleRoms.add(rom);
    }
    
    return r -> visibleRoms.contains(r);*/
    
    return null;
  }
  
  public class CopyWorker extends RomSetWorker<ExportRomsPlugin>
  {
    public CopyWorker(GameSet romSet, ExportRomsPlugin plugin, Consumer<Boolean> callback)
    {
      super(romSet, plugin, buildFilterPredicate(), callback);
    }

    @Override
    public void execute(Game r)
    {
      // TODO: rewrite for new management
      /*try
      {
        Files.copy(r.getHandle().path(), path.resolve(r.getHandle().path().getFileName()));  
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }*/
    }
  }
  
  @Override public String getTitle() { return "Exporting roms"; }
  @Override public String getProgressText() { return "Exporting..."; }
  @Override public String getMenuCaption() { return "Export Selection"; }
  @Override public String getSubmenuCaption() { return "Export"; }
}
