package com.contactManager.configur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.contactManager.entities.UserEntity;
import com.contactManager.repository.UserRepository;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching username by database;
		UserEntity byUserName = userRepository.getUserByUserName(username);
		if(byUserName == null) {
			throw new UsernameNotFoundException("user not found !!");
		}
		CustomUserDetailsimpl detailsimpl = new CustomUserDetailsimpl(byUserName);
		return detailsimpl;
	}

}
