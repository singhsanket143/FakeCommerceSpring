package com.example.FakeCommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;

public interface OrderRespository extends JpaRepository<Order, Long> {


    
}
