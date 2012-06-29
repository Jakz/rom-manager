package jack.rm.gui;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog
{
	JLabel title;
	JLabel desc;
	JProgressBar progress;
	
	public ProgressDialog(Frame frame, String strtitle)
	{
		super(frame, strtitle);
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());
		
		title = new JLabel("Title");
		progress = new JProgressBar();
		desc = new JLabel("Long description...");
	}
	
	public ProgressDialog()
	{
		this(null, "");
	}
}
