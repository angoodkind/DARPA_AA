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

import extractors.data.DbConfig;

/**
 * This interface allows one to query ancillary data from the database.
 * It comes supplied with methods to get data that is common between 
 * collections. One can also directly query information by supplying a known
 * userdata MySQL column name. 
 * 
 * data should be queried by supplying both a subj_id and session as subjects'
 * answers to the exit survey could change between sessions.
 * 
 * @author Patrick Koch
 *
 */
public class AncillaryDataInterface {
	
	private String url;
	private String dbName;
	private String userName;
	private String password;
	private Connection conn = null;
	private PreparedStatement stmt = null;
	
	//This query will not work as intended for data that can change between sessions like PC_NO or Loc_NO
	private String query = "SELECT * FROM userdata WHERE subj_id=? AND session=? LIMIT 1";
	//These are the really bad column names that i have to change...
	
	public final static String UD_AGE = "Subj_Id";
	public final static String UD_GENDER = "Gender";
	public final static String UD_HEIGHT = "Height";
	public final static String UD_ETHINICITY = "Ethnicity";
	public final static String UD_FIRST_LANGUAGE = "FirstLanguage";
	public final static String UD_PRIMARY_LANGUAGE = "PrimaryLanguage";
	public final static String UD_COLLEGE_MAJOR = "Major";
	public final static String UD_DOMINANT_HAND = "DominantHand";
	public final static String UD_HOURS_TYPING_PER_DAY = "AvgHoursTyping";
	public final static String UD_MOBILE_KEYBOARD_USE = "UseMobileKeyBoard";
	public final static String UD_FORMAL_TRAINING = "FormalTraining";
	public final static String UD_TRAINING_TYPE = "TrainingType";
	
	
	
	public AncillaryDataInterface() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException  {
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
	private void connect() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		url = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/";
		dbName = DB_NAME;
		userName = USER_NAME;
		password = PASSWORD;
		conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url+dbName,userName,password);
		stmt = conn.prepareStatement(query);
	}
	
	/**
	 * Returns ancillary user data from the database.
	 * Use the static constants defined in this class as the dataField parameter
	 * or supply a known MySQL column name.
	 * subj_id of the current user can be found in the DataNode object. 
	 * 
	 * This method is deprecated but is left in for compatibility 
	 * 
	 * @param subj_id 
	 * @param dataField an integer value. Use the static constants assigned in this class.
	 * @return data from the database as a raw Java Object.
	 * @throws SQLException
	 */
	public Object getData(int subj_id, String dataField) throws SQLException {
		stmt.setInt(1,subj_id);
		stmt.setInt(2,1);
		return query(dataField);
	}
	
	/**
	 * Returns ancillary user data from the database.
	 * Use the static constants defined in this class as the dataField parameter
	 * or supply a known MySQL column name.
	 * subj_id of the current user can be found in the DataNode object.
	 * session number is found in an answer object.  
	 * 
	 * @param subj_id int
	 * @param sesion int
	 * @param dataField an integer value. Use the static constants assigned in this class.
	 * @return data from the database as a raw Java Object.
	 * @throws SQLException
	 */
	public String getAncillaryData(int subj_id, int session, String dataField) throws SQLException {
		stmt.setInt(1,subj_id);
		stmt.setInt(2,session);
		return query(dataField);
	}
	
	public int getAge(int subj_id, int session) throws SQLException {
		return Integer.parseInt(getAncillaryData(subj_id, session, UD_AGE).toString());
	}
	
	public String getGender(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_GENDER);
	}
	
	public double getHeight(int subj_id, int session) throws SQLException {
		return Double.parseDouble(getAncillaryData(subj_id, session, UD_HEIGHT));
	}
	
	public String getEthinicity(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_ETHINICITY);
	}
	
	public String getFirstLanguage(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_FIRST_LANGUAGE);
	}
	
	public String getPrimaryLanguage(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_PRIMARY_LANGUAGE);
	}
	
	public String getMajor(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_COLLEGE_MAJOR);
	}
	
	public String getDominantHand(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_DOMINANT_HAND);
	}
	
	public String getAverageHoursTyping(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_HOURS_TYPING_PER_DAY);
	}
	
	public String getMobileKeyboardUse(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_MOBILE_KEYBOARD_USE);
	}
	
	public String getFormalTraining(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_FORMAL_TRAINING);
	}
	
	public String getTrainingType(int subj_id, int session) throws SQLException {
		return getAncillaryData(subj_id, session, UD_TRAINING_TYPE);
	}
	
	/**
	 * Runs stored query on the database. Stores the result into private variable dataList.
	 * 
	 * @param query
	 * @throws SQLException
	 */
	private String query(String column) throws SQLException {
		ResultSet rs = stmt.executeQuery();
		Object val = null;
		rs.first();
		if (rs.getRow() == 1)
			val = rs.getObject(column);
		rs.close();
		return val.toString();
	}
	
	
	
}
