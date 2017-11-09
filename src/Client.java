import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class Client extends JFrame implements ActionListener{
	// Text field for receiving results
	private JTextField jtf = new JTextField();
	// Text area to display contents
	private JTextArea jta = new JTextArea();
	private JPanel gl=new JPanel(new GridLayout(3,2, 5, 5));
	private JTextField id = new JTextField();
	private JTextField mod = new JTextField();
	private JButton req= new JButton("REQUEST");

	// IO streams
	private Socket socket = null;
	private DataOutputStream toServer;
	private DataInputStream fromServer;

	public static void main(String[] args) {
		try {
			new Client();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Client() throws IOException {
		// GUI setup
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(gl, BorderLayout.WEST);
		gl.add(new JLabel("Enter Student ID"));
		gl.add(id);
		gl.add(new JLabel("Enter Module Name"));
		gl.add(mod);
		req.addActionListener(this);
		gl.add(req);
		jtf.setHorizontalAlignment(JTextField.RIGHT);

		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
		add(new JScrollPane(jta), BorderLayout.CENTER);

		setTitle("Client");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		try {
			// Create a socket to connect to the server
			socket = new Socket("localhost", 8000);
			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

			String wm = fromServer.readUTF();
			// Display welcome message the text area
			jta.append(wm);
		} catch (IOException ex) {
			jta.append(ex.toString() + " bye!\n");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "REQUEST":
			try {
				if(toServer != null) {
					// Send the data to the server
					if ((!id.getText().equals("")) && (id.getText().matches("[0-9]+")) && (!mod.getText().equals(""))){ //regex for digits only
						toServer.writeInt(Integer.parseInt(id.getText()));
						toServer.writeUTF(mod.getText()); 
						toServer.flush();

						int check = fromServer.readInt();
						if(!(check== -1)) {
							// Get values from server
							jta.append("Student id: " + check +"\n");
							jta.append("First name: " + fromServer.readUTF() +"\n");
							jta.append("Last name: " + fromServer.readUTF() +"\n");
							jta.append("Module name: " + fromServer.readUTF() +"\n");
							jta.append("CA: " + fromServer.readFloat() +"\n");
							jta.append("Exam: " + fromServer.readFloat() +"\n");
							jta.append("Final: " + fromServer.readFloat() +"\n");
						} else {
							jta.append("No details found for these entries!\n");
							break;
						} 
					} else {
						jta.append("Invalid id or Module!\n");
						break;
					}
				} else {
					jta.append("No connection to server!\n");
					break;
				}
			}catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		}
	}
}