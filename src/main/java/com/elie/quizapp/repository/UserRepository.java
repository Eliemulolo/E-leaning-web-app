package com.elie.quizapp.repository;

import com.elie.quizapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.isAdmin = true ORDER BY u.username")
    List<User> findAllAdmins();
    
    @Query("SELECT u FROM User u WHERE u.isAdmin = false ORDER BY u.username")
    List<User> findAllRegularUsers();
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.quizAttempts WHERE u.id = :id")
    Optional<User> findByIdWithQuizAttempts(@Param("id") Long id);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isAdmin = false")
    long countRegularUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isAdmin = true")
    long countAdmins();
}