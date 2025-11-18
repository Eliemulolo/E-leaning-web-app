package com.elie.quizapp.repository;

import com.elie.quizapp.entity.UserAnswer;
import com.elie.quizapp.entity.QuizAttempt;
import com.elie.quizapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    
    List<UserAnswer> findByQuizAttemptOrderByQuestionId(QuizAttempt quizAttempt);
    
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.quizAttempt.id = :quizAttemptId ORDER BY ua.question.id")
    List<UserAnswer> findByQuizAttemptIdOrderByQuestionId(@Param("quizAttemptId") Long quizAttemptId);
    
    Optional<UserAnswer> findByQuizAttemptAndQuestion(QuizAttempt quizAttempt, Question question);
    
    @Query("SELECT ua FROM UserAnswer ua " +
           "LEFT JOIN FETCH ua.question q " +
           "LEFT JOIN FETCH ua.chosenAnswer " +
           "LEFT JOIN FETCH q.correctAnswer " +
           "WHERE ua.quizAttempt.id = :quizAttemptId " +
           "ORDER BY q.category.name, q.id")
    List<UserAnswer> findByQuizAttemptIdWithDetails(@Param("quizAttemptId") Long quizAttemptId);
    
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.quizAttempt.id = :quizAttemptId AND ua.isCorrect = true")
    int countCorrectAnswersByQuizAttemptId(@Param("quizAttemptId") Long quizAttemptId);
    
    @Query("SELECT ua.question.category.name, " +
           "COUNT(ua), " +
           "SUM(CASE WHEN ua.isCorrect = true THEN 1 ELSE 0 END), " +
           "AVG(CASE WHEN ua.isCorrect = true THEN 100.0 ELSE 0.0 END) " +
           "FROM UserAnswer ua " +
           "WHERE ua.quizAttempt.id = :quizAttemptId " +
           "GROUP BY ua.question.category.id, ua.question.category.name " +
           "ORDER BY ua.question.category.name")
    List<Object[]> getCategoryPerformanceByQuizAttemptId(@Param("quizAttemptId") Long quizAttemptId);
}