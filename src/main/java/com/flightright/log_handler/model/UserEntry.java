package com.flightright.log_handler.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class UserEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    protected UserEntry() {
    }

    @Embedded
    private User user;

    private String source;

    public UserEntry(User user, String source) {
        this.user = user;
        this.source = source;
    }

    public UserEntry(String userEmail, String userPhone, String source) {
        this.user = new User(userEmail, userPhone);
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntry)) return false;
        UserEntry userEntry = (UserEntry) o;
        return Objects.equals(user, userEntry.user) && Objects.equals(source, userEntry.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, source);
    }

}
