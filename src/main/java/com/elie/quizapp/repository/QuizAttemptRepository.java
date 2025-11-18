package com.elie.quizapp.repository;

import com.elie.quizapp.entity.QuizAttempt;
import com.elie.quizapp.entity.User;
import com.elie.quizapp.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    List<QuizAttempt> findByUserOrderByAttemptDateDesc(User user);
    
    List<QuizAttempt> findByTestOrderByAttemptDateDesc(Test test);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId ORDER BY qa.attemptDate DESC")
    List<QuizAttempt> findByUserIdOrderByAttemptDateDesc(@Param("userId") Long userId);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.test.id = :testId ORDER BY qa.attemptDate DESC")
    List<QuizAttempt> findByTestIdOrderByAttemptDateDesc(@Param("testId") Long testId);
    
    @Query("SELECT qa FROM QuizAttempt qa " +
           "LEFT JOIN FETCH qa.userAnswers ua " +
           "LEFT JOIN FETCH ua.question " +
           "LEFT JOIN FETCH ua.chosenAnswer " +
           "WHERE qa.id = :id")
    Optional<QuizAttempt> findByIdWithUserAnswers(@Param("id") Long id);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user AND qa.test = :test ORDER BY qa.attemptDate DESC")
    List<QuizAttempt> findByUserAndTestOrderByAttemptDateDesc(@Param("user") User user, @Param("test") Test test);
    
    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.test.id = :testId")
    Double getAverageScoreByTestId(@Param("testId") Long testId);
    
    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.user.id = :userId")
    Double getAverageScoreByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.attemptDate >= :fromDate")
    long countAttemptsSince(@Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT qa.test.name, COUNT(qa), AVG(qa.score) " +
           "FROM QuizAttempt qa " +
           "GROUP BY qa.test.id, qa.test.name " +
           "ORDER BY COUNT(qa) DESC")
    List<Object[]> getTestStatistics();
}