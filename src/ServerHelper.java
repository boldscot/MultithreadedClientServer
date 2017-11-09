import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ServerHelper extends Thread{
	private DataOutputStream toClient=null;
	private DataInputStream fromClient= null;
	private Socket socket = null;

	//Required fields for database communication
	private final String userName = "root";
	private final String password = "";
	private final String serverName = "localhost";
	private final int portNumber = 3306;
	private final String dbName = "gradedatabase";

	private Statement s = null;
	private ResultSet rs = null;
	private Connection conn = null;

	// Fields for testing before database communication
	private final int TESTID = 1234;
	private final String FIRST_NAME = "Joe";
	private final String LAST_NAME = "Bloggs";
	private final String MODULE = "DIS SYS";
	private final float CA_MARK = 75.0f;
	private final float EXAM_MARK = 25.0f;
	private final float FINAL_MARK = (30.0f/100.0f * CA_MARK) + (70.0f/100.0f * EXAM_MARK);

	public ServerHelper(Socket socket) {
		super("ServerHelper");
		this.socket = socket;
	}

	public void run(){
		try {
			// connect to database
			connect();
			//get details of client
			InetAddress inetAddress = socket.getInetAddress();
			String welcomeMessage = "Welcome to the server " + inetAddress.getHostName() + "\n";
			// Create an input stream to receive data from the client
			fromClient = new DataInputStream(socket.getInputStream());
			// Create an output stream to send data to the client
			toClient = new DataOutputStream(socket.getOutputStream());
			//send welcome message to the client
			toClient.writeUTF(welcomeMessage);
			toClient.flush();

			while (true) {
				// store id and module details sent from client
				int id = fromClient.readInt();
				String module = fromClient.readUTF();

				if (isMatch(id, module)) {
					toClient.writeInt(rs.getInt("STUD_ID"));
					toClient.writeUTF(FIRST_NAME);
					toClient.writeUTF(LAST_NAME);
					toClient.writeUTF(module);
					toClient.writeFloat(CA_MARK);
					toClient.writeFloat(EXAM_MARK);
					toClient.writeFloat(FINAL_MARK);
					toClient.flush();
					System.out.println("found");
				}


				//Get data if a match is found
				if(id==TESTID && module.equals(MODULE)) {
					toClient.writeInt(id);
					toClient.writeUTF(FIRST_NAME);
					toClient.writeUTF(LAST_NAME);
					toClient.writeUTF(module);
					toClient.writeFloat(CA_MARK);
					toClient.writeFloat(EXAM_MARK);
					toClient.writeFloat(FINAL_MARK);
					toClient.flush();
				} else {
					toClient.writeInt(-1); // Use -1 as flag for bad entries
					toClient.flush();
				}
			}
		} catch (IOException | SQLException e) {
			System.err.println(e);
		}
	}

	protected void finalize(){
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}

	private boolean isMatch(int id, String module) throws SQLException {
		// Initialize ResultSet
		s = conn.createStatement();
		s.executeQuery("SELECT * FROM modulegrades WHERE STUD_ID='"+ id +"' and ModuleName='"+module+"'");
		rs = s.getResultSet();

		if(rs.next()) return true;
		else return false;
	}

	private ResultSet queryStudents(int id) throws SQLException {
		ResultSet set = null;
		s = conn.createStatement();
		s.executeQuery("SELECT * FROM students WHERE STUD_ID='"+ id);
		set = s.getResultSet();
		
		return set;
	}

	// Connect to database
	private void connect() throws SQLException {
		try {
			conn = this.getConnection();
			System.out.println("Connected to database");
		} catch (SQLException e) {
			System.out.println("ERROR: Could not connect to the database");
			e.printStackTrace();
			return;
		}
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
}
