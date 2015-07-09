package jack.rm.gui;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.*;

public class ProgressDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	JLabel title;
	JLabel desc;
	JProgressBar progress;
	
	public ProgressDialog(Frame frame, String title)
	{
		super(frame, title);
		
		//this.setUndecorated(true);
		
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());
		
		progress = new JProgressBar();

		desc = new JLabel("...");
		desc.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel.add(progress, BorderLayout.CENTER);
		panel.add(desc, BorderLayout.SOUTH);
		
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		this.add(panel);
		
		pack();
		this.setLocationRelativeTo(frame);
	}
	
	public ProgressDialog()
	{
		this(null, "");
	}

	private static ProgressDialog dialog;
	
	public static void init(Frame parent, String title)
	{
	  dialog = new ProgressDialog(parent, title);
	  dialog.progress.setMaximum(100);
	  dialog.progress.setValue(0);
	  dialog.setVisible(true);
	}
	
	public static void update(SwingWorker<?,?> worker, String desc)
	{
	  dialog.progress.setValue(worker.getProgress());
	  dialog.desc.setText(desc);
	}
	
	public static void finished()
	{
	  dialog.dispose();
	  dialog = null;
	}
}
