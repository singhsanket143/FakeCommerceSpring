package com.example.FakeCommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FakeCommerce.schema.OrderProducts;


public interface OrderproductsRepository extends JpaRepository<OrderProducts, Long> {
    
    List<OrderProducts> findByOrderId(Long orderId);
}
