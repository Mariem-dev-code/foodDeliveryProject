package com.food.delivery.service.impl;

import com.food.delivery.domain.TagModel;
import com.food.delivery.repository.TagRepository;
import com.food.delivery.service.TagService;
import com.food.delivery.service.dto.TagRepresentation;
import com.food.delivery.service.mapper.TagMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TagModel}.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagRepresentation save(TagRepresentation tagRepresentation) {
        log.debug("Request to save Tag : {}", tagRepresentation);
        TagModel tagModel = tagMapper.toEntity(tagRepresentation);
        tagModel = tagRepository.save(tagModel);
        return tagMapper.toDto(tagModel);
    }

    @Override
    public TagRepresentation update(TagRepresentation tagRepresentation) {
        log.debug("Request to update Tag : {}", tagRepresentation);
        TagModel tagModel = tagMapper.toEntity(tagRepresentation);
        tagModel = tagRepository.save(tagModel);
        return tagMapper.toDto(tagModel);
    }

    @Override
    public Optional<TagRepresentation> partialUpdate(TagRepresentation tagRepresentation) {
        log.debug("Request to partially update Tag : {}", tagRepresentation);

        return tagRepository
            .findById(tagRepresentation.getId())
            .map(existingTag -> {
                tagMapper.partialUpdate(existingTag, tagRepresentation);

                return existingTag;
            })
            .map(tagRepository::save)
            .map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagRepresentation> findAll(Pageable pageable) {
        log.debug("Request to get all Tags");
        return tagRepository.findAll(pageable).map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagRepresentation> findOne(Long id) {
        log.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id).map(tagMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tag : {}", id);
        tagRepository.deleteById(id);
    }
}
