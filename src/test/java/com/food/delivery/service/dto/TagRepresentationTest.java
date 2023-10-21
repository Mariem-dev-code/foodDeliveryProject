package com.food.delivery.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TagRepresentationTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagRepresentation.class);
        TagRepresentation tagRepresentation1 = new TagRepresentation();
        tagRepresentation1.setId(1L);
        TagRepresentation tagRepresentation2 = new TagRepresentation();
        assertThat(tagRepresentation1).isNotEqualTo(tagRepresentation2);
        tagRepresentation2.setId(tagRepresentation1.getId());
        assertThat(tagRepresentation1).isEqualTo(tagRepresentation2);
        tagRepresentation2.setId(2L);
        assertThat(tagRepresentation1).isNotEqualTo(tagRepresentation2);
        tagRepresentation1.setId(null);
        assertThat(tagRepresentation1).isNotEqualTo(tagRepresentation2);
    }
}
