package com.jdbctemplate.projections.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jdbctemplate.projections.demo.entities.User;
import com.jdbctemplate.projections.demo.manager.QueryHelper;

@Repository
public class UserRepository {

	@Autowired
	private QueryHelper queryHelper;

	public User save(User user) {
		long id = queryHelper.save(user).longValue();
		user.setId(id);
		return user;
	}

	public Optional<User> findById(long id, List<String> fields) {
		return queryHelper.findBy("id = ?", new Object[] { id }, fields, User.class);
	}

	public List<User> findByName(String name, List<String> fields) {
		return queryHelper.findAllBy("firstname = ?", new Object[] { name }, fields, User.class);
	}
}
