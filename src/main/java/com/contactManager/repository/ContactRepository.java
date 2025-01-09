package com.contactManager.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactManager.entities.ContactEntity;

public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {
	/*
	 * Page:-A page is a sublist of a list of objects. It allows gain information about the position of it in the
	 * containing entire list.
	 * Pageable:- Abstract interface for pagination information.
	 * Returns a {@link Pageable} instance representing no pagination setup.
	 ******Pageable have two information
	 * 1. how many show of member of one page 
	 * 2. total page 
	 */
	@Query("SELECT c FROM ContactEntity c WHERE c.user.id = :userId")
	Page<ContactEntity> findContactByUser(@Param("userId") int userId,Pageable pageable);
}
