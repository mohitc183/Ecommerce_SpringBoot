package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserNamer(String username);
}
