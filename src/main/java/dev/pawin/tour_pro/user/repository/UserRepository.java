package dev.pawin.tour_pro.user.repository;

import org.springframework.data.repository.CrudRepository;

import dev.pawin.tour_pro.user.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    
}
