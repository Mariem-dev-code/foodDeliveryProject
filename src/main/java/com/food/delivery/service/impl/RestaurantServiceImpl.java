package com.food.delivery.service.impl;

import com.food.delivery.domain.RestaurantModel;
import com.food.delivery.repository.RestaurantRepository;
import com.food.delivery.service.RestaurantService;
import com.food.delivery.service.dto.RestaurantRepresentation;
import com.food.delivery.service.mapper.RestaurantMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RestaurantModel}.
 */
@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public RestaurantRepresentation save(RestaurantRepresentation restaurantRepresentation) {
        log.debug("Request to save Restaurant : {}", restaurantRepresentation);
        RestaurantModel restaurantModel = restaurantMapper.toEntity(restaurantRepresentation);
        restaurantModel = restaurantRepository.save(restaurantModel);
        return restaurantMapper.toDto(restaurantModel);
    }

    @Override
    public RestaurantRepresentation update(RestaurantRepresentation restaurantRepresentation) {
        log.debug("Request to update Restaurant : {}", restaurantRepresentation);
        RestaurantModel restaurantModel = restaurantMapper.toEntity(restaurantRepresentation);
        restaurantModel = restaurantRepository.save(restaurantModel);
        return restaurantMapper.toDto(restaurantModel);
    }

    @Override
    public Optional<RestaurantRepresentation> partialUpdate(RestaurantRepresentation restaurantRepresentation) {
        log.debug("Request to partially update Restaurant : {}", restaurantRepresentation);

        return restaurantRepository
            .findById(restaurantRepresentation.getId())
            .map(existingRestaurant -> {
                restaurantMapper.partialUpdate(existingRestaurant, restaurantRepresentation);

                return existingRestaurant;
            })
            .map(restaurantRepository::save)
            .map(restaurantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantRepresentation> findAll(Pageable pageable) {
        log.debug("Request to get all Restaurants");
        return restaurantRepository.findAll(pageable).map(restaurantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantRepresentation> findOne(Long id) {
        log.debug("Request to get Restaurant : {}", id);
        return restaurantRepository.findById(id).map(restaurantMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Restaurant : {}", id);
        restaurantRepository.deleteById(id);
    }
}
