package com.example.demo.controller;

public class QueuePositionResponse {

	private long position;
	private long waitTime;

	public QueuePositionResponse(long position, long waitTime) {
		this.position = position;
		this.waitTime = waitTime;
	}

	public long getPosition() {
		return position;
	}

	public long getWaitTime() {
		return waitTime;
	}

}
