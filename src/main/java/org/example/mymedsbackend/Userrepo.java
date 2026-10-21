package org.example.mymedsbackend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Userrepo extends JpaRepository<Credentials, Integer>{
    Credentials findByUsername(String username);
    List<Credentials> findByRole(String role);
    List<Credentials> findAllByOrderByUsernameAsc();
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
}
