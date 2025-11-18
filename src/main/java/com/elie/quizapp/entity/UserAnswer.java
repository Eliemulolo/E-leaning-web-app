package com.elie.quizapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_answers")
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chosen_answer_id")
    private Answer chosenAnswer;
    
    @Column(nullable = false)
    private boolean isCorrect = false;
    
    // Constructeurs
    public UserAnswer() {}
    
    public UserAnswer(QuizAttempt quizAttempt, Question question, Answer chosenAnswer) {
        this.quizAttempt = quizAttempt;
        this.question = question;
        this.chosenAnswer = chosenAnswer;
        this.isCorrect = chosenAnswer != null && chosenAnswer.equals(question.getCorrectAnswer());
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }
    
    public void setQuizAttempt(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public Answer getChosenAnswer() {
        return chosenAnswer;
    }
    
    public void setChosenAnswer(Answer chosenAnswer) {
        this.chosenAnswer = chosenAnswer;
        this.isCorrect = chosenAnswer != null && question != null && 
                        chosenAnswer.equals(question.getCorrectAnswer());
    }
    
    public boolean isCorrect() {
        return isCorrect;
    }
    
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
    
    // Méthodes utilitaires
    public Answer getCorrectAnswer() {
        return question != null ? question.getCorrectAnswer() : null;
    }
    
    public boolean wasAnswered() {
        return chosenAnswer != null;
    }
}