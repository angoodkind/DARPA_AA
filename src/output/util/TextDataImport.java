package output.util;

import static extractors.data.DbConfig.HOST_NAME;
import static extractors.data.DbConfig.PASSWORD;
import static extractors.data.DbConfig.PORT;
import static extractors.data.DbConfig.USER_NAME;
import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;

public class TextDataImport {

	public Path currentPath;
	public TreeSet<User> users; 
	public StringBuilder sqlData;
	public int dataId;
	public HashMap<String,Data> data = new HashMap<String,Data>(); 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TextDataImport di = new TextDataImport();
		String dataDir = "C:\\users\\Patrick\\Desktop\\Phase 7 Keystroke Data\\";
		di.currentPath = Paths.get(dataDir);
		di.users = new TreeSet<User>();
		di.sqlData = new StringBuilder();
		
		try {
			di.readUsers();
			di.ImportDbSubjectIds("cpbehavsec1");
			
//			System.out.printf("%s", di.users);
			LinkedList<User> userList = new LinkedList<User>(di.users);
			Collections.sort(userList, new Comparator<User>() {

				@Override
				public int compare(User o1, User o2) {
					if (o1.subjectId == o2.subjectId)
						return 0;
					else if (o1.subjectId > o2.subjectId)
						return 1;
					else
						return -1;
				}
				
			});
			System.out.printf("%s", userList);
			di.ExportSubjectIdsToDb(userList, "collection2");
			di.dataId = 20000;
			di.readFiles();
			di.generateSQL();
			System.out.println("Done!");
		} catch (InvalidPathException | IOException | InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		
	}

	//Read user info from data
	public void readUsers() throws IOException, InvalidPathException {
		UserWalker uw = new UserWalker();
		Files.walkFileTree(currentPath, uw);
	}
	
	//Read user info from data
	public void readFiles() throws IOException, InvalidPathException {
		DataWalker dw = new DataWalker();
		Files.walkFileTree(currentPath, dw);
	}
	
	//Import old subjectIds for return users and assign Ids to new users.  
	public void ImportDbSubjectIds(String db) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String url = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/";
		String dbName = db;
		String userName = USER_NAME;
		String password = PASSWORD;
		Connection conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url+dbName,userName,password);
		System.out.println("Connected to the database");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users;");
		while (rs.next()) {
			User dbUser = new User(rs.getString("User").toLowerCase(), rs.getString("Cwid").toLowerCase(), rs.getInt("Id"));

			if (users.contains(dbUser)) {
				users.remove(dbUser);
				users.add(dbUser);
			}
		}
		rs = stmt.executeQuery("SELECT COUNT(*) FROM users;");
		rs.next();
		int numDbSubjects = rs.getInt(1);
		for (User u : users) {
			if (u.subjectId == 0) {
				u.subjectId = ++numDbSubjects;
			}
				
		}
		stmt.close();
		rs.close();
		conn.close();	
	}
	
	//Export subjectId mapping to the users table
	public void ExportSubjectIdsToDb(Collection<User> users, String db) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String url = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/";
		String dbName = db;
		String userName = USER_NAME;
		String password = PASSWORD;
		Connection conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url+dbName,userName,password);
		System.out.println("Connected to the database");
		Statement stmt = conn.createStatement();
		for (User u : users) {
			String sql = String.format("INSERT INTO users VALUE (%s, %s, %s, '%s', '%s')", u.subjectId, 0, 0, u.cwid, u.name);
			System.out.println(sql);
			stmt.executeUpdate(sql);
		}
		stmt.close();
		conn.close();
	}
	
	//Walks data files and extracts user information
	private class UserWalker extends SimpleFileVisitor<Path> {
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
			file = currentPath.relativize(file);
			users.add(extractUserInformation(file));
			return CONTINUE;
		}
	}
	
	//Walks data files and extracts data information
	private class DataWalker extends SimpleFileVisitor<Path> {
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
				
				file = currentPath.relativize(file);
				if (file.getNameCount() < 9)
					return CONTINUE;
				String stripName = file.getFileName().toString().replaceAll("(_AllKeys\\.txt)?(_Keystrokes\\.txt)?(_Final\\.txt)?", "");
				String hash = file.subpath(0, file.getNameCount() - 1).toString() + stripName;
				if (!data.keySet().contains(hash))
					data.put(hash, new Data());
				try {
					String content = new Scanner(currentPath.resolve(file)).useDelimiter("\\Z").next();
					content = content.replace("'", "\\'");
					if (file.getFileName().toString().matches(".*AllKeys\\.txt"))
						data.get(hash).AllKeys = content;
					if (file.getFileName().toString().matches(".*Keystrokes\\.txt"))
						data.get(hash).Keystrokes = content;
					if (file.getFileName().toString().matches(".*Final\\.txt"))
						data.get(hash).Final = content;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			return CONTINUE;
		}
	}
	
	//Generates SQL and updates data table.
	private void generateSQL() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String url = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/";
		String dbName = "collection2";
		String userName = USER_NAME;
		String password = PASSWORD;
		Connection conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url+dbName,userName,password);
		System.out.println("Connected to the database");
		Statement stmt = conn.createStatement();
		
		TreeSet<String> sortSet = new TreeSet<String>(data.keySet());
		for (String s : sortSet) {
			Path path = Paths.get(s);
			User dataUser = extractUserInformation(path);
			if (users.contains(dataUser))
				for (User u : users) {
					if (u.equals(dataUser))
						dataUser.subjectId = u.subjectId;
				}
			else
				System.err.println("OHNO");
			char session = path.getName(5).toString().charAt(7);
			String date = path.getName(6).toString().substring(7);
			String oId = path.getName(7).toString().split("_")[1];
			String qId = path.getName(7).toString().split("_")[0].substring(9);
			String type = path.getName(7).toString().split("_")[2];
			String sql = String.format("(%s,%s,%s,%s,%s,'%s','%s','%s','%s','%s')", dataId, dataUser.subjectId, session, oId, qId, type, date, data.get(s).Final, data.get(s).AllKeys, data.get(s).Keystrokes);
			stmt.executeUpdate("Insert into data VAlUES " + sql);
			dataId++;
		}
		stmt.close();
		conn.close();
	}
	
//	TODO extract only the Path elements required by data rows.
//	These are elements 1,2,4,5,6,8
	public static String[] dirTreeToStringArray(Path path){
		int numElements = path.getNameCount();
		String[] output = new String[numElements];
		for (int i = 0; i < numElements; i++)
			output[i] = path.getName(i).toString();
		return output;
	}
	
	//Extracts a User from Path information
	public User extractUserInformation(Path path){
		String[] info = path.getName(4).toString().split("_");
		return new User(info[0].toLowerCase(), info[1].toLowerCase(), 0);
	}

	//User class used to hold user mapping data for entry in users table.
	private class User implements Comparable<User>{
		
		String name;
		String cwid;
		int subjectId;
		
		public User (String name, String cwid, int subjectId) {
			this.cwid = cwid;
			this.name = name;
			this.subjectId = subjectId;
		}
		
		public boolean equals(User user) {
			if (this.cwid.equals(user.cwid) && this.name.equals(user.name))
				return true;
			else
				return false;
		}

		@Override
		public int compareTo(User o) {
			if (this.equals(o))
				return 0;
			else if (this.name.compareTo(o.name) != 0)
				return this.name.compareTo(o.name);
			else
				return this.cwid.compareTo(o.cwid);
		}
		
		@Override
		public String toString() {
			return String.format("%s %s %s\n", this.name, this.cwid, this.subjectId);
		}
		
	}
	
	//Interal class representing a data row.
	private class Data {
		String Final;
		String AllKeys;
		String Keystrokes;
		
		public String toString() {
			if (Final.length() > 0 && AllKeys.length() > 0 && Keystrokes.length() > 0)
				return "COOL";
			return "NOT COOL";
		}
	}
	 
}
