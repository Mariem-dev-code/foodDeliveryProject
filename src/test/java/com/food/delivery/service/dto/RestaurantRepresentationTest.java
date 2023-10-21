package com.food.delivery.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantRepresentationTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantRepresentation.class);
        RestaurantRepresentation restaurantRepresentation1 = new RestaurantRepresentation();
        restaurantRepresentation1.setId(1L);
        RestaurantRepresentation restaurantRepresentation2 = new RestaurantRepresentation();
        assertThat(restaurantRepresentation1).isNotEqualTo(restaurantRepresentation2);
        restaurantRepresentation2.setId(restaurantRepresentation1.getId());
        assertThat(restaurantRepresentation1).isEqualTo(restaurantRepresentation2);
        restaurantRepresentation2.setId(2L);
        assertThat(restaurantRepresentation1).isNotEqualTo(restaurantRepresentation2);
        restaurantRepresentation1.setId(null);
        assertThat(restaurantRepresentation1).isNotEqualTo(restaurantRepresentation2);
    }
}
