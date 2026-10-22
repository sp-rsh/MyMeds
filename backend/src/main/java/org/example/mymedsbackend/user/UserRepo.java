package org.example.mymedsbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer>{
    User findByUsername(String username);
    List<User> findByRole(String role);
    List<User> findAllByOrderByUsernameAsc();
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
}
