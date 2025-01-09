package com.contactManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactManager.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	
	@Query("select u FROM UserEntity u Where u.email =:email")
	public UserEntity getUserByUserName(@Param("email") String email);
}
