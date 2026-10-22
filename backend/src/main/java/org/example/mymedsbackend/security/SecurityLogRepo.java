package org.example.mymedsbackend.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SecurityLogRepo extends JpaRepository<SecurityLog, Integer> {
    List<SecurityLog> findTop200ByOrderByEventTimeDesc();
    List<SecurityLog> findTop5ByOrderByEventTimeDesc();
}