package com.example.realTimeAuctionSystem.service;

import com.example.realTimeAuctionSystem.exception.EmailNotFoundException;
import com.example.realTimeAuctionSystem.exception.NoSuchExist;
import com.example.realTimeAuctionSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).map(CustomUserDetails::new).orElseThrow(()->new NoSuchExist("No such Email Exist : "));
    }
    public UserDetails loadUserByEmail(String email) throws EmailNotFoundException {
        return (UserDetails) userRepository.findByEmail(email)
                .orElseThrow(()->new EmailNotFoundException("No such Email Exist :"));
    }
    public UserDetails loadUserById(String id) throws NoSuchExist{
        return  userRepository.findById(id).map(CustomUserDetails::new)
                .orElseThrow(()->new NoSuchExist("No Such Id Exist :"));
    }
}
