import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mysql.jdbc.PreparedStatement;

@SuppressWarnings("serial")
public class DBReader extends Frame implements ActionListener{
	//Required fields for database communication
	private final String userName = "root";
	private final String password = "";
	private final String serverName = "localhost";
	private final int portNumber = 3306;
	private final String dbName = "Assignment1";
	private Statement s = null;
	private ResultSet rs = null;
	private Connection conn = null;

	// Fields for GUI
	private JFrame f=new JFrame();
	private JLabel ssnLabel=new JLabel("Ssn: ");
	private JLabel bdateLabel=new JLabel("Bdate: ");
	private JLabel nameLabel=new JLabel("Name: ");
	private JLabel addrLabel=new JLabel("Addr: ");
	private JLabel salaryLabel=new JLabel("Salary: ");
	private JLabel genderLabel=new JLabel("Gender: ");
	private JLabel wfLabel=new JLabel("Works For: ");
	private JLabel manLabel=new JLabel("Manages: ");
	private JLabel supLabel=new JLabel("Supervises: ");
	private JTextField ssnText=new JTextField(20);
	private JTextField bdateText=new JTextField(20);
	private JTextField nameText=new JTextField(80);
	private JTextField addrText=new JTextField(80);
	private JTextField salaryText=new JTextField(20);
	private JTextField genderText=new JTextField(20);
	private JTextField wfText=new JTextField(20);
	private JTextField manText=new JTextField(20);
	private JTextField supText=new JTextField(20);
	private JPanel p=new JPanel(new GridLayout(12,2));
	private JButton nxt = new JButton("NEXT");
	private JButton prev = new JButton("PREVIOUS");
	private JButton add = new JButton("ADD");
	private JButton del = new JButton("DELETE");
	private JButton clear = new JButton("CLEAR");

	public DBReader(){
		//Set GUI dimensions
		f.setPreferredSize(new Dimension(800, 400));

		// Setup buttons to detect input
		nxt.addActionListener(this);
		prev.addActionListener(this);
		add.addActionListener(this);
		del.addActionListener(this);
		clear.addActionListener(this);

		//add labels, buttons and text fields to GUI
		p.add(ssnLabel); p.add(ssnText); 
		p.add(bdateLabel); p.add(bdateText); 
		p.add(nameLabel); p.add(nameText);
		p.add(addrLabel); p.add(addrText);
		p.add(salaryLabel); p.add(salaryText);
		p.add(genderLabel); p.add(genderText);
		p.add(wfLabel); p.add(wfText); 
		p.add(manLabel); p.add(manText);
		p.add(supLabel); p.add(supText);
		p.add(nxt); p.add(prev);
		p.add(add); p.add(del);
		p.add(clear);
		f.add(p);
		f.setVisible(true);
		f.pack();
	}

	// Establish a connection with database
	private Connection getConnection() throws SQLException {
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		conn = DriverManager.getConnection(
				"jdbc:mysql://" 
						+ this.serverName + ":" 
						+ this.portNumber + "/" 
						+ this.dbName, connectionProps);

		return conn;
	}

	// Connect to database
	private void run() throws SQLException {
		try {
			conn = this.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			return;
		}
		// Initialize ResultSet
		s = conn.createStatement();
		s.executeQuery("SELECT * FROM " + "employee");
		rs = s.getResultSet();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		try {
			if (e.getSource() == nxt)
				if (rs.next())
					getEntry();
			if (e.getSource() == prev)
				if (rs.previous())
					getEntry();
				else {
					clearEntries();
					rs.beforeFirst();
				}
			//if (e.getSource() == add)
				//addEntry();
			if (e.getSource() == del)
				deleteEntry();
			if (e.getSource() == clear)
				clearEntries();
		} catch (SQLException exc) {
			exc.printStackTrace();
		}	
	}

	//Function to get database entries
	private void getEntry() throws SQLException {
		// Fill in text fields in GUI
		ssnText.setText(Integer.toString(rs.getInt("Ssn")));
		bdateText.setText(rs.getString("bdate"));
		nameText.setText(rs.getString("name"));
		addrText.setText(rs.getString("address"));
		salaryText.setText(rs.getString("salary"));
		genderText.setText(rs.getString("sex"));
		wfText.setText(Integer.toString(rs.getInt("works_for")));
		manText.setText(Integer.toString(rs.getInt("manages")));
		supText.setText(Integer.toString(rs.getInt("supervises")));
	}

	private void addEntry() throws SQLException {
		if (s.executeQuery(("SELECT * FROM Employee WHERE Ssn = " + Integer.parseInt(ssnText.getText()))) != null) 
			s.executeUpdate(
					"INSERT INTO Employee (Ssn, Bdate, Name,Address, Salary, Sex, Works_for, Manages, Supervises)"
							+ "VALUES"
							+ "('Integer.parseInt(ssnText.getText())', 'bdateText.getText()', 'nameText.getText()', "
							+ "'addrText.getText()', 'salaryText.getText()', 'genderText.getText()')"
					);
	}

	//Function to delete an entry
	private void deleteEntry() throws SQLException {
		if(!ssnText.getText().equals("")) {
			s.executeUpdate("DELETE FROM Employee WHERE Ssn = " + Integer.parseInt(ssnText.getText()) );
			clearEntries();
		}
	}

	// Function to clear the gui text fields
	private void clearEntries() throws SQLException {
		ssnText.setText("");
		bdateText.setText("");
		nameText.setText("");
		addrText.setText("");
		salaryText.setText("");
		genderText.setText("");
		wfText.setText("");
		manText.setText("");
		supText.setText("");
		rs.beforeFirst();
	}

	public static void main(String args[]) {
		DBReader app = new DBReader();
		try {
			app.run();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}