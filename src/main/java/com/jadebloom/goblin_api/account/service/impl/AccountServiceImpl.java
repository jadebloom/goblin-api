package com.jadebloom.goblin_api.account.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jadebloom.goblin_api.account.dto.UpdatePasswordDto;
import com.jadebloom.goblin_api.account.errors.InvalidPasswordException;
import com.jadebloom.goblin_api.account.service.AccountService;
import com.jadebloom.goblin_api.currency.repository.CurrencyRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseCategoryRepository;
import com.jadebloom.goblin_api.expense.repository.ExpenseRepository;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.util.SecurityContextUtils;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.shared.validation.GenericValidator;

@Service
public class AccountServiceImpl implements AccountService {

	private final UserRepository userRepository;

	private final ExpenseRepository expenseRepository;

	private final ExpenseCategoryRepository expenseCategoryRepository;

	private final CurrencyRepository currencyRepository;

	private final PasswordEncoder passwordEncoder;

	public AccountServiceImpl(UserRepository userRepository, ExpenseRepository expenseRepository,
			ExpenseCategoryRepository expenseCategoryRepository,
			CurrencyRepository currencyRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;

		this.expenseRepository = expenseRepository;

		this.expenseCategoryRepository = expenseCategoryRepository;

		this.currencyRepository = currencyRepository;

		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void updatePassword(UpdatePasswordDto updateDto)
			throws ForbiddenException, InvalidPasswordException, IncorrectPasswordException {
		if (!GenericValidator.isValid(updateDto)) {
			String message = GenericValidator.getValidationErrorMessage(updateDto);

			throw new InvalidPasswordException(message);
		}

		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		UserEntity user =
				userRepository.findById(userId).orElseThrow(() -> new ForbiddenException());

		String oldPassword = updateDto.getOldPassword(), newPassword = updateDto.getNewPassword();

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			System.out.println("FLAG");
			System.out.println(passwordEncoder.encode(oldPassword));
			System.out.println(user.getPassword());
			throw new IncorrectPasswordException("Provided password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		userRepository.save(user);
	}

	@Override
	public void deleteAccount() throws ForbiddenException {
		Long userId = SecurityContextUtils.getAuthenticatedUserId()
				.orElseThrow(() -> new ForbiddenException());

		userRepository.findById(userId).orElseThrow(() -> new ForbiddenException());

		expenseRepository.deleteAllByCreator_Id(userId);

		expenseCategoryRepository.deleteAllByCreator_Id(userId);

		currencyRepository.deleteAllByCreator_Id(userId);

		userRepository.deleteById(userId);
	}

}
