package com.elie.quizapp.repository;

import com.elie.quizapp.entity.Test;
import com.elie.quizapp.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    
    List<Test> findByBranchOrderByName(Branch branch);
    
    @Query("SELECT t FROM Test t WHERE t.branch.id = :branchId ORDER BY t.name")
    List<Test> findByBranchIdOrderByName(@Param("branchId") Long branchId);
    
    @Query("SELECT DISTINCT t FROM Test t LEFT JOIN FETCH t.categories ORDER BY t.name")
    List<Test> findAllWithCategories();
    
    @Query("SELECT t FROM Test t LEFT JOIN FETCH t.categories WHERE t.id = :id")
    Optional<Test> findByIdWithCategories(@Param("id") Long id);
    
    @Query("SELECT t FROM Test t " +
           "LEFT JOIN FETCH t.categories c " +
           "LEFT JOIN FETCH c.questions q " +
           "LEFT JOIN FETCH q.answers " +
           "WHERE t.id = :id")
    Optional<Test> findByIdWithFullDetails(@Param("id") Long id);
    
    boolean existsByNameAndBranch(String name, Branch branch);
    
    @Query("SELECT COUNT(q) FROM Test t JOIN t.categories c JOIN c.questions q WHERE t.id = :testId")
    int countQuestionsByTestId(@Param("testId") Long testId);
}