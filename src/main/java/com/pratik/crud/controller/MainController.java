package com.pratik.crud.controller;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pratik.crud.model.User;
import com.pratik.crud.userRepository.UserRepository;

@RestController
public class MainController  {
	
	@Autowired
	UserRepository userRepo;
	
	@GetMapping("/all")
	public Iterable<User> getAll() {
	    // This returns a JSON or XML with the users
	    return userRepo.findAll();
	  }
	
	@PostMapping(path= "/reg", consumes = "application/json", produces = "application/json")
	public  String addNewUser (@RequestBody User user) 
	{
		// Build a fresh entity and copy only client-settable fields so the
		// client-supplied id is never honoured (prevents overwriting existing rows).
		User u=new User();
		u.setName(user.getName());
		u.setSal(user.getSal());
		System.out.println(u);
		userRepo.save(u);
		
		return "Data successfully saved in Database";
		
	}
	
	//@PutMapping(path= "/update", consumes = "application/json", produces = "application/json")
	@PutMapping("/update")
	public  String updateData (@RequestBody User user) 
	{
		Optional<User> u=userRepo.findById(user.getId());
//		u.setName(name);
//		u.setSal(sal);
		System.out.println(u);
		if(u.isPresent())
		{
		System.out.println(u);
		userRepo.save(user);
		return "Data updated successfully saved in Database";
		}
		else
		{
			
			return "Data not exist in Database";
		}
	
	}

	@DeleteMapping(path= "/delete", consumes = "application/json", produces = "application/json")
	public  String deleteData (@RequestParam int id ) 
	{
		//User u=(User)userRepo.findAllById(id);
//		u.setName(name);
//		u.setSal(sal);
		//System.out.println(u);
		
		//userRepo.save(u);
		userRepo.deleteById(id);
		
		return "Data Deleted ";
		
	}
	

	
	
	

}
