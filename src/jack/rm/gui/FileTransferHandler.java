package jack.rm.gui;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class FileTransferHandler extends TransferHandler {
  public static interface Listener
  {
    public void filesDropped(File[] files);
  }
  
  private static final DataFlavor FILE_FLAVOR = DataFlavor.javaFileListFlavor;
  
  private final Listener listener;
  private final Component component;

  public FileTransferHandler(Component component, Listener listener)
  {
    this.listener = listener;
    this.component = component;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean importData(TransferHandler.TransferSupport support)
  {
    if (!canImport(support))
      return false;
    
    Transferable t = support.getTransferable();
    
    try
    {
      List<File> files = (List<File>)t.getTransferData(FILE_FLAVOR);
      
      support.setDropAction(LINK);
      
      listener.filesDropped(files.toArray(new File[files.size()]));
    } 
    catch (IOException e)
    {
      return false;
    } 
    catch (UnsupportedFlavorException e)
    {
      return false;
    }
    
    return true;
  }

  @Override
  public boolean canImport(TransferHandler.TransferSupport support)
  {
    if (!support.isDataFlavorSupported(FILE_FLAVOR))
      return false;
    
    return true;
  }
}