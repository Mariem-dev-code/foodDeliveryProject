package com.food.delivery.service.dto;

import com.food.delivery.domain.UserModel;
import java.io.Serializable;

/**
 * A DTO representing a user, with only the public attributes.
 */
public class UserRepresentation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    public UserRepresentation() {
        // Empty constructor needed for Jackson.
    }

    public UserRepresentation(UserModel user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserRepresentation{" +
            "id='" + id + '\'' +
            ", login='" + login + '\'' +
            "}";
    }
}
