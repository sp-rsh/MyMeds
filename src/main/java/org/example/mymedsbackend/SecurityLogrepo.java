package org.example.mymedsbackend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SecurityLogrepo extends JpaRepository<SecurityLog, Integer> {
    List<SecurityLog> findTop200ByOrderByEventTimeDesc();
    List<SecurityLog> findTop5ByOrderByEventTimeDesc();
}