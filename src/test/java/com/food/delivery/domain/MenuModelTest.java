package com.food.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuModelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuModel.class);
        MenuModel menuModel1 = new MenuModel();
        menuModel1.setId(1L);
        MenuModel menuModel2 = new MenuModel();
        menuModel2.setId(menuModel1.getId());
        assertThat(menuModel1).isEqualTo(menuModel2);
        menuModel2.setId(2L);
        assertThat(menuModel1).isNotEqualTo(menuModel2);
        menuModel1.setId(null);
        assertThat(menuModel1).isNotEqualTo(menuModel2);
    }
}
