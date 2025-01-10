package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;

    //Get cart items for a user
    public Map<String, Object> getCartItems(int userId) {
    //Fetch the cart items for the user with product details
    	List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(userId);
    	
    	//Creating a response map to hold the cart details
    	Map<String,Object> response = new HashMap<>();
    	 User user = userRepository.findById(userId)
                 .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    	 
    	 response.put("username", user.getUsername());
    	 response.put("role", user.getRole().toString());
    	 
    	 //List to hold the product details
    	 List<Map<String,Object>> products = new ArrayList<>();
    	 int overallTotalPrice = 0;
    	 
    	 for(CartItem cartItem : cartItems) {
    		 Map<String,Object> productDetails = new HashMap<>();
    		 
    		 //Get product details
    		 Product product = cartItem.getProduct();
    		 
    		 //Fetch product images
    		 List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());
    		 String imageUrl = (productImages != null && !productImages.isEmpty()) ? productImages.get(0).getImageUrl():"default-image-url";
    		 
    		 //Populate product details
    		 productDetails.put("product_id", product.getProductId());
    		 productDetails.put("image_url", imageUrl);
    		 productDetails.put("name", product.getName());
    		 productDetails.put("description", product.getDescription());
    		 productDetails.put("price_per_unit", product.getPrice());
    		 productDetails.put("quantity", cartItem.getQuantity());
    		 productDetails.put("total_price", cartItem.getQuantity()* product.getPrice().doubleValue());

    		 
    		 //Add tp products list
    		 products.add(productDetails);
    		 
    		 //Update overall total price
    		 overallTotalPrice += cartItem.getQuantity() * product.getPrice().doubleValue();
    		 
    		 
    	 }
    	 
    	 //Prepare the final cart response
    	 Map<String, Object> cart = new HashMap<>();
    	 cart.put("products", products);
    	 cart.put("overall_total_price", overallTotalPrice);
    	 
    	 response.put("cart", cart);
    	 return response;
    }
    
    
    // Get the total cart item count for a user
    public int getCartItemCount(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return cartRepository.countTotalItems(userId);
    }

    // Add an item to the cart
    public void addToCart(int userId, int productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartRepository.save(cartItem);
        } else {
            CartItem newItem = new CartItem(user, product, quantity);
            cartRepository.save(newItem);
        }
    }
    
    public void updateCartItemQuantity(int userId, int productId, int quantity) {
    	User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user not found"));
    	Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("product not found"));
    	
    	//Fetch cart item for this userId and productId
    	Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);
    	
    	if(existingItem.isPresent()) { 
    		CartItem cartItem = existingItem.get();
    		if(quantity == 0) {
    			deleteCartItem(userId, productId);
    			
    		} else  {
    			cartItem.setQuantity(quantity);
    			cartRepository.save(cartItem);
    		}
    			
    	}
    }
    	public void deleteCartItem(int userId, int productId) {
        	User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("user not found"));
        	Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("product not found"));
        	
    	
    	cartRepository.deleteCartItem(userId,productId);
    	}
 
}