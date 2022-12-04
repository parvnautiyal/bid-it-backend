package com.project.bidit.controller;

import com.project.bidit.models.Product;
import com.project.bidit.models.User;
import com.project.bidit.models.dto.ProductDTO;
import com.project.bidit.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@Slf4j
public class UserController {

    @Autowired
    private ProductService productService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> welcomeAdmin(@AuthenticationPrincipal User user) {
        log.info("CONTEXT -> {}", SecurityContextHolder.getContext().toString());
        return ResponseEntity.ok("HELLO ADMIN");
    }

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Product> addProduct(@AuthenticationPrincipal User user, @RequestBody ProductDTO productDTO, @RequestParam String id) {
        Product product = new Product(null, productDTO.getName(), productDTO.getDescription(), productDTO.getImg(), productDTO.getDuration(), productDTO.getStart(), productDTO.getEnd(), productDTO.getAmount(), productDTO.getUser(), null, productDTO.getBids(),null);
        return new ResponseEntity<>(productService.addProduct(product, id), HttpStatus.CREATED);
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> showProducts(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(productService.showProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Product> showProduct(@PathVariable String id) {
        return new ResponseEntity<>(productService.showProduct(id), HttpStatus.OK);
    }

    @PostMapping("/add-bid")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Product> addBid(@RequestBody Map<String, String> json) {
        return new ResponseEntity<>(productService.addBid(json.get("productId"), json.get("userId"), json.get("amount")), HttpStatus.OK);
    }

    @GetMapping("/get-current")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Integer> getCurrentBid(@RequestParam String productId) {
        return new ResponseEntity<>(productService.getCurrentPrice(productId), HttpStatus.OK);
    }

    @GetMapping("/finish")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> finishAuction(@RequestParam String productId) {
        return new ResponseEntity<>(productService.finishAuction(productId), HttpStatus.OK);
    }

}

