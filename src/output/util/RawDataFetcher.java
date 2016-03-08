package output.util;

import static extractors.data.DbConfig.DB_NAME;
import static extractors.data.DbConfig.HOST_NAME;
import static extractors.data.DbConfig.PASSWORD;
import static extractors.data.DbConfig.PORT;
import static extractors.data.DbConfig.USER_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RawDataFetcher {
	private String url;
	private String dbName;
	private String userName;
	private String password;
	private Connection conn = null;
	private Statement stmt = null;
	
	//This query will not work as intended for data that can change between sessions like PC_NO or Loc_NO
	private String query;
	//These are the really bad column names that i have to change...
		
	public RawDataFetcher() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException  {
			this.connect();
	}
	
	private void connect() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		url = "jdbc:mysql://" + "localhost" + ":" + "3306" + "/";
		dbName = "ballchairstudy1";//DB_NAME;
		userName = "root";
		password = "";
		conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url+dbName,userName,password);
		stmt = conn.createStatement();
	}
	
	public void use(String db) throws SQLException {
		conn.setCatalog(db);
	}
	
	public ResultSet query(String query) throws SQLException {
		ResultSet rs = stmt.executeQuery(query);
		return rs;
	}
	
	public String getOneString(String query) throws SQLException {
		ResultSet rs = query(query);
		rs.next();
		return rs.getString(1);
	}
	
	
	
}
