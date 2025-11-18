package com.elie.quizapp.service;

import com.elie.quizapp.entity.Question;
import com.elie.quizapp.entity.Answer;
import com.elie.quizapp.entity.Category;
import com.elie.quizapp.repository.QuestionRepository;
import com.elie.quizapp.repository.AnswerRepository;
import com.elie.quizapp.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;

    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository,
                          CategoryRepository categoryRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public List<Question> findByCategory(Category category) {
        return questionRepository.findByCategoryOrderById(category);
    }

    public List<Question> findByCategoryId(Long categoryId) {
        return questionRepository.findByCategoryIdOrderById(categoryId);
    }

    public List<Question> findByCategoryIdWithAnswers(Long categoryId) {
        return questionRepository.findByCategoryIdWithAnswers(categoryId);
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    public Optional<Question> findByIdWithAnswers(Long id) {
        return questionRepository.findByIdWithAnswers(id);
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public Question create(String text, String explanation, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        
        Question question = new Question(text, category);
        question.setExplanation(explanation);
        return questionRepository.save(question);
    }

    public Question update(Long id, String text, String explanation, Long categoryId) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Question non trouvée"));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        
        question.setText(text);
        question.setExplanation(explanation);
        question.setCategory(category);
        return questionRepository.save(question);
    }

    public void delete(Long id) {
        Question question = questionRepository.findByIdWithAnswers(id)
            .orElseThrow(() -> new RuntimeException("Question non trouvée"));
        
        // Supprimer d'abord toutes les réponses
        answerRepository.deleteByQuestion(question);
        
        // Puis supprimer la question
        questionRepository.delete(question);
    }

    public Answer addAnswer(Long questionId, String answerText) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question non trouvée"));
        
        Answer answer = new Answer(answerText, question);
        question.addAnswer(answer);
        return answerRepository.save(answer);
    }

    public Answer updateAnswer(Long answerId, String answerText) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("Réponse non trouvée"));
        
        answer.setText(answerText);
        return answerRepository.save(answer);
    }

    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("Réponse non trouvée"));
        
        Question question = answer.getQuestion();
        if (question.getCorrectAnswer() != null && question.getCorrectAnswer().equals(answer)) {
            question.setCorrectAnswer(null);
            questionRepository.save(question);
        }
        
        question.removeAnswer(answer);
        answerRepository.delete(answer);
    }

    public Question setCorrectAnswer(Long questionId, Long answerId) {
        Question question = questionRepository.findByIdWithAnswers(questionId)
            .orElseThrow(() -> new RuntimeException("Question non trouvée"));
        
        Answer correctAnswer = question.getAnswers().stream()
            .filter(answer -> answer.getId().equals(answerId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Réponse non trouvée"));
        
        question.setCorrectAnswer(correctAnswer);
        return questionRepository.save(question);
    }

    public List<Answer> getAnswersForQuestion(Long questionId) {
        return answerRepository.findByQuestionIdOrderById(questionId);
    }

    public int getAnswerCount(Long questionId) {
        return answerRepository.countByQuestionId(questionId);
    }

    public boolean isQuestionComplete(Long questionId) {
        Question question = questionRepository.findByIdWithAnswers(questionId)
            .orElse(null);
        
        return question != null && 
               question.getAnswers().size() >= 2 && 
               question.getCorrectAnswer() != null;
    }

    public long getTotalQuestionCount() {
        return questionRepository.count();
    }

    public int getQuestionCountByCategory(Long categoryId) {
        return questionRepository.countByCategoryId(categoryId);
    }

    public int getQuestionCountByTest(Long testId) {
        return questionRepository.countByTestId(testId);
    }
}