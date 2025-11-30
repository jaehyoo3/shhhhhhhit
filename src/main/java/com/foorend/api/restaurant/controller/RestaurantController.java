package com.foorend.api.restaurant.controller;

import com.foorend.api.common.domain.BaseGenericListRes;
import com.foorend.api.restaurant.domain.Restaurant;
import com.foorend.api.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 식당 API 컨트롤러
 */
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant", description = "식당 API")
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * 전체 식당 조회
     */
    @GetMapping
    @Operation(summary = "전체 식당 조회", description = "등록된 모든 식당을 조회합니다.")
    public BaseGenericListRes<Restaurant> findAll() {
        return restaurantService.findAll();
    }
}
