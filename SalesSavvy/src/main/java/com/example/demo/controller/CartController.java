package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/items/counts")
    public ResponseEntity<Integer> getCartItemCount(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        int count = cartService.getCartItemCount(user.getUserId());
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/items") 
    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
    	User user = (User) request.getAttribute("authenticatedUser");
    	
    	Map<String, Object> cartItems = cartService.getCartItems(user.getUserId());
    	return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        int productId = (int) request.get("productId");

        // Handle quantity: Default to 1 if not provided
        int quantity = request.containsKey("quantity") ? (int) request.get("quantity") : 1;

        // Fetch the user using username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        // Add the product to the cart
        cartService.addToCart(user.getUserId(), productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
        
        @PutMapping("/update")
        public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request) {
        	String username = (String) request.get("username");
        	int productId = (int) request.get("productId");
        	int quantity = (int) request.get("quantity");
        	
        	//Fetch the user using username
        	User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("user not found with username: "+username));
        	
        	//update the cart item quantity
        	cartService.updateCartItemQuantity(user.getUserId(), productId, quantity);
        	return ResponseEntity.status(HttpStatus.OK).build();
        
    }
        
        
        @DeleteMapping("/delete")
        public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request) {
        	String username = (String) request.get("username");
        	int productId = (int) request.get("productId");
        	
        	//Fetch the user using username
        	User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("user not found with username: "+username));
        	
        	//delete the cart item 
        	cartService.deleteCartItem(user.getUserId(), productId);
        	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        
    }
}