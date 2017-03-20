package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.error.ParserException;

import jack.rm.data.romset.GameSet;
import jack.rm.script.Script;
import jack.rm.script.ScriptEnvironment;
import jack.rm.script.ScriptParser;
import jack.rm.script.ScriptStdout;

public class ConsolePanel extends JPanel implements KeyListener, ScriptStdout
{
  private final JTextArea console;
  private int startCommandPosition;
  
  private Parser<Script> parser;
  
  ConsolePanel()
  {
    console = new JTextArea();
    JScrollPane pane = new JScrollPane(console);
    pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
    console.setFont(new Font("Monaco", Font.BOLD, 12));
    console.setBackground(Color.BLACK);
    console.setForeground(new Color(177,242,27));
    
    this.setLayout(new BorderLayout());
    this.add(pane, BorderLayout.CENTER);
    
    parser = new ScriptParser().script();
    
    console.addKeyListener(this);
    
    appendPrompt();
  }
  
  public void appendPrompt()
  {
    /*if (console.getText().length() != 0 && console.getText().charAt(console.getText().length()-1) != '\n');
      console.append("\n");*/
    
    console.append("> ");
    console.setCaretPosition(console.getText().length());
    startCommandPosition = console.getText().length();
  }
  
  public void syntaxError(String message)
  {
    console.append("\nSyntax error: "+message.replaceAll("\n", " ")+"\n");
  }
  
  public void keyPressed(KeyEvent k)
  {

  }
  
  public void keyReleased(KeyEvent k)
  {
    if (k.getKeyChar() == KeyEvent.VK_ENTER)
    {
      try
      {
        String command = console.getText(startCommandPosition, console.getText().length() - startCommandPosition);
        System.out.println("Executing \'"+command+"\'");
        
        Script script = parser.parse(command);
        script.execute(new ScriptEnvironment(GameSet.current, this));
      }
      catch (ParserException e)
      {
        //ParseErrorDetails details = e.getErrorDetails();
        syntaxError(e.getMessage());

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      finally
      {
        appendPrompt();
      }
    }
  }
  
  public void keyTyped(KeyEvent k)
  {
    
  }
  
  public void append(String text)
  {
    console.append(text+"\n");
  }
}
