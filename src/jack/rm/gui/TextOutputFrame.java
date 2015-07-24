package jack.rm.gui;

import javax.swing.*;

import java.awt.Component;
import java.awt.Dimension;

public class TextOutputFrame extends JFrame
{
  private final JTextArea textArea;
  
  TextOutputFrame()
  {
    textArea = new JTextArea();
    JScrollPane sp = new JScrollPane(textArea);
    sp.setPreferredSize(new Dimension(800,600));
    this.add(sp);
    pack();
  }
  
  void showWithText(Component parent, String text)
  {
    this.setLocationRelativeTo(parent);
    textArea.setText(text);
    this.setVisible(true);
  }
}
