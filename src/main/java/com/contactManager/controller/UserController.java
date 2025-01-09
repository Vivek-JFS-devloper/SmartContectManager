package com.contactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactManager.entities.UserEntity;
import com.contactManager.msghelper.MassageHelper;
import com.contactManager.entities.ContactEntity;
import com.contactManager.repository.ContactRepository;
import com.contactManager.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository repository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		UserEntity userEntity = this.repository.getUserByUserName(userName);
		model.addAttribute("user", userEntity);
	}

	@RequestMapping("/index")
	public String dashBoard(Model model, Principal principal) {
		model.addAttribute("title", "DashBoard page");
		return "normal/user_dashboard";
	}

	@RequestMapping("/addContact")
	public String openAddContactForm(Model model, Principal principal) {
		model.addAttribute("title", "Add new Contact page");
		model.addAttribute("contact", new ContactEntity());
		return "normal/add-contact";
	}

	// data processing add contact

	@PostMapping("/process-contact")
	public String addContactData(@ModelAttribute ContactEntity contactEntity,
			@RequestParam("profileImage") MultipartFile imageFile, Principal principal, HttpSession session) {

		try {
			// fetch the logged-in user's details
			String loginUserName = principal.getName();
			UserEntity userDetails = this.repository.getUserByUserName(loginUserName);

			if (!imageFile.isEmpty()) {
				// Generate a unique file name based on current timestamp and original file name
				String filename = imageFile.getOriginalFilename();
				String uniqFileName = System.currentTimeMillis() + "-" + filename;

				// get the folder path where file will be store
				File folder = new ClassPathResource("static/image").getFile();
				System.out.println("Folder Path to store image :=>  " + folder);

				// Resolve the file path where the image will be stored
				Path filePath = Paths.get(folder.getAbsolutePath(), uniqFileName);

				// Save the file to resolved path
				Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				// set the unique file name in the contact entity
				contactEntity.setImage(uniqFileName);

				userDetails.getContactEntities().add(contactEntity);
				contactEntity.setUserEntity(userDetails);
				this.repository.save(userDetails);
				session.setAttribute("message",
						new MassageHelper("Your Data Add SuccessFully !! Add new Content..", "success"));
				System.err.println(session);
			} else {
				// if no image is upload use a default image
				System.out.println("No image file uploaded. Using default image.");
				contactEntity.setImage("default.png");
				// associate the contact entity with the user
				userDetails.getContactEntities().add(contactEntity);
				contactEntity.setUserEntity(userDetails);
				this.repository.save(userDetails);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Error: " + ex.getMessage());
			session.setAttribute("message", new MassageHelper("Something went wrong !! Try Again..", "danger"));
		}

		return "normal/add-contact";
	}

	// show user details

	@GetMapping("/showViewContacts/{page}")
	public String viewAllDetails(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "View all contacts");
		String userName = principal.getName();
		UserEntity userEntity = repository.getUserByUserName(userName);

		// page is current page to show and how to show total number of one page

		Pageable pageable = PageRequest.of(page, 10);
		Page<ContactEntity> contactByUser = contactRepository.findContactByUser(userEntity.getId(), pageable);
		model.addAttribute("Contact", contactByUser);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contactByUser.getTotalPages());
		return "normal/view-contact";
	}

	// show particular details
	@GetMapping("/{id}/contactDetails")
	public String showParticularDetails(@PathVariable("id") int id, Model model, Principal principal) {
		try {
			Optional<ContactEntity> optional = this.contactRepository.findById(id);
			ContactEntity contactEntity = optional.get();

			String userName = principal.getName();
			UserEntity userByUserName = this.repository.getUserByUserName(userName);

			if (userByUserName.getId() == contactEntity.getUser().getId()) {
				model.addAttribute("title",
						"show details by " + contactEntity.getFname() + " " + contactEntity.getCname());
				model.addAttribute("contact", contactEntity);
			} else {
				System.out.println("errot " + contactEntity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Error " + ex.getMessage());
			model.addAttribute("id", "You have not permission to you Plz, try to other user !");
			return "login";
		}
		return "normal/show_about";
	}

	// delete contact

	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") Integer id, Model model, Principal principal) {
		try {
			Optional<ContactEntity> optional = contactRepository.findById(id);

			if (optional.isPresent()) {
				ContactEntity contactEntity = optional.get();

				String name = principal.getName();
				UserEntity userByUserName = repository.getUserByUserName(name);

				if (userByUserName.getId() == contactEntity.getUser().getId()) {
					// Delete photo
					File imageDirectory = new ClassPathResource("static/image").getFile();
					File deleteFile = new File(imageDirectory, contactEntity.getImage());
					if (deleteFile.exists()) {
						deleteFile.delete();
					}

					// Delete the contact
					contactRepository.delete(contactEntity);
				} else {
					System.out.println("User is not authorized to delete contact with id " + id);
				}
			} else {
				System.out.println("Contact with id " + id + " not found");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: " + ex.getMessage());
		}

		return "redirect:/user/showViewContacts/0";
	}

	// open update contact form
	@PostMapping("/update/{id}")
	public String updateContactForm(@PathVariable("id") Integer id, Model model, Principal principal) {
		try {
			Optional<ContactEntity> optional = contactRepository.findById(id);
			ContactEntity contactEntity = optional.get();

			String name = principal.getName();
			UserEntity userByUserName = repository.getUserByUserName(name);

			if (userByUserName.getId() == contactEntity.getUser().getId()) {
				contactRepository.findById(id);
				model.addAttribute("title",
						"Delite Id by " + contactEntity.getFname() + " " + contactEntity.getCname());
				model.addAttribute("contact", contactEntity);
			} else {
				System.out.println("Id is not invalid " + id);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error " + ex.getMessage());
		}
		return "normal/update_contact";
	}

	// update contact process form

	@PostMapping("/process-update")
	public String updateProcessForm(@ModelAttribute ContactEntity contact,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
		System.out.println("/process-update ur/.................");
		System.out.println(contact.getCid());

		try {

			ContactEntity oldContactDetails = this.contactRepository.findById(contact.getCid()).get();

			if (!file.isEmpty()) {
				// file photo update

				// delete old photo
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file1 = new File(deleteFile, oldContactDetails.getImage());
				file1.delete();

				// update new photo

				// Generate a unique file name based on current timestamp and original file name
				String filename = file.getOriginalFilename();
				String uniqFileName = System.currentTimeMillis() + "-" + filename;

				// get the folder path where file will be store
				File folder = new ClassPathResource("static/image").getFile();
				System.out.println("Folder Path to store image :=>  " + folder);

				// Resolve the file path where the image will be stored
				Path filePath = Paths.get(folder.getAbsolutePath(), uniqFileName);

				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(uniqFileName);

			} else if (file.isEmpty()) {
				contact.setImage("default.png");
			} else {
				contact.setImage(oldContactDetails.getImage());
			}

			UserEntity userName = repository.getUserByUserName(principal.getName());

			contact.setUserEntity(userName);
			contactRepository.save(contact);

			session.setAttribute("message", new MassageHelper("Your Contact is Updated", "success"));

		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Error " + ex.getMessage());
			session.setAttribute("message", new MassageHelper("Somthing went Wrong", "danger"));
		}
		return "redirect:/user/" + contact.getCid() + "/contactDetails";
	}

}
