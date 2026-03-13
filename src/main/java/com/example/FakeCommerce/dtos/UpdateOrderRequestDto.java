package com.example.FakeCommerce.dtos;

import java.util.List;

import com.example.FakeCommerce.schema.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderRequestDto {

    private OrderStatus status;

    private List<OrderItemActionDto> orderItems;


    
}
