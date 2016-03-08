package output.util;

import static extractors.data.DbConfig.HOST_NAME;
import static extractors.data.DbConfig.PASSWORD;
import static extractors.data.DbConfig.PORT;
import static extractors.data.DbConfig.USER_NAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

public class TestData {

	public static void main(String[] args) {
	
		try {
			Connection conn1 = connect("collection2");
			Connection conn2 = connect("collection2");
			PreparedStatement byname = conn1.prepareStatement("Select distinct Subj_id from users where name=?");
			PreparedStatement bycwid = conn2.prepareStatement("Select distinct Subj_id from users where cwid=?");
			Scanner scanner = new Scanner(new File("C:\\Users\\Patrick\\Desktop\\Phase 7 Ancillary Data.csv"));
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String name = line.split(",")[7].trim();
				String cwid = line.split(",")[8].trim();
				byname.setString(1, name);
				bycwid.setString(1, cwid);
				ResultSet bynameResult = byname.executeQuery();
				ResultSet bycwidResult = bycwid.executeQuery();
				String nameSubj,cwidSubj;
				if (bynameResult.next()) {
					nameSubj = bynameResult.getString(1).trim();
				} else {
					nameSubj = "";
				}
				if (bycwidResult.next()) {
					cwidSubj = bycwidResult.getString(1).trim();
				} else {
					cwidSubj = "";
				}
				System.out.printf("%s,%s\n",nameSubj,cwidSubj);
			}
			conn1.close();
			conn2.close();
			
			Connection conn = connect("collection2");
			ResultSet rs = conn.createStatement().executeQuery("Select name, cwid, subj_id from users");
			LinkedList<String> dbcwids = new LinkedList<String>();
			LinkedList<String> dbnames = new LinkedList<String>();
			LinkedList<String> dbsubjs = new LinkedList<String>();
			while (rs.next()) {
				dbnames.add(rs.getString(1).trim());
				dbcwids.add(rs.getString(2).trim());
				dbsubjs.add(rs.getString(3).trim());
			}
			
			
			LinkedList<String> escwids = new LinkedList<String>();
			LinkedList<String> esnames = new LinkedList<String>();
			LinkedList<String> essubjs = new LinkedList<String>();
			scanner = new Scanner(new File("C:\\Users\\Patrick\\Desktop\\Phase 7 Ancillary Data.csv"));
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				escwids.add(line.split(",")[8].trim());
				esnames.add(line.split(",")[7].trim());
				essubjs.add(line.split(",")[6].trim());
			}
			
			for (String name : dbnames) 
				if (!esnames.contains(name))
					System.out.printf("%s\n", name);
			for (String cwid : dbcwids) 
				if (!escwids.contains(cwid))
					System.out.printf("%s\n", cwid);
			for (String subj : dbsubjs) {
				if (subj.isEmpty())
					continue;
				if (!essubjs.contains(subj))
					System.out.printf("%s\n", subj);
			}
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public class User {
		
		String name;
		String cwid;
		String subj_Id;
		
		public User (String name, String cwid, String subjectId) {
			this.cwid = cwid.trim();
			this.name = name.trim();
			this.subj_Id = subjectId.trim();
		}
		
//		public boolean equals(User user) {
//			if (this.cwid.equals(user.cwid) && this.name.equals(user.name))
//				return true;
//			else
//				return false;
//		}
	}
	
	public static Connection connect(String db) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String url = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/";
		String dbName = db;
		String userName = USER_NAME;
		String password = PASSWORD;
		Connection conn = null;
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url+dbName,userName,password);
		System.out.println("Connected to the database");
		return conn;
	}
}
