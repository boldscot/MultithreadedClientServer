import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class MultiThreadedServerA2 extends JFrame {
	// Text area for displaying contents
	private JTextArea jta = new JTextArea();
	private ServerSocket serverSocket = null;

	public static void main(String[] args) throws IOException {
		new MultiThreadedServerA2();
	}

	public MultiThreadedServerA2() throws IOException {
		// Place text area on the frame
		setLayout(new BorderLayout());
		add(new JScrollPane(jta), BorderLayout.CENTER);

		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		try {
			// Create a server socket
			serverSocket = new ServerSocket(8000);
			jta.append("Server started at " + new Date() + '\n');
			
			while (true) {
				// Listen for a connection request
				Socket socket = serverSocket.accept();
				new ServerHelper(socket).start();
				InetAddress inetAddress = socket.getInetAddress();
				jta.append(inetAddress.getHostName() + "connected to the server!\n");
			}
		} catch(IOException ex) {
			System.err.println(ex);
		} finally {
			serverSocket.close();
		}
	}
}