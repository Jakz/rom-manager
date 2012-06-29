package jack.rm.gui;

import jack.rm.Main;
import jack.rm.data.Language;
import jack.rm.data.Location;
import jack.rm.data.Renamer;
import jack.rm.data.RomSize;
import jack.rm.i18n.Text;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;

public class RenamerFrame extends JFrame implements CaretListener, ActionListener
{
	private JTextField patternField = new JTextField(30);
	private JTextField exampleField = new JTextField(30);
	
	private JButton close = new JButton(Text.TEXT_CLOSE.text());
	
	private JTable patterns;
	
	public class TableModel extends AbstractTableModel
	{
    public int getColumnCount() { return 2; }
    public int getRowCount() { return Renamer.patterns.size();}
    public String getColumnName(int col) { return col == 0 ? "Code" : "Description"; }
    public Object getValueAt(int row, int col) {
    	Renamer.Pattern p = Renamer.patterns.get(row);
    	return col == 0 ? p.code : p.desc;
    }
	};
	
	private TableModel model = new TableModel();
	
	public RenamerFrame()
	{
		JPanel fields = new JPanel(new GridBagLayout());
		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		
		GridBagConstraints c = new GridBagConstraints();
		
		updateGBC(c, 0,0,1,1);
		c.ipadx = 5;
		fields.add(new JLabel(Text.TEXT_RENAMER_PATTERN.text()+":"), c);
		updateGBC(c, GridBagConstraints.RELATIVE,0,4,1);
		c.ipadx = 0;
		fields.add(patternField,c);
		updateGBC(c, 0,GridBagConstraints.RELATIVE,1,1);
		c.ipadx = 5;
		fields.add(new JLabel(Text.TEXT_RENAMER_EXAMPLE.text()+":"),c);
		updateGBC(c, GridBagConstraints.RELATIVE,1,4,1);
		c.ipadx = 0;
		fields.add(exampleField,c);
		
		patterns = new JTable(model);
		patterns.setAutoCreateRowSorter(true);
		patterns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		patterns.addMouseListener(
				new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						if (e.getClickCount() == 2){
							int r = patterns.getSelectedRow();
							
							if (r != -1)
							{
								String code = Renamer.patterns.get(r).code;
								int p = patternField.getCaretPosition();
								String before = patternField.getText().substring(0, p);
								String after = patternField.getText().substring(p);
								patternField.setText(before+code+after);
								caretUpdate(null);
								patternField.setCaretPosition(p+2);
								patternField.requestFocusInWindow();
							}
						}
					}
				});
		/*patterns.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		patterns.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		patterns.getColumnModel().getColumn(0).setWidth(80);*/
		JScrollPane scrollPane = new JScrollPane(patterns);
		
		
		
		exampleField.setEnabled(false);
		exampleField.setDisabledTextColor(Color.BLACK);  
		patternField.addCaretListener(this);
		
		close.addActionListener(this);
		close.setPreferredSize(new Dimension(100,30));

		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(close);

		main.add(fields,BorderLayout.NORTH);
		main.add(scrollPane,BorderLayout.CENTER);
		main.add(buttons,BorderLayout.SOUTH);
		
		this.add(main);
		
		pack();
	}
	
	public void updateGBC(GridBagConstraints c, int x, int y, int w, int h)
	{
		c.gridx = x; c.gridy = y; c.gridwidth = w; c.gridheight = h;
	}
	
	public void showMe()
	{
		if (this.isVisible())
			return;
		
		updateFields();
		setLocationRelativeTo(Main.mainFrame);
		
		setVisible(true);
	}
	
	public void updateFields()
	{
		patternField.setText(Renamer.renamingPattern);
	}

	public void caretUpdate(CaretEvent e)
	{
		Renamer.renamingPattern = patternField.getText();
		exampleField.setText(Renamer.getCorrectName(Main.romList.get(0)));
	}
	
	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
		Main.romList.checkNames();
	}

}
