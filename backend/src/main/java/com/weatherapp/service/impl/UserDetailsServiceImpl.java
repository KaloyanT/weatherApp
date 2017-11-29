package com.weatherapp.service.impl;

import com.weatherapp.model.factory.JwtUserFactory;
import com.weatherapp.model.entity.User;
import com.weatherapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("The username " + username + " doesn't exist.");
        }

        // Should be removed from here and a proper entity for User Roles should be added
        // List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // authorities.add(new SimpleGrantedAuthority("Admin"));

        return JwtUserFactory.create(user);
    }
}
