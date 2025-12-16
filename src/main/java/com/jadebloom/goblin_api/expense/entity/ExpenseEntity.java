package com.jadebloom.goblin_api.expense.entity;

import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.jadebloom.goblin_api.currency.entity.CurrencyEntity;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseAmount;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseDescription;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabel;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseLabelsList;
import com.jadebloom.goblin_api.expense.validation.ValidExpenseName;

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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "expense")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, length = 64)
    @ValidExpenseName
    private String name;

    @Column(length = 256)
    @ValidExpenseDescription
    private String description;

    @Column(nullable = false)
    @ValidExpenseAmount
    private Long amount;

    @ElementCollection
    @CollectionTable(name = "expense_labels", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "label", length = 32)
    @ValidExpenseLabelsList
    private List<@ValidExpenseLabel String> labels;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "expense_category_id", referencedColumnName = "id")
    @NotNull
    private ExpenseCategoryEntity expenseCategory;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "id")
    @NotNull
    private CurrencyEntity currency;

    public ExpenseEntity() {
    }

    public ExpenseEntity(
            String name,
            Long amount,
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

    public Long getAmount() {
        return amount;
    }

    public List<String> getLabels() {
        return labels;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
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

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
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

        if (createdAt != expenseEntity.createdAt) {
            return false;
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
                ", createdAt=" + createdAt +
                ", expenseCategory=" + expenseCategory +
                ", currency=" + currency + ")";

        return f;
    }

}
