package com.kodekart.dao;

import com.kodekart.model.Order;
import com.kodekart.model.OrderItem;
import com.kodekart.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO {

	public int createOrder(Connection conn, int userId, double totalAmount) throws SQLException {
		String sql = "INSERT INTO orders (user_id, total_amount) VALUES (?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, userId);
			ps.setDouble(2, totalAmount);
			ps.executeUpdate();
			ResultSet g = ps.getGeneratedKeys();
			if (g.next())
				return g.getInt(1);
		}
		throw new SQLException("Failed to create order");
	}

	public void insertOrderItems(Connection conn, List<OrderItem> items) throws SQLException {
		String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (OrderItem it : items) {
				ps.setInt(1, it.getOrderId());
				ps.setInt(2, it.getProductId());
				ps.setInt(3, it.getQuantity());
				ps.setDouble(4, it.getPrice());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	// View orders for a user
	public List<Order> getOrdersByUser(int userId) {
		List<Order> out = new ArrayList<>();
		String sql = "SELECT id,user_id,order_date,total_amount FROM orders WHERE user_id=? ORDER BY order_date DESC";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Order o = new Order();
				o.setId(rs.getInt("id"));
				o.setUserId(rs.getInt("user_id"));
				o.setOrderDate(new Date(rs.getTimestamp("order_date").getTime()));
				o.setTotalAmount(rs.getDouble("total_amount"));
				out.add(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	// Admin: get all orders
	public List<Order> getAllOrders() {
		List<Order> out = new ArrayList<>();
		String sql = "SELECT id,user_id,order_date,total_amount FROM orders ORDER BY order_date DESC";
		try (Connection c = DBUtil.getConnection();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				Order o = new Order();
				o.setId(rs.getInt("id"));
				o.setUserId(rs.getInt("user_id"));
				o.setOrderDate(new Date(rs.getTimestamp("order_date").getTime()));
				o.setTotalAmount(rs.getDouble("total_amount"));
				out.add(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}
}
