package com.foorend.api.restaurant.domain;

import com.foorend.api.user.domain.PriceTier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 식당 정보 도메인
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "식당 정보")
public class Restaurant {

    @Schema(description = "식당 ID (PK)")
    private Integer restaurantId;

    @Schema(description = "식당 이름")
    private String restaurantName;

    @Schema(description = "식당 주소")
    private String restaurantAddr;

    @Schema(description = "식당 지역")
    private String locationArea;

    @Schema(description = "평균 가격대")
    private PriceTier avgPriceTier;

    @Schema(description = "음식 카테고리")
    private String category;
}

