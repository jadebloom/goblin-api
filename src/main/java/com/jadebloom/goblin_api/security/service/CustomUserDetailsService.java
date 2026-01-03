package com.jadebloom.goblin_api.security.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.security.config.CustomUserDetails;
import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> {
					String f = "User with email %s wasn't found";
					String errorMessage = String.format(f, email);

					throw new UsernameNotFoundException(errorMessage);
				});

		Set<GrantedAuthority> userGrantedAuthorities = new HashSet<>();

		for (RoleEntity role : user.getRoles()) {
			userGrantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
		}

		return new CustomUserDetails(
				user.getId(),
				email,
				user.getPassword(),
				userGrantedAuthorities);
	}

}
