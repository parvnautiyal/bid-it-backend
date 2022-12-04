package com.project.bidit.service;

import com.project.bidit.models.PartialUser;
import com.project.bidit.models.Product;
import com.project.bidit.models.Status;
import com.project.bidit.models.User;
import com.project.bidit.repository.ProductRepository;
import com.project.bidit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Product addProduct(Product product, String id) {
        product.setStart(LocalDateTime.now());
        product.setEnd(endTimeCalculator(product.getStart(), product.getDuration()));
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            PartialUser partialUser = PartialUser.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .contact(user.getContact())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
            product.setUser(partialUser);
            product.setBids(new HashMap<>());
            product.setStatus(Status.ACTIVE);
            return productRepository.save(product);
        }
        throw new RuntimeException("Exception!");
    }

    public List<Product> showProducts() {
        return productRepository.findAllByStatus("ACTIVE");
    }

    public Product showProduct(String id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent())
            return productOptional.get();
        throw new RuntimeException("NO PRODUCT");
    }

    public LocalDateTime endTimeCalculator(LocalDateTime start, String duration) {
        if (duration.contains("M")) {
            duration = duration.replace("M", "");
            return start.plusMinutes(Long.parseLong(duration));
        } else {
            duration = duration.replace("H", "");
            return start.plusHours(Long.parseLong(duration));
        }
    }

    public Product addBid(String productId, String userId, String amount) {
        this.productRepository.findById(productId).ifPresent(product -> {
            Map<String, Map<String, String>> map = product.getBids();
            String now = LocalDateTime.now().toString();
            Map<String, String> amountMap = new HashMap<>();
            amountMap.put("timestamp", now);
            amountMap.put("amount", amount);
            map.put(userId, amountMap);
            product.setBids(map);
            this.productRepository.save(product);
        });
        return showProduct(productId);
    }

    public Integer getCurrentPrice(String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Integer currentPrice = product.getAmount();
            int val = 0;
            if(product.getBids().size()!=0){
                List<String> value = product.getBids().entrySet().stream()
                        .sorted((i1, i2) -> Integer.compare(Integer.parseInt(i2.getValue().get("amount")), Integer.parseInt(i1.getValue().get("amount"))))
                        .limit(1)
                        .map(stringMapEntry -> stringMapEntry.getValue().get("amount"))
                        .toList();
                val = Integer.parseInt(value.get(0));
            }
            if (val > currentPrice)
                currentPrice = val;
            return currentPrice;
        }
        return 0;
    }

    public String finishAuction(String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Map<String, Map<String, String>> bids = product.getBids();
            String userId = null;
            if (product.getBids().size() != 0) {
                userId = bids.entrySet().stream()
                        .sorted((i1, i2) -> {
                            LocalDateTime t1 = LocalDateTime.parse(i1.getValue().get("timestamp"));
                            LocalDateTime t2 = LocalDateTime.parse(i2.getValue().get("timestamp"));
                            return t2.compareTo(t1);
                        })
                        .limit(1)
                        .map(Map.Entry::getKey)
                        .toList().get(0);
                product.setStatus(Status.SOLD);
                userRepository.findById(userId).ifPresent(product::setBuyer);
                productRepository.save(product);
            } else product.setStatus(Status.EXPIRED);
            return userId;
        } else {
            throw new RuntimeException("INVALID");
        }
    }
}
