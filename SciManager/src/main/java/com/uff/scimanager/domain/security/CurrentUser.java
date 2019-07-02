package com.uff.scimanager.domain.security;

import org.springframework.security.core.authority.AuthorityUtils;

import com.uff.scimanager.domain.Role;
import com.uff.scimanager.domain.User;

public class CurrentUser extends org.springframework.security.core.userdetails.User {
	
	private static final long serialVersionUID = 1L;
	
	private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getUserRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return user.getUserId();
    }
    
    public String getPassword() {
        return user.getPassword();
    }

    public Role getUserRole() {
        return user.getUserRole();
    }
	
}