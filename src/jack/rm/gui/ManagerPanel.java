package jack.rm.gui;

import jack.rm.Settings;
import jack.rm.i18n.Text;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

public class ManagerPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	public JLabel romsPathLabel;
	public JTextField romsPath;
	public JButton romsPathButton;
	
	
	public ManagerPanel()
	{
		romsPathLabel = new JLabel(Text.ROMSET_ROMS_PATH.text());
		romsPath = new JTextField(30);
		romsPathButton = new JButton("...");
		romsPathButton.addActionListener(this);
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		u(c,0,0,1,1);
		panel.add(romsPathLabel,c);
		u(c,1,0,3,1);
		panel.add(romsPath,c);
		u(c,4,0,4,1);
		panel.add(romsPathButton,c);
		u(c,0,1,2,1);
		
		this.add(panel);
	}
	
	public void u(GridBagConstraints c, int x, int y, int w, int h)
	{
		c.gridx = x; c.gridy = y; c.gridwidth = w; c.gridheight = h;
	}
	
	public void updateFields()
	{
		Settings s = Settings.current();
		
		//unknownPath.setEnabled(s.moveUnknownFiles);
		//unknownPathButton.setEnabled(s.moveUnknownFiles);
		//moveUnknownFiles.setSelected(s.moveUnknownFiles);
		
		if (s.romsPath != null)
		  romsPath.setText(s.romsPath.toString());
	}
	
	@Override
  public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == romsPathButton)
		{
			final JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int response = jfc.showOpenDialog(this);
			
			if (response == JFileChooser.APPROVE_OPTION)
			{
				File f = jfc.getSelectedFile();
				
				
				romsPath.setText(f.getPath());
				Settings.current().romsPath = f.toPath();
			}
		}
	}
}
