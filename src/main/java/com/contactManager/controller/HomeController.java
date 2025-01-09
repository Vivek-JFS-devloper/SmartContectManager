package com.contactManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactManager.entities.UserEntity;
import com.contactManager.msghelper.MassageHelper;
import com.contactManager.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
     
	@Autowired
	BCryptPasswordEncoder encoder;
	
    @Autowired
    private UserRepository userRepository;
    
    // Home Page
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    // About Page
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    // Signup Page
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("title", "Signup - Smart Contact Manager");
        model.addAttribute("newUser", new UserEntity());
        return "signup";
    }

    // Handler for registering a user
    
    @PostMapping("/do_register")
    public String registerUser(
            @Valid @ModelAttribute("newUser") UserEntity userEntity,
            BindingResult bindingResult,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session) {
        
        try {
            // Check if the user has agreed to the terms and conditions
            if (!agreement) {
                System.out.println("You have not agreed to the terms and conditions.");
                session.setAttribute("message", new MassageHelper("Please agree to the terms and conditions.", "alert-danger"));
                return "signup";
            }

            // Check for validation errors
            if (bindingResult.hasErrors()) {
                System.out.println("Validation errors: " + bindingResult);
                model.addAttribute("newUser", userEntity);
                return "signup";
            }
            
            userEntity.setRole("ROLE_USER");
            userEntity.setEnabled(true);
            userEntity.setImageUrl("imag.png");
            
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));
            
            System.out.println("Agreement "+agreement);
            System.out.println("UserEntity "+userEntity);
            
            // Save the user to the database
            UserEntity user = this.userRepository.save(userEntity);
            
            // Clear any existing messages and set a success message
            session.removeAttribute("message");
            session.setAttribute("message", new MassageHelper("Successfully Registered!", "alert-success"));
            
            // Reset the form after successful registration
            model.addAttribute("newUser", new UserEntity());
            return "signup";
        
        } catch (Exception ex) {
            ex.printStackTrace();

            // Handle any exceptions and show an error message
            session.setAttribute("message", new MassageHelper("Something went wrong: " + ex.getMessage(), "alert-danger"));
            model.addAttribute("newUser", userEntity);
            return "signup";
        }
    }

   
    //Custom login controller
    @GetMapping("/login")
    public String openLoginPage(Model model) {
    	model.addAttribute("title", "this is a login page.");
    	return "login";
    }
    
    
}
