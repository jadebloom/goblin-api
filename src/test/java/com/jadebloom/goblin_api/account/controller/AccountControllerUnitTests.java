package com.jadebloom.goblin_api.account.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.jadebloom.goblin_api.account.service.AccountService;
import com.jadebloom.goblin_api.security.service.JwtService;
import com.jadebloom.goblin_api.shared.error.ForbiddenException;
import com.jadebloom.goblin_api.test.MethodSecurityTestConfig;

@WebMvcTest(AccountController.class)
@Import({ MethodSecurityTestConfig.class })
public class AccountControllerUnitTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AccountService accountService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	@DisplayName("Return HTTP 204 when successfuly deleting the authenticated user's account")
	@WithMockUser(roles = { "USER" })
	public void GivenAuthenticatedUser_WhenDeletingAccount_ThenReturnHttp204()
			throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/account").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DisplayName("Return HTTP 403 when trying to delete the authenticated user's account without valid roles")
	@WithMockUser(roles = { "SOME_INVALID_ROLE" })
	public void GivenWithoutValidRoles_WhenDeletingAccount_ThenReturn403() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/account").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DisplayName("Return HTTP 403 when trying to delete the invalid authenticated user's account")
	@WithMockUser(roles = { "USER" })
	public void GivenInvalidAuthenticatedUser_WhenDeletingAccount_ThenReturn403() throws Exception {
		doThrow(ForbiddenException.class).when(accountService).deleteAccount();

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/account").with(csrf()))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

}
