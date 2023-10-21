package com.food.delivery.service.mapper;

import com.food.delivery.domain.Authority;
import com.food.delivery.domain.UserModel;
import com.food.delivery.service.dto.AdminUserRepresentation;
import com.food.delivery.service.dto.UserRepresentation;
import java.util.*;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link UserModel} and its DTO called {@link UserRepresentation}.
 *
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class UserMapper {

    public List<UserRepresentation> usersToUserDTOs(List<UserModel> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).collect(Collectors.toList());
    }

    public UserRepresentation userToUserDTO(UserModel user) {
        return new UserRepresentation(user);
    }

    public List<AdminUserRepresentation> usersToAdminUserDTOs(List<UserModel> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToAdminUserDTO).collect(Collectors.toList());
    }

    public AdminUserRepresentation userToAdminUserDTO(UserModel user) {
        return new AdminUserRepresentation(user);
    }

    public List<UserModel> userDTOsToUsers(List<AdminUserRepresentation> userDTOs) {
        return userDTOs.stream().filter(Objects::nonNull).map(this::userDTOToUser).collect(Collectors.toList());
    }

    public UserModel userDTOToUser(AdminUserRepresentation userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            UserModel user = new UserModel();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setImageUrl(userDTO.getImageUrl());
            user.setActivated(userDTO.isActivated());
            user.setLangKey(userDTO.getLangKey());
            Set<Authority> authorities = this.authoritiesFromStrings(userDTO.getAuthorities());
            user.setAuthorities(authorities);
            return user;
        }
    }

    private Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
        Set<Authority> authorities = new HashSet<>();

        if (authoritiesAsString != null) {
            authorities =
                authoritiesAsString
                    .stream()
                    .map(string -> {
                        Authority auth = new Authority();
                        auth.setName(string);
                        return auth;
                    })
                    .collect(Collectors.toSet());
        }

        return authorities;
    }

    public UserModel userFromId(Long id) {
        if (id == null) {
            return null;
        }
        UserModel user = new UserModel();
        user.setId(id);
        return user;
    }

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public UserRepresentation toDtoId(UserModel user) {
        if (user == null) {
            return null;
        }
        UserRepresentation userDto = new UserRepresentation();
        userDto.setId(user.getId());
        return userDto;
    }

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    public Set<UserRepresentation> toDtoIdSet(Set<UserModel> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserRepresentation> userSet = new HashSet<>();
        for (UserModel userEntity : users) {
            userSet.add(this.toDtoId(userEntity));
        }

        return userSet;
    }

    @Named("login")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public UserRepresentation toDtoLogin(UserModel user) {
        if (user == null) {
            return null;
        }
        UserRepresentation userDto = new UserRepresentation();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        return userDto;
    }

    @Named("loginSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    public Set<UserRepresentation> toDtoLoginSet(Set<UserModel> users) {
        if (users == null) {
            return Collections.emptySet();
        }

        Set<UserRepresentation> userSet = new HashSet<>();
        for (UserModel userEntity : users) {
            userSet.add(this.toDtoLogin(userEntity));
        }

        return userSet;
    }
}
