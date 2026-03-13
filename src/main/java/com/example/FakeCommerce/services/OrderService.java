package com.example.FakeCommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.CreateOrderRequestDTO;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.UpdateOrderRequestDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.OrderRespository;
import com.example.FakeCommerce.repositories.OrderproductsRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.schema.Product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRespository orderRespository;
    private final OrderproductsRepository orderproductsRepository;
    private final ProductRepository productRepository;
    private final OrderAdapter orderAdapter;


    public List<GetOrderResponseDto> getAllOrders() {

        List<Order> orders = orderRespository.findAll();
        return orderAdapter.mapToGetOrderResponseDtoList(orders);

    }

    public GetOrderResponseDto getOrderById(Long id) {
        Order order = orderRespository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRespository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRespository.delete(order);
    }


    @Transactional
    public GetOrderResponseDto createOrder(CreateOrderRequestDTO createOrderRequestDTO) {
        Order order = Order.builder()
                        .status(OrderStatus.PENDING)
                        .build();

        orderRespository.save(order);

        if(createOrderRequestDTO.getOrderItems() != null) {
            List<Long> productIds = createOrderRequestDTO.getOrderItems().stream().map(item -> item.getProductId()).collect(Collectors.toList());

            List<Product> products =productRepository.findAllById(productIds);

            Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

            for(Long id : productIds) {
                if(!productMap.containsKey(id)) {
                    throw new ResourceNotFoundException("Product not found with id: " + id);
                }
            }


            List<OrderProducts> orderProducts = new ArrayList<>();

            for(var itemDto : createOrderRequestDTO.getOrderItems()) {
                Product product = productMap.get(itemDto.getProductId());

                orderProducts.add(OrderProducts.builder()
                                                .order(order)
                                                .product(product)
                                                .quantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 1)
                                                .build());
                                                
            }

            orderproductsRepository.saveAll(orderProducts);
        }

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public GetOrderResponseDto updateOrder(Long id, UpdateOrderRequestDto updateOrderRequestDto) {
        Order order = orderRespository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        
        if(updateOrderRequestDto.getStatus() != null) {
            order.setStatus(updateOrderRequestDto.getStatus());
            orderRespository.save(order);
        }

        if(updateOrderRequestDto.getOrderItems() != null) {
            for(var itemDto : updateOrderRequestDto.getOrderItems()) {

                // process each item ---> N+1 queries: TODO
            }
        }

        return orderAdapter.mapToGetOrderResponseDto(order);

    }
}


// User -> Cart -> Adds an item -> New Order (Pending)

// User -> adds more items in the cart -> Same order will be updated

// During checkout -> Order Pending -> Success/Failure