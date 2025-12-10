package com.kodekart.model;

public class CartItem {
	private int id;
	private int userId;
	private int productId;
	private int quantity;
	private Product product; // optional for joined view

	public CartItem() {
	}

	public CartItem(int id, int userId, int productId, int quantity) {
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public String toString() {
		String pInfo = (product != null) ? (product.getName() + " ₹" + product.getPrice()) : ("productId:" + productId);
		return "CartItem{" + id + ", user:" + userId + ", " + pInfo + ", qty:" + quantity + "}";
	}
}
