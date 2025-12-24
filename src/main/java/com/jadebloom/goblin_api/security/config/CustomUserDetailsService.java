package com.jadebloom.goblin_api.security.config;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.repository.UserRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            String f = "User with email %s wasn't found";

            throw new UsernameNotFoundException(String.format(f, email));
        }
        UserEntity user = optUser.get();

        Set<GrantedAuthority> userGrantedAuthorities = new HashSet<>();

        for (RoleEntity role : user.getRoles()) {
            userGrantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new User(email, user.getPassword(), userGrantedAuthorities);
    }

}
