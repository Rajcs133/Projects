package com.example.demo.controller;

public class DonutOrder {

	private long id;
	private long clientId;
	private int quantity;
	private int priority;
	private long orderTime;

	public DonutOrder(long id, long clientId, int quantity, int priority) {
		this.id = id;
		this.clientId = clientId;
		this.quantity = quantity;
		this.priority = priority;
		this.orderTime = System.currentTimeMillis();
	}

	public long getId() {
		return id;
	}

	public long getClientId() {
		return clientId;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getPriority() {
		return priority;
	}

	public long getOrderTime() {
		return orderTime;
	}

}
