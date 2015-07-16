package jack.rm.files;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

import com.pixbits.gui.ProgressDialog;

import jack.rm.Main;
import jack.rm.data.Rom;
import jack.rm.data.RomList;
import jack.rm.plugins.OrganizerPlugin;

public abstract class OrganizerWorker<T extends OrganizerPlugin> extends SwingWorker<Void, Integer>
{
  protected int total = 0;
  protected final RomList list;
  protected final T plugin;
  protected final Consumer<Boolean> callback;
  
  public OrganizerWorker(RomList list, T plugin, Consumer<Boolean> callback)
  {
    this.list = list;
    this.plugin = plugin;
    total = list.count();
    this.callback = callback;
  }

  @Override
  public Void doInBackground()
  {
    ProgressDialog.init(Main.mainFrame, "Rom Organize", null);
    
    for (int i = 0; i < list.count(); ++i)
    {
      setProgress((int)((((float)i)/total)*100));

      Rom rom = list.get(i); 
      Organizer.moveRom(rom);

      publish(i);
    }

    return null;
  }
  
  @Override
  public void process(List<Integer> v)
  {
    ProgressDialog.update(this, "Organizing "+v.get(v.size()-1)+" of "+list.count()+"..");
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