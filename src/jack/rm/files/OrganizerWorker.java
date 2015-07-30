package jack.rm.files;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

import com.pixbits.gui.ProgressDialog;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.BackgroundPlugin;

public abstract class OrganizerWorker<T extends BackgroundPlugin> extends SwingWorker<Void, Integer>
{
  protected int total = 0;
  protected final RomSet romSet;
  protected final T plugin;
  protected final Consumer<Boolean> callback;
  protected final String title;
  protected final String progressText;
  
  public OrganizerWorker(RomSet romSet, T plugin, Consumer<Boolean> callback)
  {
    this.romSet = romSet;
    this.plugin = plugin;
    total = romSet.list.count();
    this.callback = callback;
    
    this.title = plugin.getTitle();
    this.progressText = plugin.getProgressText();
  }

  @Override
  public Void doInBackground()
  {
    ProgressDialog.init(Main.mainFrame, title, null);
    
    for (int i = 0; i < romSet.list.count(); ++i)
    {
      setProgress((int)((((float)i)/total)*100));

      Rom rom = romSet.list.get(i); 
      execute(rom);

      publish(i);
    }

    return null;
  }
  
  @Override
  public void process(List<Integer> v)
  {
    ProgressDialog.update(this, progressText+" "+v.get(v.size()-1)+" of "+romSet.list.count()+"..");
    Main.mainFrame.updateTable();
  }
  
  @Override
  public void done()
  {
    ProgressDialog.finished();
    callback.accept(true);
    
    
    //if (Main.pref.organizeRomsDeleteEmptyFolders)
    //  deleteEmptyFolders();
  }
  
  public abstract void execute(Rom rom);
}