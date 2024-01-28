package com.pkm.medicalinventory.config.security;

import com.pkm.medicalinventory.entity.User;
import com.pkm.medicalinventory.repository.main.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service("userDetailsService")
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService{
	@Autowired
	private UserRepository userRepository;
	
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findTop1ByUsername(username);
		
		if (null == user) {
			throw new UsernameNotFoundException("Username not found");
		}
		log.info("loaded username: {}", username);
		
		return new UserDetailDomain(user);
	}

}
