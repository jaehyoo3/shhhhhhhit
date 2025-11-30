package com.foorend.api.restaurant.service;

import com.foorend.api.common.domain.BaseGenericListRes;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.restaurant.domain.Restaurant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 식당 Service
 */
@Service
public class RestaurantService {

    private final GenericDAO genericDAO;

    public RestaurantService(@Qualifier("mainDB") GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
    }

    /**
     * 전체 식당 조회
     */
    @SuppressWarnings("unchecked")
    public BaseGenericListRes<Restaurant> findAll() {
        BaseGenericListRes<Restaurant> response = new BaseGenericListRes<>();

        List<Restaurant> restaurants = (List<Restaurant>) genericDAO.selectList("restaurant.findAll");

        response.setSuccessResultData(restaurants);
        return response;
    }
}
