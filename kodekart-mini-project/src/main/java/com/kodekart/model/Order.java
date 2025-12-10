package com.kodekart.model;

import java.util.Date;

public class Order {
	private int id;
	private int userId;
	private Date orderDate;
	private double totalAmount;

	public Order() {
	}

	public Order(int id, int userId, Date orderDate, double totalAmount) {
		this.id = id;
		this.userId = userId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
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

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "Order{" + id + ", user:" + userId + ", date:" + orderDate + ", total:₹" + totalAmount + "}";
	}
}
