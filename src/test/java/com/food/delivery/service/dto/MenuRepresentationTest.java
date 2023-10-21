package com.food.delivery.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MenuRepresentationTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MenuRepresentation.class);
        MenuRepresentation menuRepresentation1 = new MenuRepresentation();
        menuRepresentation1.setId(1L);
        MenuRepresentation menuRepresentation2 = new MenuRepresentation();
        assertThat(menuRepresentation1).isNotEqualTo(menuRepresentation2);
        menuRepresentation2.setId(menuRepresentation1.getId());
        assertThat(menuRepresentation1).isEqualTo(menuRepresentation2);
        menuRepresentation2.setId(2L);
        assertThat(menuRepresentation1).isNotEqualTo(menuRepresentation2);
        menuRepresentation1.setId(null);
        assertThat(menuRepresentation1).isNotEqualTo(menuRepresentation2);
    }
}
