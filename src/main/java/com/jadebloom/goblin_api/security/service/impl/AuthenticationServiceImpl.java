package com.jadebloom.goblin_api.security.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jadebloom.goblin_api.security.dto.JwtResponseDto;
import com.jadebloom.goblin_api.security.dto.LoginDto;
import com.jadebloom.goblin_api.security.dto.RegistrationDto;
import com.jadebloom.goblin_api.security.entity.RoleEntity;
import com.jadebloom.goblin_api.security.entity.UserEntity;
import com.jadebloom.goblin_api.security.error.IncorrectPasswordException;
import com.jadebloom.goblin_api.security.error.UserEmailInUseException;
import com.jadebloom.goblin_api.security.error.UserNotFoundException;
import com.jadebloom.goblin_api.security.repository.RoleRepository;
import com.jadebloom.goblin_api.security.repository.UserRepository;
import com.jadebloom.goblin_api.security.service.AuthenticationService;
import com.jadebloom.goblin_api.security.service.JwtService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;

        this.roleRepository = roleRepository;

        this.passwordEncoder = passwordEncoder;

        this.authenticationManager = authenticationManager;

        this.jwtService = jwtService;
    }

    @Override
    public JwtResponseDto register(RegistrationDto dto) throws UserEmailInUseException {
        String email = dto.getEmail(), password = dto.getPassword();

        if (userRepository.existsByEmail(email)) {
            String f = "Email '%s' is already in use";

            throw new UserEmailInUseException(String.format(f, email));
        }

        Optional<RoleEntity> opt = roleRepository.findByName("ROLE_USER");

        if (opt.isEmpty()) {
            throw new RuntimeException("Failed to load user roles");
        }

        Set<RoleEntity> userRoles = Set.of(opt.get());

        UserEntity user = new UserEntity(email, passwordEncoder.encode(password), userRoles);

        userRepository.save(user);

        Set<String> userRoleNames = new HashSet<>();
        Set<GrantedAuthority> userGrantedAuthorities = new HashSet<>();

        for (RoleEntity role : user.getRoles()) {
            userGrantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));

            userRoleNames.add(role.getName());
        }

        Authentication request = new UsernamePasswordAuthenticationToken(
                email,
                password,
                userGrantedAuthorities);
        authenticationManager.authenticate(request);

        String accessToken = jwtService.generateAccessToken(email, userRoleNames);
        String refreshToken = jwtService.generateRefreshToken(email, userRoleNames);

        return new JwtResponseDto(accessToken, refreshToken);
    }

    @Override
    public JwtResponseDto login(LoginDto dto) throws UserNotFoundException, IncorrectPasswordException {
        String email = dto.getEmail(), password = dto.getPassword();

        Optional<UserEntity> opt = userRepository.findByEmail(email);

        if (opt.isEmpty()) {
            String f = "User with email '%s' wasn't found";

            throw new UserNotFoundException(String.format(f, email));
        }

        UserEntity user = opt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Provided password is incorrect");
        }

        Set<GrantedAuthority> userGrantedAuthorities = new HashSet<>();
        Set<String> userRoleNames = new HashSet<>();

        for (RoleEntity role : user.getRoles()) {
            userGrantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));

            userRoleNames.add(role.getName());
        }

        Authentication request = new UsernamePasswordAuthenticationToken(
                email,
                password,
                userGrantedAuthorities);
        authenticationManager.authenticate(request);

        String accessToken = jwtService.generateAccessToken(email, userRoleNames);
        String refreshToken = jwtService.generateRefreshToken(email, userRoleNames);

        return new JwtResponseDto(accessToken, refreshToken);
    }

}
