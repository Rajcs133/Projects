package com.example.demo.controller;

import java.util.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DonutOrderController {

	private static final int MAX_DONUTS_IN_CART = 50;

	private static final int AVERAGE_TIME_PER_DONUT_SECONDS = 5;

	// Atomic counter to assign unique order IDs
	private final AtomicLong orderCounter = new AtomicLong();

	// Priority queue to store the orders
	private final Queue<DonutOrder> orderQueue = new PriorityQueue<>(
			Comparator.comparing(DonutOrder::getPriority).thenComparing(DonutOrder::getOrderTime));

	// Map to store the orders by client ID for easy cancellation
	private final Map<Long, DonutOrder> ordersByClientId = new HashMap<>();

	// Endpoint to add a new order to the queue
	@PostMapping("/order")
	public ResponseEntity<?> placeOrder(@RequestBody DonutOrderRequest orderRequest) {
		// Check if the client has already placed an order
		if (ordersByClientId.containsKey(orderRequest.getClientId())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client has already placed an order");
		}

		// Calculate the priority of the order based on client type
		int priority = orderRequest.getClientId() < 1000 ? 1 : 2;

		// Create a new DonutOrder object and add it to the queue and map
		DonutOrder newOrder = new DonutOrder(orderCounter.incrementAndGet(), orderRequest.getClientId(),
				orderRequest.getQuantity(), priority);
		orderQueue.add(newOrder);
		ordersByClientId.put(orderRequest.getClientId(), newOrder);

		// Return the order ID to the client
		return ResponseEntity.ok(new OrderIdResponse(newOrder.getId()));
	}

	@GetMapping("/test")
	public ResponseEntity<?> test() {
		return ResponseEntity.ok("The message is success");
	}

	// Endpoint to check the client's queue position and approximate wait time
	@GetMapping("/position/{clientId}")
	public ResponseEntity<?> getQueuePosition(@PathVariable("clientId") Long clientId) {
		int position = 1;
		int waitTime = 0;
		boolean found = false;

		for (DonutOrder order : orderQueue) {
			if (order.getClientId() == clientId) {
				found = true;
				break;
			}
			position++;
			waitTime += order.getQuantity() * AVERAGE_TIME_PER_DONUT_SECONDS;
		}

		if (found) {
			return ResponseEntity.ok(new QueuePositionResponse(position, waitTime));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// Endpoint to retrieve all orders in the queue with their approximate wait time

	@GetMapping("/order")
	public ResponseEntity<?> getQueue() {
		// Calculate the wait time of each order in the queue
		Map<Long, Integer> waitTimesByOrderId = new HashMap<>();
		int position = 0;
		for (DonutOrder order : orderQueue) {
			waitTimesByOrderId.put(order.getId(), position * (order.getQuantity() * AVERAGE_TIME_PER_DONUT_SECONDS));
			position++;
		}

		// Construct a list of OrderStatus objects, which contains the order details
		// along with the wait time
		List<OrderStatus> orderStatuses = new ArrayList<>();
		for (DonutOrder order : orderQueue) {
			int waitTime = waitTimesByOrderId.get(order.getId());
			orderStatuses.add(new OrderStatus(order.getId(), order.getClientId(), order.getQuantity(), waitTime));
		}

		return ResponseEntity.ok(orderStatuses);
	}

	@GetMapping("/nextDelivery")
	public ResponseEntity<?> getNextDelivery() {
		// Check if there are any orders in the queue
		if (orderQueue.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		// Get the orders from the queue and add to the cart until the cart is full or
		// no more orders in the queue
		List<DonutOrder> ordersForPickup = new ArrayList<>();
		int donutCount = 0;

		for (DonutOrder order : orderQueue) {
			if (donutCount + order.getQuantity() <= MAX_DONUTS_IN_CART) {
				ordersForPickup.add(order);
				ordersByClientId.remove(order.getClientId());
				donutCount += order.getQuantity();
			} else {
				break;
			}
		}

		// Remove the orders from the queue
		orderQueue.removeAll(ordersForPickup);

		// Create response
		List<OrderStatus> orderStatusList = new ArrayList<>();
		for (DonutOrder order : ordersForPickup) {
			orderStatusList.add(new OrderStatus(order.getId(), order.getClientId(), order.getQuantity(), 0));
		}

		return ResponseEntity.ok(orderStatusList);
	}

	@DeleteMapping("/order/{clientId}")
	public ResponseEntity<?> cancelOrder(@PathVariable("clientId") long clientId) {
		// Check if the order exists in the queue
		Optional<DonutOrder> optionalOrder = orderQueue.stream().filter(order -> order.getClientId() == clientId)
				.findFirst();
		if (!optionalOrder.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		// Remove the order from the queue
		DonutOrder order = optionalOrder.get();
		orderQueue.remove(order);
		ordersByClientId.remove(order.getClientId());

		// Create response
		OrderStatus orderStatus = new OrderStatus(order.getId(), order.getClientId(), order.getQuantity(), 0);

		return ResponseEntity.ok(orderStatus);
	}

}