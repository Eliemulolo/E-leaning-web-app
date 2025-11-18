package com.elie.quizapp.repository;

import com.elie.quizapp.entity.Question;
import com.elie.quizapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByCategoryOrderById(Category category);
    
    @Query("SELECT q FROM Question q WHERE q.category.id = :categoryId ORDER BY q.id")
    List<Question> findByCategoryIdOrderById(@Param("categoryId") Long categoryId);
    
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answers WHERE q.id = :id")
    Optional<Question> findByIdWithAnswers(@Param("id") Long id);
    
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN FETCH q.answers " +
           "LEFT JOIN FETCH q.correctAnswer " +
           "WHERE q.category.id = :categoryId ORDER BY q.id")
    List<Question> findByCategoryIdWithAnswers(@Param("categoryId") Long categoryId);
    
    @Query("SELECT q FROM Question q " +
           "LEFT JOIN FETCH q.answers " +
           "LEFT JOIN FETCH q.correctAnswer " +
           "WHERE q.category.test.id = :testId ORDER BY q.category.name, q.id")
    List<Question> findByTestIdWithAnswers(@Param("testId") Long testId);
    
    @Query("SELECT q FROM Question q WHERE q.category.test.id = :testId ORDER BY RAND()")
    List<Question> findByTestIdRandomOrder(@Param("testId") Long testId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.category.id = :categoryId")
    int countByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.category.test.id = :testId")
    int countByTestId(@Param("testId") Long testId);
}