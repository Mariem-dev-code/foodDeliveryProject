package com.food.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IngredientModelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IngredientModel.class);
        IngredientModel ingredientModel1 = new IngredientModel();
        ingredientModel1.setId(1L);
        IngredientModel ingredientModel2 = new IngredientModel();
        ingredientModel2.setId(ingredientModel1.getId());
        assertThat(ingredientModel1).isEqualTo(ingredientModel2);
        ingredientModel2.setId(2L);
        assertThat(ingredientModel1).isNotEqualTo(ingredientModel2);
        ingredientModel1.setId(null);
        assertThat(ingredientModel1).isNotEqualTo(ingredientModel2);
    }
}
