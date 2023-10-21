package com.food.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TagModelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagModel.class);
        TagModel tagModel1 = new TagModel();
        tagModel1.setId(1L);
        TagModel tagModel2 = new TagModel();
        tagModel2.setId(tagModel1.getId());
        assertThat(tagModel1).isEqualTo(tagModel2);
        tagModel2.setId(2L);
        assertThat(tagModel1).isNotEqualTo(tagModel2);
        tagModel1.setId(null);
        assertThat(tagModel1).isNotEqualTo(tagModel2);
    }
}
