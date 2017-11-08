package com.weatherapp.repository;

import com.weatherapp.model.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUserId(final long userId);

    User findByUsername(final String username);

    User findByEmail(final String email);
}
