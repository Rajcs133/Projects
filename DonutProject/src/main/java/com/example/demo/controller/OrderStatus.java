package com.example.demo.controller;

public class OrderStatus {
	private Long id;
	private long quantity;
	private long clientid;
	private Integer waitTime;

	public OrderStatus(Long id, Long clientId, long quantity, Integer waitTime) {
		this.id = id;
		this.quantity = quantity;
		this.setClientid(clientId);
		this.waitTime = waitTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(Integer waitTime) {
		this.waitTime = waitTime;
	}

	public long getClientid() {
		return clientid;
	}

	public void setClientid(long clientid) {
		this.clientid = clientid;
	}
}