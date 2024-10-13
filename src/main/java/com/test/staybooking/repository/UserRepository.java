package com.test.staybooking.repository;


import com.test.staybooking.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {


   UserEntity findByUsername(String username);


   boolean existsByUsername(String username);
}
