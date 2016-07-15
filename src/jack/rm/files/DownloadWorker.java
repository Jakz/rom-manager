package jack.rm.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import com.pixbits.gui.ProgressDialog;
import com.pixbits.strings.StringUtils;

public class DownloadWorker<T extends BackgroundOperation> extends SwingWorker<Path, Long>
{
  protected final T operation;
  protected final Consumer<Boolean> callback;
  protected final String title;
  protected final String progressText;
  protected final URL url;
  protected final Path savePath;
  protected Path tmpPath;
  
  protected final Map<String,String> postArguments;
  
  protected final JFrame parent;
  
  protected long downloadStatus;
  protected long downloadSize;
  protected int BUFFER_SIZE = 8192;
  
  public DownloadWorker(URL url, Path dest, T operation, Consumer<Boolean> callback, JFrame parent)
  {
    this(url, dest, operation, callback, parent, null);
  }
  
  public DownloadWorker(URL url, Path dest, T operation, Consumer<Boolean> callback, JFrame parent, Map<String,String> postArguments)
  {
    this.url = url;
    this.savePath = dest;
    
    this.parent = parent;
    
    this.operation = operation;
    this.callback = callback;
    
    this.title = operation.getTitle();
    this.progressText = operation.getProgressText();
    
    this.postArguments = postArguments;
    
    this.downloadStatus = 0;
  }
  
  @Override
  public Path doInBackground()
  {
    ProgressDialog.init(parent, title, null);
    
    try
    { 
      tmpPath = Files.createTempFile(null, null);
      
      try (OutputStream os = Files.newOutputStream(tmpPath))
      {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        
        if (postArguments != null && !postArguments.isEmpty())
        {
          final StringBuilder postData = new StringBuilder();
          postArguments.forEach((k,v) -> {
            try
            {
              if (postData.length() != 0)
                postData.append('&');
            
              postData.append(URLEncoder.encode(k, "UTF-8")).append('=').append(URLEncoder.encode(v, "UTF-8"));
             
            }
            catch (UnsupportedEncodingException e)
            {
              e.printStackTrace();
            }
          });
          
          byte[] postDataBytes = postData.toString().getBytes("UTF-8");
          
          connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
          connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
          connection.setDoOutput(true);
          connection.getOutputStream().write(postDataBytes);
        }
        
        connection.connect();
        
        if ((connection.getResponseCode() / 100) != 2)
          return null;
        
        downloadSize = connection.getContentLengthLong();
        
        byte[] buffer = new byte[BUFFER_SIZE];
                
        try (InputStream is = connection.getInputStream())
        {
        
          while (true)
          {
            int read = is.read(buffer, 0, BUFFER_SIZE);
            
            if (read == -1)
              break;
            else
              downloadStatus += read;
            
            os.write(buffer, 0, read);
            
            setProgress((int)((downloadStatus / (float)downloadSize)*100));
            publish(downloadStatus);
            
            if (downloadSize == downloadStatus)
              break;
          }
          
          connection.disconnect();
        
          if (downloadSize == downloadStatus)
            return tmpPath;
          else
            return null;
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
    ProgressDialog.update(this, progressText + " " + 
                                StringUtils.humanReadableByteCount(v.get(v.size()-1)) +
                                " of " + 
                                StringUtils.humanReadableByteCount(downloadSize)+"..");
  }
  
  @Override
  public void done()
  {
    try
    {
      Path tmpPath = get();
      
      if (tmpPath != null)
        Files.move(tmpPath, savePath, StandardCopyOption.REPLACE_EXISTING);
      
      ProgressDialog.finished();
      callback.accept(true);
    }
    catch (IOException e)
    {
      e.printStackTrace();
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