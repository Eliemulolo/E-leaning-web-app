package com.elie.quizapp.repository;

import com.elie.quizapp.entity.Category;
import com.elie.quizapp.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByTestOrderByName(Test test);
    
    @Query("SELECT c FROM Category c WHERE c.test.id = :testId ORDER BY c.name")
    List<Category> findByTestIdOrderByName(@Param("testId") Long testId);
    
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.questions WHERE c.id = :id")
    Optional<Category> findByIdWithQuestions(@Param("id") Long id);
    
    @Query("SELECT c FROM Category c " +
           "LEFT JOIN FETCH c.questions q " +
           "LEFT JOIN FETCH q.answers " +
           "WHERE c.test.id = :testId ORDER BY c.name")
    List<Category> findByTestIdWithQuestionsAndAnswers(@Param("testId") Long testId);
    
    boolean existsByNameAndTest(String name, Test test);
    
    @Query("SELECT COUNT(q) FROM Category c JOIN c.questions q WHERE c.id = :categoryId")
    int countQuestionsByCategoryId(@Param("categoryId") Long categoryId);
}