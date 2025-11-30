package com.foorend.api.restaurant.domain;

import com.foorend.api.user.domain.PriceTier;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 식당 등록/수정 요청
 */
@Schema(description = "식당 등록/수정 요청")
public record RestaurantRequest(

        @NotBlank(message = "식당 이름은 필수입니다.")
        @Schema(description = "식당 이름", example = "맛있는 식당")
        String restaurantName,

        @NotBlank(message = "식당 주소는 필수입니다.")
        @Schema(description = "식당 주소", example = "서울시 강남구 테헤란로 123")
        String restaurantAddr,

        @NotBlank(message = "식당 지역은 필수입니다.")
        @Schema(description = "식당 지역", example = "강남")
        String locationArea,

        @NotNull(message = "가격대는 필수입니다.")
        @Schema(description = "평균 가격대", example = "MID")
        PriceTier avgPriceTier,

        @Schema(description = "음식 카테고리", example = "한식")
        String category
) {}
