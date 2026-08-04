package com.JavaBackEnd.spring_boot_journey_week6_day4.security;

import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Wraps our User entity so Spring Security can work with it.
// Spring Security doesn't know about our User — it knows UserDetails.
// This adapter bridges the two.
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Expose the underlying entity so controllers can map it to UserResponse
    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override public String getPassword()                   { return user.getPassword(); }
    @Override public String getUsername()                   { return user.getEmail(); }
    @Override public boolean isAccountNonExpired()          { return user.isAccountNonExpired(); }
    @Override public boolean isAccountNonLocked()           { return user.isAccountNonLocked(); }
    @Override public boolean isCredentialsNonExpired()      { return user.isCredentialsNonExpired(); }
    @Override public boolean isEnabled()                    { return user.isEnabled(); }
}
