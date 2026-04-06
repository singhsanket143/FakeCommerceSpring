package com.example.FakeCommerce.services.cache;

import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRedisCache {

    private static final String KEY_SUMMARY = "product:summary:";
    
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<GetProductResponseDto> getSummary(Long id) {
        String responseJson = stringRedisTemplate.opsForValue().get(KEY_SUMMARY + id);

        if (responseJson == null) return Optional.empty(); // cache miss

        // cache hit
        try {
            GetProductResponseDto response = objectMapper.readValue(responseJson, GetProductResponseDto.class);
            return Optional.of(response);
        } catch (Exception e) {
            log.error("Error parsing product summary from cache: {}", e.getMessage());
            stringRedisTemplate.delete(KEY_SUMMARY + id); // because the data is corrupted
            return Optional.empty();
        }
    }

    private void putSummary(Long id, GetProductResponseDto response) {
        try {
            stringRedisTemplate.opsForValue().set(KEY_SUMMARY + id, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            throw new RuntimeException("Error serializing product summary to cache: " + e.getMessage());
        }
    }
}
