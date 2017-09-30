import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	private JTextField errorText=new JTextField(20);
	private JPanel p=new JPanel(new GridLayout(13,2));
	private JButton nxt = new JButton("NEXT");
	private JButton prev = new JButton("PREVIOUS");
	private JButton add = new JButton("ADD");
	private JButton update= new JButton("UPDATE");
	private JButton del = new JButton("DELETE");
	private JButton clear = new JButton("CLEAR");

	public DBReader(){
		//Set GUI dimensions
		f.setPreferredSize(new Dimension(800, 400));
		//disable text entry in error text field
		errorText.setEditable(false);

		// Setup buttons to detect input
		nxt.addActionListener(this);
		prev.addActionListener(this);
		add.addActionListener(this);
		update.addActionListener(this);
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
		p.add(add); p.add(update);
		p.add(del); p.add(clear);
		p.add(errorText);
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
			s.executeQuery("SELECT * FROM " + "employee");
			rs = s.getResultSet();

			switch(e.getActionCommand()) {
			case "NEXT": 
				if (rs.next()) {
					errorText.setText("");
					getEntry();
				} else errorText.setText("NO  NEXT ENTRY");
				break;
			case "PREVIOUS": 
				if (rs.previous()){
					errorText.setText("");
					getEntry();
				} else {
					clearEntries();
					errorText.setText("NO PREVIOUS ENTRY");
				}
				break;
			case "ADD": 
				addEntry();
				break;
			case "DELETE":
				deleteEntry();
				break;
			case "CLEAR":
				clearEntries();
				break;
			}
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
		int ssn = 0;
		if (checkNumber(ssnText.getText(), true))
			ssn = Integer.parseInt(ssnText.getText());
		else 
			return;

		String bdate = bdateText.getText();
		Date date1 = new Date(System.currentTimeMillis());
		if (!bdate.equals(""))
			try {
				date1 = (Date) new SimpleDateFormat("yyyy/mm/dd").parse(bdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		String name = nameText.getText();
		String addr = addrText.getText();

		float salary = 0.0f;
		if (checkNumber(ssnText.getText(), false))
			salary = Float.parseFloat(salaryText.getText());

		String gen= genderText.getText();
		String wf = wfText.getText(); 
		if (wf.equals("")) wf = "0";
		String man = manText.getText();
		if (man.equals("")) man = "0";
		String sup = supText.getText();
		if (sup.equals("")) sup = "0";

		ResultSet res = s.executeQuery("SELECT * FROM Employee WHERE Ssn = " + "ssn");
		if (!res.next()) {
			s.executeUpdate( 
					"INSERT INTO Employee (Ssn, Bdate, Name, Address, Salary, Sex, Works_For, Manages, Supervises)" 
							+"VALUES ('" +ssn+ "','" +date1+ "','"+name+ "', '"
							+addr+ "','" +salary+ "','" +gen+ "','" 
							+wf+ "','" +man+ "','" +sup+ "')");
		} else
			errorText.setText("ERROR4: DUPLICATE SSN, ENTRY NOT ADDED");

		s.executeQuery("SELECT * FROM " + "employee");
		rs = s.getResultSet();
	}

	// Check if a number in a string is valid
	private boolean checkNumber(String num, boolean isInt) {
		if(num.equals("") || (!num.matches("[0-9]+")) || (!num.matches("[0-9]+\\.?"))) {
			errorText.setText("ERROR1: INVALID NUMBER, ENTRY NOT ADDED");
			return false;
		} else if (num.matches("[0-9]+") && isInt)
			return true;
		else if (num.matches("[0-9]+\\.?") && !isInt)
			return true;
		else
			return false;
	}

	//Function to delete an entry
	private void deleteEntry() throws SQLException {
		if(!ssnText.getText().equals("")) {
			s.executeUpdate("DELETE FROM Employee WHERE Ssn = " + Integer.parseInt(ssnText.getText()) );
			clearEntries();
		}
		s.executeQuery("SELECT * FROM " + "employee");
		if(s.getResultSet() == null)
			errorText.setText("TABLE IS EMPTY, RESULT SET CLOSED");
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
		errorText.setText("");
		if (!rs.isClosed())
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