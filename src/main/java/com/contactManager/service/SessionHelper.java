package com.contactManager.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {

	@Bean
	public void removeMassageFromSession() {
			System.out.println("session remove");
		try {
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
					.getSession();
			
			session.removeAttribute("message.....................");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
