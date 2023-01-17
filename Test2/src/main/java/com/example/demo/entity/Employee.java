package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table
@Entity
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String lnEmployeeId;
	private String lnEmployeeName;
	private String lnEmployeeDesignation;
	private String lnEmplyeeAddress;
	

}
