package com.example.demo.controller;

public class DonutOrderRequest {

	private long clientId;
	private int quantity;

	public DonutOrderRequest(long clientId, int quantity) {
		this.clientId = clientId;
		this.quantity = quantity;
	}

	public long getClientId() {
		return clientId;
	}

	public int getQuantity() {
		return quantity;
	}

}
