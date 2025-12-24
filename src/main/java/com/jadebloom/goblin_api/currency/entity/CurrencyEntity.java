package com.jadebloom.goblin_api.currency.entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.jadebloom.goblin_api.currency.validation.ValidCurrencyAlphabeticalCode;
import com.jadebloom.goblin_api.currency.validation.ValidCurrencyName;
import com.jadebloom.goblin_api.security.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    @ValidCurrencyName
    private String name;

    @Column(name = "alphabetical_code", length = 3)
    @ValidCurrencyAlphabeticalCode
    private String alphabeticalCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id", nullable = false, updatable = false)
    private UserEntity creator;

    public CurrencyEntity() {
    }

    public CurrencyEntity(String name, UserEntity creator) {
        this.name = name;

        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlphabeticalCode() {
        return alphabeticalCode;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlphabeticalCode(String alphabeticalCode) {
        this.alphabeticalCode = alphabeticalCode;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CurrencyEntity currencyEntity = (CurrencyEntity) o;

        if (id != currencyEntity.getId() || name != currencyEntity.getName()) {
            return false;
        }

        if (createdAt != currencyEntity.createdAt) {
            return false;
        }

        return alphabeticalCode == currencyEntity.getAlphabeticalCode();
    }

    @Override
    public String toString() {
        return "CurrencyEntity(id=" + id +
                ", name=" + name +
                ", alphabeticalCode=" + alphabeticalCode +
                ", createdAt=" + createdAt +
                ", creator=" + creator + ")";
    }

}
