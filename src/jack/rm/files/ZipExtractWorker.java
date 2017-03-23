package jack.rm.files;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import jack.rm.Main;

import com.pixbits.lib.lang.StringUtils;

public class ZipExtractWorker<T extends BackgroundOperation> extends SwingWorker<Path, Long>
{
  protected final T operation;
  protected final Consumer<Boolean> callback;
  protected final String title;
  protected final String progressText;
  protected final Path src;
  protected final Path dest;
  
  protected final JFrame parent;
  
  protected long size;
  protected long processed;
  protected int BUFFER_SIZE = 8192;
  
  public ZipExtractWorker(Path src, Path dest, T operation, Consumer<Boolean> callback, JFrame parent)
  {
    this.src = src;
    this.dest = dest;
    
    this.parent = parent;
    
    this.operation = operation;
    this.callback = callback;
    
    this.title = operation.getTitle();
    this.progressText = operation.getProgressText();
  }
  
  @Override
  public Path doInBackground()
  {
    Main.progress.show(parent, title, null);
    
    try (ZipFile zfile = new ZipFile(src.toFile()))
    { 
      ZipEntry entry = zfile.entries().nextElement();
      size = entry.getSize();
      
      try (InputStream is = new BufferedInputStream(zfile.getInputStream(entry)))
      {
        try (OutputStream os = Files.newOutputStream(dest, StandardOpenOption.CREATE))
        {   
          byte[] buffer = new byte[8192];
          
          int chunk = 0;
          
          while ((chunk = is.read(buffer)) > 0)
          {
            processed += chunk;
            
            setProgress((int)((processed / (float)size)*100));
            publish(processed);
            
            os.write(buffer, 0, chunk);
          }
 
        } 
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }


    return null;
  }
  
  @Override
  public void process(List<Long> v)
  {
    Main.progress.update(this, progressText + " " + 
                                StringUtils.humanReadableByteCount(v.get(v.size()-1)) +
                                " of " + 
                                StringUtils.humanReadableByteCount(size)+"..");
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
}