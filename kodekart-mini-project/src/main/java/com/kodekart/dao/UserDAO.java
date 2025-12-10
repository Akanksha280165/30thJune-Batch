package com.kodekart.dao;

import com.kodekart.model.User;
import com.kodekart.util.DBUtil;

import java.sql.*;

public class UserDAO {

	public boolean register(User user) {
		String sql = "INSERT INTO users (name,email,phone,password,is_admin) VALUES (?,?,?,?,?)";
		try (Connection c = DBUtil.getConnection();
				PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, user.getName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPhone());
			ps.setString(4, user.getPassword()); // production: hash passwords
			ps.setBoolean(5, user.isAdmin());

			int r = ps.executeUpdate();
			if (r == 1) {
				ResultSet g = ps.getGeneratedKeys();
				if (g.next())
					user.setId(g.getInt(1));
				return true;
			}
		} catch (SQLIntegrityConstraintViolationException ex) {
			System.out.println("Registration failed: email already exists.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public User login(String email, String password) {
		String sql = "SELECT id,name,email,phone,password,is_admin FROM users WHERE email=? AND password=?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User u = new User();
				u.setId(rs.getInt("id"));
				u.setName(rs.getString("name"));
				u.setEmail(rs.getString("email"));
				u.setPhone(rs.getString("phone"));
				u.setPassword(rs.getString("password"));
				u.setAdmin(rs.getBoolean("is_admin"));
				return u;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getById(int id) {
		String sql = "SELECT id,name,email,phone,password,is_admin FROM users WHERE id=?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User u = new User();
				u.setId(id);
				u.setName(rs.getString("name"));
				u.setEmail(rs.getString("email"));
				u.setPhone(rs.getString("phone"));
				u.setPassword(rs.getString("password"));
				u.setAdmin(rs.getBoolean("is_admin"));
				return u;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
