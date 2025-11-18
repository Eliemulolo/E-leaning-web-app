package com.elie.quizapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;
    
    @Column(nullable = false)
    private double score;
    
    @Column(nullable = false)
    private LocalDateTime attemptDate;
    
    @Column(nullable = false)
    private int totalQuestions;
    
    @Column(nullable = false)
    private int correctAnswers;
    
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> userAnswers = new ArrayList<>();
    
    // Constructeurs
    public QuizAttempt() {
        this.attemptDate = LocalDateTime.now();
    }
    
    public QuizAttempt(User user, Test test) {
        this();
        this.user = user;
        this.test = test;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Test getTest() {
        return test;
    }
    
    public void setTest(Test test) {
        this.test = test;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }
    
    public void setAttemptDate(LocalDateTime attemptDate) {
        this.attemptDate = attemptDate;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    
    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }
    
    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }
    
    // Méthodes utilitaires
    public void addUserAnswer(UserAnswer userAnswer) {
        userAnswers.add(userAnswer);
        userAnswer.setQuizAttempt(this);
    }
    
    public void calculateScore() {
        this.totalQuestions = userAnswers.size();
        this.correctAnswers = (int) userAnswers.stream()
                .filter(UserAnswer::isCorrect)
                .count();
        this.score = totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;
    }
    
    public String getScorePercentage() {
        return String.format("%.1f%%", score);
    }
}