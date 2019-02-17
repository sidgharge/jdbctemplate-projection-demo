package com.jdbctemplate.projections.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jdbctemplate.projections.demo.entities.User;
import com.jdbctemplate.projections.demo.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User addUser(User user) {
		return userRepository.save(user);
	}

	public User getById(Long id, List<String> fields) {
		return userRepository.findById(id, fields).get();
	}

	public List<User> getByName(String name, List<String> fields) {
		return userRepository.findByName(name, fields);
	}
	
}
