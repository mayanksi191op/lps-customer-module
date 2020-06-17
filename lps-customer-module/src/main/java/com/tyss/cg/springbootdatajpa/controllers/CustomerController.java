package com.tyss.cg.springbootdatajpa.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tyss.cg.springbootdatajpa.entity.Applyloan;
import com.tyss.cg.springbootdatajpa.entity.LoanPrograms;
import com.tyss.cg.springbootdatajpa.entity.User;
import com.tyss.cg.springbootdatajpa.exception.EntryAlreadyExistsException;
import com.tyss.cg.springbootdatajpa.exception.UserNotFoundException;
import com.tyss.cg.springbootdatajpa.response.Response;
import com.tyss.cg.springbootdatajpa.services.ApplyLoanServices;
import com.tyss.cg.springbootdatajpa.services.LoanProgramsServices;
import com.tyss.cg.springbootdatajpa.services.UserServices;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CustomerController {
	
	@Autowired
	private LoanProgramsServices loanProgramsServices;
	
	@Autowired
	private ApplyLoanServices service;
	
	@Autowired
	private UserServices userServices;
	
	//Loan Programs
	@GetMapping("/loanprograms")
	public Response<List<LoanPrograms>> findAll() {
		List<LoanPrograms> lists = loanProgramsServices.findAll();
		return new Response<>(false, "list retrieved", lists);
	}
	
	@GetMapping("/loanprograms/{pageNo}/{itemsPerPage}")
	public Page<LoanPrograms> getLoans(@PathVariable int pageNo, @PathVariable int itemsPerPage){
		return loanProgramsServices.getLoans(pageNo, itemsPerPage);
	}
	
	@GetMapping("/loanprograms/{pageNo}/{itemsPerPage}/{fieldname}")
	public Page<LoanPrograms> getLoans(@PathVariable int pageNo, @PathVariable int itemsPerPage, @PathVariable String fieldname){
		return loanProgramsServices.getSortLoans(pageNo, itemsPerPage, fieldname);
	}
	
	
	//applications
	//recheck the response
		@PostMapping("/makeloan/{email}")
		public Response<Applyloan> makeLoan(@Valid @PathVariable String email, @RequestBody Applyloan applyloan){
			User user = userServices.getByEmail(email);
			applyloan.setUser(user);
			applyloan.setStatus("Requested");
			service.saveApplication(applyloan);
			if (user == null) {
				throw new UserNotFoundException("User not found!!!");
			} else {
				return new Response<Applyloan>(false, "Application saved", applyloan);
			}
		}
	
	
	//User
		@GetMapping("/user/{email}")
		public Response<User> getById(@PathVariable String email) {
			User user = userServices.getByEmail(email);

			if (user == null) {
				throw new UserNotFoundException("User not found!!!");
			}else {
				return new Response<User>(false, "User found!!!", user);
			}
		}

			@GetMapping("/customers/{userid}")
			public Response<User> getByIdSustomer(@PathVariable int userid) {
				User user = userServices.getById(userid);
				
				if (user == null) {
					throw new UserNotFoundException("User not found!!!");
				}else {
					return new Response<User>(false, "User found!!!", user);
				}
			}

		@PostMapping("/customers")
		public Response<User> saveCustomer(@Valid @RequestBody User user) {
			user.setRole("ROLE_CUSTOMER");
			user.setPassword("Qwerty@123");
			try {
				userServices.saveUser(user);
				return new Response<User>(false, "Customer added successfuly.", user);
			} catch (Exception e) {
				throw new EntryAlreadyExistsException("User already exist!!!");
			}
		}

		@PutMapping("/customers/password/put")
		public Response<User> putCustomerPassword(@RequestBody User user){
			User user2= userServices.getByEmail(user.getEmail());
			if (user2 == null) {
				throw new UserNotFoundException("User not found!!!");
			} else {
				user2.setPassword(user.getPassword());
				userServices.updatePassword(user2);
				return new Response<User>(false, "Password updated sucessfully!!!", user2);
			}
		}
		
//		@PutMapping("/customers/putpassword")
//		public Response<User> putCustomerPassword2(@RequestBody User user){
//			User user2= userServices.getByEmail(user.getEmail());
//			if (user2 == null) {
//				throw new UserNotFoundException("User not found!!!");
//			} else {
//				user2.setPassword(user.getPassword());
//				userServices.updatePassword(user2);
//				return new Response<User>(false, "Password updated sucessfully!!!", user2);
//			}
//		}
		
		@PutMapping("/customers/put")
		public Response<User> putCustomerDetails(@Valid @RequestBody User user){
			User user2= userServices.getById(user.getUserid());
			if (user2 == null) {
				throw new UserNotFoundException("User not found!!!");
			} else {
				user.setPassword(user2.getPassword());
				userServices.updateUser(user);
				return new Response<User>(false, "Details updated sucessfully!!!", user);
			}
		}
		
		////recheck response
		@GetMapping("/customers/application/{email}")
		public Response<User> getCustomerApplications(@PathVariable String email){
			User user = userServices.getByEmail(email);
			if (userServices.getByEmail(email) == null) {
				throw new UserNotFoundException("User not found!!!");
			} else {
				return new Response<User>(true, "Applications Found", user);
			}
		}

}
