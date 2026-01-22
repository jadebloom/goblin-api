package com.jadebloom.goblin_api.expense.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense.dto.ExpenseDto;
import com.jadebloom.goblin_api.expense.entity.ExpenseEntity;
import com.jadebloom.goblin_api.expense.error.ExpenseNotFoundException;
import com.jadebloom.goblin_api.expense.mapper.ExpenseMapper;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.expense.service.ExpenseFindService;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@Service
public class ExpenseFindServiceImpl implements ExpenseFindService {

	private final ExpenseRepository expenseRepo;

	private final ExpenseMapper mapper;

	public ExpenseFindServiceImpl(ExpenseRepository expenseRepo, ExpenseMapper mapper) {
		this.expenseRepo = expenseRepo;

		this.mapper = mapper;
	}

	@Override
	public Page<ExpenseDto> findUserAuthenticatedExpenses(Pageable pageable) throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		Page<ExpenseEntity> page = expenseRepo.findAllByCreator_Id(userId, pageable);

		return page.map(mapper::map);
	}

	@Override
	public ExpenseDto findById(Long expenseId) throws ForbiddenException, ExpenseNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseEntity expense = expenseRepo.findById(expenseId)
				.orElseThrow(() -> {
					String f = "Expense with ID '%d' wasn't found";

					throw new ExpenseNotFoundException(String.format(f, expenseId));
				});

		if (expense.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		return mapper.map(expense);
	}

}
