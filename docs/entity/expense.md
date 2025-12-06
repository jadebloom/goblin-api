# Expense Entity

1. id PK.
2. name VARCHAR(64) NOT NULL.
3. description VARCHAR(256).
4. amount INT NOT NULL.
5. labels VARCHAR(16)[].
6. created_at DATETIME NOT NULL.
7. expense_category_id FK REFERENCES expense_category(id).
8. currency_id FK REFERENCES currency(id).
9. user_id FK REFERENCES user(id).
