package com.food.delivery.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.food.delivery.FoodDeliveryBdApp;
import com.food.delivery.config.AsyncSyncConfiguration;
import com.food.delivery.domain.IngredientModel;
import com.food.delivery.repository.IngredientRepository;
import com.food.delivery.service.dto.IngredientRepresentation;
import com.food.delivery.service.impl.IngredientServiceImpl;
import com.food.delivery.service.mapper.IngredientMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.config.JHipsterConstants;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { FoodDeliveryBdApp.class })
@Transactional
@Profile(JHipsterConstants.SPRING_PROFILE_TEST)
public class IngredientServiceImplTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientMapper ingredientMapper;

    private IngredientServiceImpl ingredientService;

    @BeforeEach
    public void init() {
        ingredientService = new IngredientServiceImpl(ingredientRepository, ingredientMapper);
    }

    @Test
    public void testBusinessLogic() {}
}
