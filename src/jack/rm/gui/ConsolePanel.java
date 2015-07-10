package jack.rm.gui;

import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	JTextArea console;
	
	ConsolePanel()
	{
		console = new JTextArea(25,80);
		console.setEditable(false);
		JScrollPane scroll = new JScrollPane(console);
		this.setLayout(new BorderLayout());
		this.add(scroll,BorderLayout.CENTER);
	}
	
	public void append(String str)
	{
		console.append(str);
		console.setCaretPosition(console.getText().length() - 1);
	}
	
	public void appendln(String str)
	{
		console.append(str+System.getProperty("line.separator"));
		console.setCaretPosition(console.getText().length() - 1);
	}
}
