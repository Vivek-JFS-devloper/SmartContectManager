package com.contactManager.configur;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.contactManager.entities.UserEntity;


public class CustomUserDetailsimpl implements UserDetails{
	
	@Autowired
	private UserEntity userEntity;
	
	
	
	public CustomUserDetailsimpl(UserEntity userEntity) {
		super();
		this.userEntity = userEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		 SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(userEntity.getRole());
		return List.of(simpleGrantedAuthority);
	}

	@Override
	public String getPassword() {
		
		return userEntity.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userEntity.getEmail();
	}

}
