package com.project.todolistwebapp.config.security;

import com.project.todolistwebapp.model.User;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
public class WebAuthenticationToken implements Authentication {

    private final User user;
    private final Object credentials;
    private final Collection<? extends GrantedAuthority> authorities;
    private boolean isAuthenticated;

    public WebAuthenticationToken(User user) {
        this(user, null, Collections.emptyList());
    }

    public WebAuthenticationToken(User user, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.credentials = credentials;
        this.authorities = authorities;
        this.isAuthenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }

    @Override
    public Object getDetails() {
        return user;
    }

    public User getUser(){return user;}

    @Override
    public Object getPrincipal() {
        return user.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
