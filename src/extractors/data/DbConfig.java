package extractors.data;

import appwindow.AppWindowScript;

/**
 * Database connection info.
 * 
 * @author Patrick Koch
 */
public class DbConfig {
	
	public static void setCollectionName (String collectionName) {
		DB_NAME = collectionName;
	} 
	
	/**
	 * Host IP Address or Domain that MySQL is running on.
	 * 
	 * E.g. "127.0.0.1" or "localhost"
	 */
	public static final String HOST_NAME = "localhost"; //"192.168.35.37";
	
	/**
	 * Port that MySQL is bound to and is listening on.
	 */
	public static final String PORT      = "3306";
	
	/**
	 * Name of the database to be used during this session
	 */
	public static String DB_NAME = "collection1";//"collection2"; 
	
	/**
	 * Database user name with appropriate permissions. 
	 */
	public static final String USER_NAME = "root";
	
	/**
	 * Case sensitive password for database user
	 */
	public static final String PASSWORD  = "root";
	
}
