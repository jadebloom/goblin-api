package com.jadebloom.goblin_api.security.config;

import java.util.Collection;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private Long id;
	private String email;
	private String password;
	private Set<GrantedAuthority> grantedAuthorities;

	public CustomUserDetails(
			Long id,
			String email,
			String password,
			Set<GrantedAuthority> grantedAuthorities) {
		this.id = id;

		this.email = email;

		this.password = password;

		this.grantedAuthorities = grantedAuthorities;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public @Nullable String getPassword() {
		return password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	@Override
	public String toString() {
		return "CustomUserDetails(id=" + id +
				", email=" + email +
				", password=" + password +
				", grantedAuthorities=" + grantedAuthorities + ")";
	}

}
