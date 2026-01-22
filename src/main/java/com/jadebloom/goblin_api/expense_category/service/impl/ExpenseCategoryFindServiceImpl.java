package com.jadebloom.goblin_api.expense_category.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.expense_category.dto.ExpenseCategoryDto;
import com.jadebloom.goblin_api.expense_category.entity.ExpenseCategoryEntity;
import com.jadebloom.goblin_api.expense_category.error.ExpenseCategoryNotFoundException;
import com.jadebloom.goblin_api.expense_category.mapper.ExpenseCategoryMapper;
import com.jadebloom.goblin_api.expense_category.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense_category.service.ExpenseCategoryFindService;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;

@Service
public class ExpenseCategoryFindServiceImpl implements ExpenseCategoryFindService {

	private final ExpenseCategoryRepository expenseCategoryRepo;

	private final ExpenseCategoryMapper mapper;

	public ExpenseCategoryFindServiceImpl(ExpenseCategoryRepository expenseCategoryRepo,
			ExpenseCategoryMapper mapper) {
		this.expenseCategoryRepo = expenseCategoryRepo;

		this.mapper = mapper;
	}

	@Override
	public Page<ExpenseCategoryDto> findAuthenticatedUserExpenseCategories(Pageable pageable)
			throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		Page<ExpenseCategoryEntity> page = expenseCategoryRepo.findAllByCreator_Id(userId, pageable);

		return page.map(mapper::map);
	}

	@Override
	public ExpenseCategoryDto findById(Long expenseCategoryId)
			throws ForbiddenException, ExpenseCategoryNotFoundException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		ExpenseCategoryEntity expenseCategory = expenseCategoryRepo
				.findById(expenseCategoryId)
				.orElseThrow(() -> {
					String f = "Expense category with ID '%d' wasn't found";

					throw new ExpenseCategoryNotFoundException(String.format(f, expenseCategoryId));
				});

		if (expenseCategory.getCreator().getId() != userId) {
			throw new ForbiddenException();
		}

		return mapper.map(expenseCategory);
	}

}
