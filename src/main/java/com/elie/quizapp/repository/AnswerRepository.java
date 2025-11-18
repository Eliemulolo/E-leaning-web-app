package com.elie.quizapp.repository;

import com.elie.quizapp.entity.Answer;
import com.elie.quizapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
    List<Answer> findByQuestionOrderById(Question question);
    
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId ORDER BY a.id")
    List<Answer> findByQuestionIdOrderById(@Param("questionId") Long questionId);
    
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId")
    int countByQuestionId(@Param("questionId") Long questionId);
    
    void deleteByQuestion(Question question);
}