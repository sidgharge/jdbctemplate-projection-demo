package com.jdbctemplate.projections.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jdbctemplate.projections.demo.entities.User;
import com.jdbctemplate.projections.demo.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping
	public User addUser(@RequestBody User user) {
		return userService.addUser(user);
	}
	
	@PostMapping("/get/{id}")
	public User getById(@PathVariable Long id, @RequestBody List<String> fields) {
		return userService.getById(id, fields);
	}
	
	@PostMapping("/getbyname/{name}")
	public List<User> getByName(@PathVariable String name, @RequestBody List<String> fields) {
		return userService.getByName(name, fields);
	}
}
