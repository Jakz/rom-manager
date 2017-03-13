package jack.rm.files;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

import com.pixbits.lib.ui.elements.ProgressDialog;

import jack.rm.Main;

public abstract class BackgroundWorker<E, T extends BackgroundOperation> extends SwingWorker<Void, Integer>
{
  protected final List<E> data;
  protected final T operation;
  protected final Consumer<Boolean> callback;
  protected final String title;
  protected final String progressText;
  
  protected BackgroundWorker(T operation, Consumer<Boolean> callback)
  {
    this(new ArrayList<E>(), operation, callback);
  }
  
  public BackgroundWorker(List<E> data, T operation, Consumer<Boolean> callback)
  {
    this.data = data;
    this.operation = operation;
    this.callback = callback;
    
    this.title = operation.getTitle();
    this.progressText = operation.getProgressText();
  }
  
  protected void add(E item) { data.add(item); }

  @Override
  public Void doInBackground()
  {
    ProgressDialog.init(Main.mainFrame, title, null);
    
    for (int i = 0; i < data.size(); ++i)
    {
      int progress = (int)((((float)i)/data.size())*100);
      setProgress(progress);

      E rom = data.get(i); 
      execute(rom);

      publish(i);
    }

    return null;
  }
  
  @Override
  public void process(List<Integer> v)
  {
    ProgressDialog.update(this, progressText+" "+v.get(v.size()-1)+" of "+data.size()+"..");
    Main.mainFrame.updateTable();
  }
  
  @Override
  public void done()
  {
    try
    {
      get();
      ProgressDialog.finished();
      callback.accept(true);
    }
    catch (ExecutionException e)
    {
      Throwable cause = e.getCause();
      cause.printStackTrace();
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }
  
  public abstract void execute(E element);
}