package jack.rm.gui;

import javax.swing.JOptionPane;
import java.awt.Component;

public class Dialogs
{
  public static void showMessage(String title, String message, Component parent)
  {
    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }
  
  public static void showWarning(String title, String message, Component parent)
  {
    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
  }
  
  public static void showError(String title, String message, Component parent)
  {
    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
  }
  
  public static void showQuestion(String title, String message, Component parent, Runnable callback)
  {
    if (JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
      callback.run();
  }
}
