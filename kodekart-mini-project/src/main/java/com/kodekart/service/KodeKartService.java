package com.kodekart.service;

import com.kodekart.dao.CartDAO;
import com.kodekart.dao.OrderDAO;
import com.kodekart.dao.ProductDAO;
import com.kodekart.model.CartItem;
import com.kodekart.model.OrderItem;
import com.kodekart.model.Product;
import com.kodekart.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KodeKartService {
	private ProductDAO productDAO = new ProductDAO();
	private CartDAO cartDAO = new CartDAO();
	private OrderDAO orderDAO = new OrderDAO();

	public boolean placeOrder(int userId) {
		Connection conn = null;
		try {
			
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false);

			List<CartItem> cart = cartDAO.getCartItemsByUser(userId);
			if (cart.isEmpty()) {
				System.out.println("Cart is empty. Nothing to place.");
				conn.rollback();
				return false;
			}

			
			double total = 0.0;
			for (CartItem ci : cart) {
				Product p = ci.getProduct();
				if (p.getQuantity() < ci.getQuantity()) {
					System.out.println("Insufficient stock for: " + p.getName());
					conn.rollback();
					return false;
				}
				total += p.getPrice() * ci.getQuantity();
			}

			
			int orderId = orderDAO.createOrder(conn, userId, total);

			
			List<OrderItem> items = new ArrayList<>();
			String updateStockSql = "UPDATE product_info SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
			try (PreparedStatement psUpd = conn.prepareStatement(updateStockSql)) {
				for (CartItem ci : cart) {
					OrderItem oi = new OrderItem();
					oi.setOrderId(orderId);
					oi.setProductId(ci.getProductId());
					oi.setQuantity(ci.getQuantity());
					oi.setPrice(ci.getProduct().getPrice());
					items.add(oi);

					psUpd.setInt(1, ci.getQuantity());
					psUpd.setInt(2, ci.getProductId());
					psUpd.setInt(3, ci.getQuantity());
					psUpd.addBatch();
				}
				int[] updCounts = psUpd.executeBatch();
				for (int c : updCounts) {
					if (c == 0) {
						System.out.println("Stock update failed for one or more products.");
						conn.rollback();
						return false;
					}
				}
			}

			
			orderDAO.insertOrderItems(conn, items);

			
			cartDAO.clearCartByUser(userId, conn);

			conn.commit();
			System.out.println("Order placed successfully. Order ID: " + orderId + " Total: ₹" + total);
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				if (conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}
			} catch (SQLException ignored) {
			}
		}
	}

	
	public List<Product> listAllProducts() {
		return productDAO.getAll();
	}

	public Product getProductById(int id) {
		return productDAO.getById(id);
	}

	public boolean addProduct(Product p) {
		return productDAO.add(p);
	}

	public boolean updateProduct(Product p) {
		return productDAO.update(p);
	}

	public boolean deleteProduct(int id) {
		return productDAO.delete(id);
	}

	public List<Product> searchProducts(String q) {
		return productDAO.search(q);
	}

	public boolean addToCart(int userId, int productId, int qty) {
		// quick stock check
		Product p = productDAO.getById(productId);
		if (p == null) {
			System.out.println("Product not found.");
			return false;
		}
		if (p.getQuantity() < qty) {
			System.out.println("Not enough stock. Available: " + p.getQuantity());
			return false;
		}
		return cartDAO.addToCart(userId, productId, qty);
	}

	public List<com.kodekart.model.CartItem> viewCart(int userId) {
		return cartDAO.getCartItemsByUser(userId);
	}
}
