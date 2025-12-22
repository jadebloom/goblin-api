package com.jadebloom.goblin_api.security.entity;

import java.time.ZonedDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.jadebloom.goblin_api.security.validation.ValidUserEmail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"user\"")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ValidUserEmail
    @Column(unique = true)
    private String email;

    private String password;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @ManyToMany
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;

    public UserEntity() {
    }

    public UserEntity(String email, String password) {
        this.email = email;

        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserEntity userEntity = (UserEntity) o;

        if (id != userEntity.getId() || email != userEntity.getEmail()) {
            return false;
        }

        return password == userEntity.getPassword();
    }

    @Override
    public String toString() {
        return "UserEntity(id=" + id +
                ", email=" + email +
                ", password=" + password +
                ", roles=" + roles.toString() +
                ", createdAt=" + createdAt + ")";
    }

}
