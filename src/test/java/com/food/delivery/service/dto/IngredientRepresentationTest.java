package com.food.delivery.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IngredientRepresentationTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IngredientRepresentation.class);
        IngredientRepresentation ingredientRepresentation1 = new IngredientRepresentation();
        ingredientRepresentation1.setId(1L);
        IngredientRepresentation ingredientRepresentation2 = new IngredientRepresentation();
        assertThat(ingredientRepresentation1).isNotEqualTo(ingredientRepresentation2);
        ingredientRepresentation2.setId(ingredientRepresentation1.getId());
        assertThat(ingredientRepresentation1).isEqualTo(ingredientRepresentation2);
        ingredientRepresentation2.setId(2L);
        assertThat(ingredientRepresentation1).isNotEqualTo(ingredientRepresentation2);
        ingredientRepresentation1.setId(null);
        assertThat(ingredientRepresentation1).isNotEqualTo(ingredientRepresentation2);
    }
}
