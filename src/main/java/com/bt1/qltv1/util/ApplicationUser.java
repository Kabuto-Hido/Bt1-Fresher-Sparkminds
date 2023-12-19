package com.bt1.qltv1.util;

import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class ApplicationUser implements UserDetails {
    private String email;
    private String password;
    private Set<GrantedAuthority> authorities;

    public ApplicationUser() {
    }

    public ApplicationUser(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        Set<Role> roleSet  = user.getRoleSet();

        List<Role> roleList = new ArrayList<>(roleSet);
        Set<GrantedAuthority> role = new HashSet<>();

        roleList.forEach(item -> {
            role.add(new SimpleGrantedAuthority(item.getName()));
        });

        this.authorities = role;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
