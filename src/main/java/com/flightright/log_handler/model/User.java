package com.flightright.log_handler.model;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class User {
    private String email;
    private String phone;

    public User(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    protected User() {

    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phone);
    }
}
