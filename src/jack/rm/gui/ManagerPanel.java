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
	
	public JCheckBox moveUnknownFiles;
	public JLabel unknownPathLabel;
	public JTextField unknownPath;
	public JButton unknownPathButton;
	
	public ManagerPanel()
	{
		romsPathLabel = new JLabel(Text.ROMSET_ROMS_PATH.text());
		romsPath = new JTextField(30);
		romsPathButton = new JButton("...");
		romsPathButton.addActionListener(this);
		moveUnknownFiles = new JCheckBox(Text.ROMSET_MOVE_UNKNOWN_FILES.text());
		moveUnknownFiles.addActionListener(this);
		unknownPathLabel = new JLabel(Text.ROMSET_UNKNOWN_PATH.text());
		unknownPath = new JTextField(30);
		unknownPathButton = new JButton("...");
		unknownPathButton.addActionListener(this);
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		u(c,0,0,1,1);
		panel.add(romsPathLabel,c);
		u(c,1,0,3,1);
		panel.add(romsPath,c);
		u(c,4,0,4,1);
		panel.add(romsPathButton,c);
		u(c,0,1,2,1);
		panel.add(moveUnknownFiles,c);
		u(c,0,2,1,1);
		panel.add(unknownPathLabel,c);
		u(c,1,2,3,1);
		panel.add(unknownPath,c);
		u(c,4,2,4,1);
		panel.add(unknownPathButton,c);
		
		this.add(panel);
	}
	
	public void u(GridBagConstraints c, int x, int y, int w, int h)
	{
		c.gridx = x; c.gridy = y; c.gridwidth = w; c.gridheight = h;
	}
	
	public void updateFields()
	{
		Settings s = Settings.current();
		
		unknownPath.setEnabled(s.moveUnknownFiles);
		unknownPathButton.setEnabled(s.moveUnknownFiles);
		moveUnknownFiles.setSelected(s.moveUnknownFiles);
		
		if (s.romsPath != null)
		  romsPath.setText(s.romsPath.toString());
		
		if (s.unknownPath != null)
		  unknownPath.setText(s.unknownPath.toString());
		
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
		else if (e.getSource() == unknownPathButton)
		{
			final JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int response = jfc.showOpenDialog(this);
			
			if (response == JFileChooser.APPROVE_OPTION)
			{
				File f = jfc.getSelectedFile();
				
				unknownPath.setText(f.getPath());
				Settings.current().unknownPath = f.toPath();
			}
		}
		else if (e.getSource() == moveUnknownFiles)
		{
			Settings s = Settings.current();
			s.moveUnknownFiles = moveUnknownFiles.isSelected();
			unknownPathButton.setEnabled(s.moveUnknownFiles);
			unknownPath.setEnabled(s.moveUnknownFiles);
			moveUnknownFiles.setSelected(s.moveUnknownFiles);
		}
	}
}
