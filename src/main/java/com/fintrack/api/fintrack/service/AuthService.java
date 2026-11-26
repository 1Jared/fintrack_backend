package com.fintrack.api.fintrack.service;


import java.util.Locale;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fintrack.api.fintrack.domain.Users;
import com.fintrack.api.fintrack.dto.LoginResponse;
import com.fintrack.api.fintrack.error.ApiException;
import com.fintrack.api.fintrack.repository.UserRepository;
import com.fintrack.api.fintrack.security.TokenService;

@Service
public class AuthService {

    private static final String DUPLICATE = "An account with this email already exists";
    private static final String INVALID_LOGIN = "Invalid email or password";


 private final UserRepository users;
 private final PasswordEncoder passwordEncoder;
 private final TokenService tokenService;

 //compared against when email unknown so response timing doesnt reveal registered accs
 private final String dummyHash;

 public AuthService(UserRepository users, PasswordEncoder passwordEncoder,TokenService tokenService){
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.dummyHash =passwordEncoder.encode("fintrack-dummy-password");
 }

 public LoginResponse register(String rawEmail , String password){
    String email = normalizeEmail(rawEmail);
    if(users.findByEmail(email).isPresent()){
        throw ApiException.conflict(DUPLICATE);
    }
    try{
    long userId = users.create(email, passwordEncoder.encode(password));
    return new LoginResponse(tokenService.createToken(userId, email), email);
    }catch(DuplicateKeyException exception){
        throw ApiException.conflict(DUPLICATE); 
    }
 }

 public LoginResponse login(String rawEmail, String password){
    String email = normalizeEmail(rawEmail);
    Users user = users.findByEmail(email).orElse(null);
    String hash = user == null ? dummyHash : user.passwordHash();

    if(!passwordEncoder.matches(password, hash)  || user == null){
        throw ApiException.unauthorized(INVALID_LOGIN);
    }
    return  new LoginResponse(tokenService.createToken(user.id(),user.email()),user.email());
 }

 public  static  String normalizeEmail(String email){
    return email.trim().toLowerCase(Locale.ROOT);
 }
}
