package com.elie.quizapp.service;

import com.elie.quizapp.entity.*;
import com.elie.quizapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;

    public QuizService(TestRepository testRepository, QuestionRepository questionRepository,
                      QuizAttemptRepository quizAttemptRepository, UserAnswerRepository userAnswerRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
    }

    public List<Question> getQuestionsForQuiz(Long testId, boolean randomOrder) {
        if (randomOrder) {
            return questionRepository.findByTestIdRandomOrder(testId);
        }
        return questionRepository.findByTestIdWithAnswers(testId);
    }

    public QuizAttempt startQuiz(User user, Long testId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test non trouvé"));
        
        QuizAttempt attempt = new QuizAttempt(user, test);
        return quizAttemptRepository.save(attempt);
    }

    public QuizAttempt submitQuiz(Long attemptId, Map<Long, Long> answers) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new RuntimeException("Tentative de quiz non trouvée"));
        
        List<Question> questions = questionRepository.findByTestIdWithAnswers(attempt.getTest().getId());
        
        for (Question question : questions) {
            Long chosenAnswerId = answers.get(question.getId());
            Answer chosenAnswer = null;
            
            if (chosenAnswerId != null) {
                chosenAnswer = question.getAnswers().stream()
                    .filter(answer -> answer.getId().equals(chosenAnswerId))
                    .findFirst()
                    .orElse(null);
            }
            
            UserAnswer userAnswer = new UserAnswer(attempt, question, chosenAnswer);
            attempt.addUserAnswer(userAnswer);
        }
        
        attempt.calculateScore();
        return quizAttemptRepository.save(attempt);
    }

    public List<QuizAttempt> getUserQuizHistory(User user) {
        return quizAttemptRepository.findByUserOrderByAttemptDateDesc(user);
    }

    public List<QuizAttempt> getUserQuizHistoryForTest(User user, Test test) {
        return quizAttemptRepository.findByUserAndTestOrderByAttemptDateDesc(user, test);
    }

    public Optional<QuizAttempt> getQuizAttemptWithDetails(Long attemptId) {
        return quizAttemptRepository.findByIdWithUserAnswers(attemptId);
    }

    public QuizResultSummary getQuizResultSummary(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdWithUserAnswers(attemptId)
            .orElseThrow(() -> new RuntimeException("Tentative de quiz non trouvée"));
        
        List<Object[]> categoryStats = userAnswerRepository.getCategoryPerformanceByQuizAttemptId(attemptId);
        
        QuizResultSummary summary = new QuizResultSummary();
        summary.setAttempt(attempt);
        summary.setCategoryPerformances(new ArrayList<>());
        
        for (Object[] stat : categoryStats) {
            CategoryPerformance performance = new CategoryPerformance();
            performance.setCategoryName((String) stat[0]);
            performance.setTotalQuestions(((Number) stat[1]).intValue());
            performance.setCorrectAnswers(((Number) stat[2]).intValue());
            performance.setPercentage(((Number) stat[3]).doubleValue());
            
            summary.getCategoryPerformances().add(performance);
        }
        
        // Identifier les points forts et faibles
        List<CategoryPerformance> sorted = summary.getCategoryPerformances().stream()
            .sorted(Comparator.comparingDouble(CategoryPerformance::getPercentage).reversed())
            .collect(Collectors.toList());
        
        summary.setStrongPoints(sorted.stream()
            .filter(cp -> cp.getPercentage() >= 70)
            .collect(Collectors.toList()));
        
        summary.setWeakPoints(sorted.stream()
            .filter(cp -> cp.getPercentage() < 50)
            .collect(Collectors.toList()));
        
        return summary;
    }

    public Double getAverageScoreForTest(Long testId) {
        return quizAttemptRepository.getAverageScoreByTestId(testId);
    }

    public Double getAverageScoreForUser(Long userId) {
        return quizAttemptRepository.getAverageScoreByUserId(userId);
    }

    public List<Object[]> getTestStatistics() {
        return quizAttemptRepository.getTestStatistics();
    }

    public long getAttemptCountSince(LocalDateTime fromDate) {
        return quizAttemptRepository.countAttemptsSince(fromDate);
    }

    // Classes internes pour les résultats
    public static class QuizResultSummary {
        private QuizAttempt attempt;
        private List<CategoryPerformance> categoryPerformances;
        private List<CategoryPerformance> strongPoints;
        private List<CategoryPerformance> weakPoints;

        // Getters et setters
        public QuizAttempt getAttempt() { return attempt; }
        public void setAttempt(QuizAttempt attempt) { this.attempt = attempt; }
        
        public List<CategoryPerformance> getCategoryPerformances() { return categoryPerformances; }
        public void setCategoryPerformances(List<CategoryPerformance> categoryPerformances) { 
            this.categoryPerformances = categoryPerformances; 
        }
        
        public List<CategoryPerformance> getStrongPoints() { return strongPoints; }
        public void setStrongPoints(List<CategoryPerformance> strongPoints) { this.strongPoints = strongPoints; }
        
        public List<CategoryPerformance> getWeakPoints() { return weakPoints; }
        public void setWeakPoints(List<CategoryPerformance> weakPoints) { this.weakPoints = weakPoints; }
    }

    public static class CategoryPerformance {
        private String categoryName;
        private int totalQuestions;
        private int correctAnswers;
        private double percentage;

        // Getters et setters
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
        
        public int getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
        
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }
}