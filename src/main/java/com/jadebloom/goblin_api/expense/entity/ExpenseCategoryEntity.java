package com.jadebloom.goblin_api.expense.entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryName;
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
@Table(name = "expense_category")
public class ExpenseCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    @ValidExpenseCategoryName
    private String name;

    @Column(length = 256)
    @ValidExpenseCategoryDescription
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false, updatable = false)
    private UserEntity creator;

    public ExpenseCategoryEntity() {
    }

    public ExpenseCategoryEntity(String name, UserEntity creator) {
        this.name = name;

        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
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

        ExpenseCategoryEntity expenseCategory = (ExpenseCategoryEntity) o;

        if (id != expenseCategory.getId() || name != expenseCategory.getName()) {
            return false;
        }

        if (createdAt != expenseCategory.createdAt) {
            return false;
        }

        return description == expenseCategory.getDescription();
    }

    @Override
    public String toString() {
        return "ExpenseCategoryEntity(id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", createdAt=" + createdAt +
                ", creatorId=" + creator.getId() + ")";
    }

}
