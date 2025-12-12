package com.jadebloom.goblin_api.expense.entity;

import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseCategoryName;

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
    @ValidExpenseCategoryName
    private String name;

    @Column(length = 256)
    @ValidExpenseCategoryDescription
    private String description;

    public ExpenseCategoryEntity() {
    }

    public ExpenseCategoryEntity(String name) {
        this.name = name;
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

        if (id != expenseCategory.getId() || name != expenseCategory.getName()) {
            return false;
        }

        return description == expenseCategory.getDescription();
    }

    @Override
    public String toString() {
        String f = "ExpenseCategoryEntity(id=%d, name=%s, description=%s)";

        return String.format(f, id, name, description);
    }

}
