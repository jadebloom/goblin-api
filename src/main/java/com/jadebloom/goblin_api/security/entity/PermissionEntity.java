package com.jadebloom.goblin_api.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "permission")
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    public PermissionEntity() {
    }

    public PermissionEntity(String name) {
        this.name = name;
    }

    public PermissionEntity(Long id, String name) {
        this.id = id;

        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PermissionEntity e = (PermissionEntity) o;

        return id == e.getId() && name == e.getName();
    }

    @Override
    public String toString() {
        return "PermissionEntity(id=" + id + ", name=" + name + ")";
    }

}
