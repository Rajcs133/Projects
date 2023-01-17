package com.example.demo.controller;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;

@RestController
@RequestMapping("/Employee")
public class RestDemoController {
	
	@Autowired
	private EmployeeRepository loRepo;
	
	@GetMapping("/getData")
	public ResponseEntity<List<Employee>> getData() {
		List<Employee> loEmp = loRepo.findAll();
		return new ResponseEntity<List<Employee>>(loEmp,HttpStatus.OK);
	}
	
	@PostMapping("/saveEmployee")
	public ResponseEntity<Employee> saveData(@RequestBody Employee emp) {
		
		return new ResponseEntity<>((loRepo.save(emp)),HttpStatus.CREATED);
	}
	
	
	@PutMapping("/updateData/")
	public ResponseEntity<Employee> updateData(@RequestBody Employee emp) {
		
			loRepo.save(emp);
		
		return new ResponseEntity<>((loRepo.save(emp)),HttpStatus.OK);
	} 
	
	@DeleteMapping("/deleteData/{id}")
	public ResponseEntity<HttpStatus> deleteData(@PathVariable String id) {
		
		Optional<Employee> loEmp = loRepo.findByLnEmployeeId(id);
		if(loEmp.isPresent()) {
		loRepo.delete(loEmp.get());
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	
	@PatchMapping("/updateDatawithValues/{id}")
	public ResponseEntity<Employee> updateOnlyWithSpecificValues(@PathVariable String id,
			@RequestBody Map<Object,Object> fields) {
		
		Optional<Employee> loEmp = loRepo.findByLnEmployeeId(id);
		if(loEmp.isPresent()) {
			fields.forEach((key,value) -> {
			Field loField =	ReflectionUtils.findField(Employee.class, (String)key);
			loField.setAccessible(true);
			ReflectionUtils.setField(loField, loEmp.get(), value);
			});
			
		loRepo.save(loEmp.get());
		}
		return new ResponseEntity<>(loEmp.get(),HttpStatus.OK);
	}
	

}
