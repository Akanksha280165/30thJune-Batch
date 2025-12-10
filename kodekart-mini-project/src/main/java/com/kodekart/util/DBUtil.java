package com.kodekart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	// Update these values if your DB uses different credentials
	private static final String URL = "jdbc:mysql://localhost:3306/kodekart";
	private static final String USER = "root";
	private static final String PASS = "root";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("MySQL JDBC Driver not found.", ex);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}
}
