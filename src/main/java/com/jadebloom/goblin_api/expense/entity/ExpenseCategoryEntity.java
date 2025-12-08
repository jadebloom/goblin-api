package com.jadebloom.goblin_api.expense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "expense_category")
public class ExpenseCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String name;

    @Column(length = 256)
    private String description;

    public ExpenseCategoryEntity() {
    }

    public ExpenseCategoryEntity(String name) {
        this.name = name;
    }

    public ExpenseCategoryEntity(String name, String description) {
        this.name = name;

        this.description = description;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
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

        return id == expenseCategory.getId();
    }

}
