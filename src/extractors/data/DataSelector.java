package extractors.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import events.EventParser;
import events.GenericEvent;
import keystroke.KeyStrokeParser;

/**
 * At its heart the DataSelector is a wrapper class for a JDBC connection to a MySQL Database.
 * <p><p>
 * The job of the DataSelector is to communicate with the database and return a Collection of DataNode Objects for use in experiments.
 * <p><p>
 * In current form the DataSelector can only handle raw queries to the database.
 * 
 * @author Patrick Koch
 * @see DataNode
 * @see Collection
 */
public class DataSelector {

	private String url;
	private String dbName;
	private String userName;
	private String password;
	private Connection conn = null;
	private Statement stmt = null;
	private LinkedList<DataNode> dataList = new LinkedList<DataNode>();
	private String query;
	private EventParser<? extends GenericEvent> eventParser;
	
	/**
	 * Sets the eventType used for parsing.
	 * 
	 * @param eventType a GenericEvent which handles the method parseSession.
	 */
	public void setEventParser(EventParser<?> eventParser) {
		this.eventParser = eventParser;
	}

	/**
	 * Returns the query string used for database operations.
	 * 
	 * @return the query string used for database operations.
	 */
	public String getQuery() {
		return query;
	}
	
	/**
	 * Sets the SQL query string that will be used during the query() method.
	 * 
	 * @param query the query that will be used during the query() method.
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Returns true if there is no data currently held by this DataSelector object.
	 * 
	 * @return true if there is no data currently held by this DataSelector object.
	 */
	public boolean isEmpty() {
		return dataList.isEmpty();
	}
	/**
	 * Creates a DataSelector and connects to the database.
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public DataSelector() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		this.connect();	
	}
	
	/**
	 * Connects to a MySQL server instance using settings specified in DbConfig.java.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @see DbConfig
	 */
	public void connect() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		url = "jdbc:mysql://" + DbConfig.HOST_NAME + ":" + DbConfig.PORT + "/";
		dbName = DbConfig.DB_NAME;
		userName = DbConfig.USER_NAME;
		password = DbConfig.PASSWORD;
		conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url+dbName,userName,password);
		System.out.println("Connected to the database"); 
	}
	
	/**
	 * Runs stored query on the database. Stores the result into private variable dataList.
	 * 
	 * @param query
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public void query() throws SQLException {
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		dataList.clear();
		DataNode dn = new DataNode();
		int curUser = 0;
		int oldUser = 0;
		Answer answer;
		// this sets the event parser.
		// KeyStrokeParser for phase6/7
		// TParser should be used going forward (ballchair).
		setEventParser(new KeyStrokeParser());
		/*setEventParser(new TParser());
		//////////////////////////////////////////////
		if (rs.next()) { 
			curUser = rs.getInt("subj_id");
			String keyStrokes = rs.getString("EventStream");
			answer = new Answer(rs.getString("CharStream"), rs.getString("FinalText"), 
					keyStrokes, rs.getInt("TimeStamp"), rs.getInt("Q_Id"), rs.getInt("O_Id"),
					0, rs.getString("TypeFlag"), (Collection<GenericEvent>) eventParser.parseSession(keyStrokes));
			dn.add(answer);
			oldUser = curUser;
		}
		while (rs.next()) {
			curUser = rs.getInt("subj_id");
			if (curUser != oldUser) {
				dn.setUserID(oldUser);
				dataList.add(dn);
				System.out.println(dn);
				dn = new DataNode();
			}
			String keyStrokes = rs.getString("EventStream");
			answer = new Answer(rs.getString("CharStream"), rs.getString("FinalText"), 
					keyStrokes, rs.getInt("TimeStamp"), rs.getInt("Q_Id"), rs.getInt("O_Id"),
					0, rs.getString("TypeFlag"), (Collection<GenericEvent>) eventParser.parseSession(keyStrokes));
			dn.add(answer);
			oldUser = curUser;
			//System.out.println("Answer Id: " + rs.getInt("TimeStamp"));
		}*/
		/////////////////////////////////
		if (rs.next()) { 
			curUser = rs.getInt("Subj_Id");
			String keyStrokes = rs.getString("keystrokes");
			answer = new Answer(rs.getString("charstream"), rs.getString("finaltext"), 
					keyStrokes, rs.getInt("D_Id"), rs.getInt("Q_Id"), rs.getInt("O_Id"),
					rs.getInt("Cog_Load"), rs.getString("TypeFlag"), (Collection<GenericEvent>) eventParser.parseSession(keyStrokes));
			dn.add(answer);
			oldUser = curUser;
		}
		while (rs.next()) {
			curUser = rs.getInt("Subj_Id");
			if (curUser != oldUser) {
				dn.setUserID(oldUser);
				dataList.add(dn);
				System.out.println(dn);
				dn = new DataNode();
			}
			String keyStrokes = rs.getString("keystrokes");
			answer = new Answer(rs.getString("charstream"), rs.getString("finaltext"), 
					keyStrokes, rs.getInt("D_Id"), rs.getInt("Q_Id"), rs.getInt("O_Id"),
					rs.getInt("Cog_Load"), rs.getString("TypeFlag"), (Collection<GenericEvent>) eventParser.parseSession(keyStrokes));
			dn.add(answer);
			oldUser = curUser;
		}
		dn.setUserID(oldUser);
		dataList.add(dn);
		System.out.println(dn);
		System.out.println(dataList.size() + " Users Selected." );
		stmt.close();
	}
	
	/**
	 * Allows developer to run queries to update underlying databases.
	 * 
	 * @param query A query string to modifies the connected database.
	 * @throws SQLException if supplied query string returns a ResultSet object.
	 */
	public void maintenanceQuery(String query) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}
	
	/**
	 * Returns the result of the last query as a Collection.
	 * 
	 * @return the result of the last query as a Collection.
	 * @see Collection
	 * @see DataNode
	 */
	public LinkedList<DataNode> getData() {
		return dataList;
	}
	
	/**
	 * Closes the underlying database connection.
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		conn.close();
	}
	
	public int getSize() {
		return dataList.size();
	}
}
