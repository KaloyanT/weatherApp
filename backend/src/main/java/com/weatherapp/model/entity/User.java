package com.weatherapp.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity(name = "User")
@Table(name = "user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GenericGenerator(name = "userIdGenerator", strategy = "increment")
    @GeneratedValue(generator = "userIdGenerator")
    @Column(name = "userId")
    private long userId;

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    @Size(min = 4, max = 50)
    private String username;

    @Basic(optional = false)
    @NotNull
    @Size(min = 8, max = 1000)
    private String password;

    @Basic(optional = false)
    @Column(unique = true)
    @NotNull
    private String email;

    @Column(name = "lastPasswordResetDate")
    @NotNull
    private Date lastPasswordResetDate;

    @Column(name = "authorities")
    @NotNull
    private String authorities;

    @Column(name = "enabled")
    @NotNull
    private Boolean enabled;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Override the default equals method for consistency
     */
    @Override
    public boolean equals(Object o) {

        if(this == o) {
            return true;
        }
        if(! (o instanceof User) ) {
            return false;
        }

        return (this.userId != 0) &&
                (this.userId == ((User) o).userId);
    }

    /**
     * Use this hashCode for consistency
     */
    @Override
    public int hashCode() {
        return 1;
    }
}
