package com.example.demo.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody User user){
		try {
			User registeredUser = userService.registerUser(user);
			return ResponseEntity.ok(Map.of("message","User registered successfully","user",registeredUser));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));
		}
	}
	
}
