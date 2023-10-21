package com.food.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantModelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantModel.class);
        RestaurantModel restaurantModel1 = new RestaurantModel();
        restaurantModel1.setId(1L);
        RestaurantModel restaurantModel2 = new RestaurantModel();
        restaurantModel2.setId(restaurantModel1.getId());
        assertThat(restaurantModel1).isEqualTo(restaurantModel2);
        restaurantModel2.setId(2L);
        assertThat(restaurantModel1).isNotEqualTo(restaurantModel2);
        restaurantModel1.setId(null);
        assertThat(restaurantModel1).isNotEqualTo(restaurantModel2);
    }
}
