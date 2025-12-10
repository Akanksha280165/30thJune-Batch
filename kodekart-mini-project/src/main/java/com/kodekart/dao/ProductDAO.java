package com.kodekart.dao;

import com.kodekart.model.Product;
import com.kodekart.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

	public List<Product> getAll() {
		List<Product> out = new ArrayList<>();
		String sql = "SELECT id,name,category,price,quantity,description FROM product_info";
		try (Connection c = DBUtil.getConnection();
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				Product p = mapRow(rs);
				out.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	public Product getById(int id) {
		String sql = "SELECT id,name,category,price,quantity,description FROM product_info WHERE id=?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return mapRow(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean add(Product p) {
		String sql = "INSERT INTO product_info (name,category,price,quantity,description) VALUES (?,?,?,?,?)";
		try (Connection c = DBUtil.getConnection();
				PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, p.getName());
			ps.setString(2, p.getCategory());
			ps.setDouble(3, p.getPrice());
			ps.setInt(4, p.getQuantity());
			ps.setString(5, p.getDescription());
			int r = ps.executeUpdate();
			if (r == 1) {
				ResultSet g = ps.getGeneratedKeys();
				if (g.next())
					p.setId(g.getInt(1));
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean update(Product p) {
		String sql = "UPDATE product_info SET name=?,category=?,price=?,quantity=?,description=? WHERE id=?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, p.getName());
			ps.setString(2, p.getCategory());
			ps.setDouble(3, p.getPrice());
			ps.setInt(4, p.getQuantity());
			ps.setString(5, p.getDescription());
			ps.setInt(6, p.getId());
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete(int id) {
		String sql = "DELETE FROM product_info WHERE id=?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Product> search(String keyword) {
		List<Product> out = new ArrayList<>();
		String sql = "SELECT id,name,category,price,quantity,description FROM product_info WHERE name LIKE ? OR category LIKE ?";
		try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
			String like = "%" + keyword + "%";
			ps.setString(1, like);
			ps.setString(2, like);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				out.add(mapRow(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	private Product mapRow(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getInt("id"));
		p.setName(rs.getString("name"));
		p.setCategory(rs.getString("category"));
		p.setPrice(rs.getDouble("price"));
		p.setQuantity(rs.getInt("quantity"));
		p.setDescription(rs.getString("description"));
		return p;
	}
}
