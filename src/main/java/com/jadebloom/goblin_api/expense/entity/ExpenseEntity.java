package com.jadebloom.goblin_api.expense.entity;

import java.util.List;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expense")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(length = 256)
    private String description;

    @Column(nullable = false)
    private Integer amount;

    @ElementCollection
    @CollectionTable(name = "expense_labels", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "label", length = 32)
    private List<String> labels;

    @ManyToOne
    @JoinColumn(name = "expense_category_id", referencedColumnName = "id")
    private ExpenseCategoryEntity expenseCategory;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "id")
    private CurrencyEntity currency;

    public ExpenseEntity() {
    }

    public ExpenseEntity(
            String name,
            Integer amount,
            ExpenseCategoryEntity expenseCategory,
            CurrencyEntity currency) {
        this.name = name;

        this.amount = amount;

        this.expenseCategory = expenseCategory;

        this.currency = currency;
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

    public Integer getAmount() {
        return amount;
    }

    public List<String> getLabels() {
        return labels;
    }

    public ExpenseCategoryEntity getExpenseCategory() {
        return expenseCategory;
    }

    public CurrencyEntity getCurrency() {
        return currency;
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

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void setExpenseCategory(ExpenseCategoryEntity expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExpenseEntity expenseEntity = (ExpenseEntity) o;

        if (id != expenseEntity.getId() || name != expenseEntity.getName()) {
            return false;
        }

        if (description != expenseEntity.getDescription() || amount != expenseEntity.getAmount()) {
            return false;
        }

        if (labels.size() != expenseEntity.getLabels().size()) {
            return false;
        }

        for (int i = 0; i < labels.size(); i++) {
            String a = labels.get(i);
            String b = expenseEntity.getLabels().get(i);

            if (!a.equals(b)) {
                return false;
            }
        }

        if (expenseCategory != expenseEntity.expenseCategory) {
            return false;
        }

        return currency == expenseEntity.getCurrency();
    }

    @Override
    public String toString() {
        String f = "ExpenseEntity(id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", amount=" + amount +
                ", labels=" + labels +
                ", expenseCategory=" + expenseCategory +
                ", currency=" + currency + ")";

        return f;
    }

}
