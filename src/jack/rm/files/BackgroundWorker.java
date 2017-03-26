package jack.rm.files;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

import com.pixbits.lib.concurrent.OperationDetails;

import jack.rm.Main;

public abstract class BackgroundWorker<E, T extends OperationDetails> extends SwingWorker<Void, Integer>
{
  protected final List<E> data;
  protected final T operation;
  protected final Consumer<Boolean> callback;
  
  private String getTitle() { return operation.getTitle(); }
  private String getProgressText() { return operation.getProgressText(); }
  
  /*protected BackgroundWorker(T operation, Consumer<Boolean> callback)
  {
    this(new ArrayList<E>(), operation, callback);
  }*/
  
  public BackgroundWorker(List<E> data, T operation, Consumer<Boolean> callback)
  {
    this.data = data;
    this.operation = operation;
    this.callback = callback;
  }
  
  //protected void add(E item) { data.add(item); }

  @Override
  public Void doInBackground()
  {
    Main.progress.show(Main.mainFrame, getTitle(), null);
    
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
    Main.progress.update(this, getProgressText()+" "+v.get(v.size()-1)+" of "+data.size()+"..");
    Main.mainFrame.rebuildGameList();;
  }
  
  @Override
  public void done()
  {
    try
    {
      get();
      Main.progress.finished();
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