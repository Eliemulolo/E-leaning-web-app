package com.elie.quizapp.repository;

import com.elie.quizapp.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    
    Optional<Branch> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT b FROM Branch b LEFT JOIN FETCH b.tests ORDER BY b.name")
    List<Branch> findAllWithTests();
    
    @Query("SELECT b FROM Branch b ORDER BY b.name")
    List<Branch> findAllOrderByName();
}