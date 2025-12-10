package com.kodekart.dao;

import com.kodekart.model.CartItem;
import com.kodekart.model.Product;
import com.kodekart.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

	public boolean addToCart(int userId, int productId, int quantity) {
		String insert = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?,?,?)";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(insert)) {
			ps.setInt(1, userId);
			ps.setInt(2, productId);
			ps.setInt(3, quantity);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeFromCart(int cartId) {
		String sql = "DELETE FROM cart WHERE id=?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, cartId);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<CartItem> getCartItemsByUser(int userId) {
		List<CartItem> out = new ArrayList<>();
		String sql = "SELECT c.id as cart_id, c.user_id, c.product_id, c.quantity, p.name, p.category, p.price, p.quantity as stock, p.description "
				+ "FROM cart c JOIN product_info p ON c.product_id = p.id WHERE c.user_id=?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CartItem ci = new CartItem();
				ci.setId(rs.getInt("cart_id"));
				ci.setUserId(rs.getInt("user_id"));
				ci.setProductId(rs.getInt("product_id"));
				ci.setQuantity(rs.getInt("quantity"));

				Product p = new Product();
				p.setId(rs.getInt("product_id"));
				p.setName(rs.getString("name"));
				p.setCategory(rs.getString("category"));
				p.setPrice(rs.getDouble("price"));
				p.setQuantity(rs.getInt("stock"));
				p.setDescription(rs.getString("description"));

				ci.setProduct(p);
				out.add(ci);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	public boolean clearCartByUser(int userId, Connection conn) throws SQLException {
		String sql = "DELETE FROM cart WHERE user_id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.executeUpdate();
			return true;
		}
	}
}
