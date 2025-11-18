package com.elie.quizapp.service;

import com.elie.quizapp.entity.Category;
import com.elie.quizapp.entity.Test;
import com.elie.quizapp.repository.CategoryRepository;
import com.elie.quizapp.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TestRepository testRepository;

    public CategoryService(CategoryRepository categoryRepository, TestRepository testRepository) {
        this.categoryRepository = categoryRepository;
        this.testRepository = testRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findByTest(Test test) {
        return categoryRepository.findByTestOrderByName(test);
    }

    public List<Category> findByTestId(Long testId) {
        return categoryRepository.findByTestIdOrderByName(testId);
    }

    public List<Category> findByTestIdWithQuestionsAndAnswers(Long testId) {
        return categoryRepository.findByTestIdWithQuestionsAndAnswers(testId);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> findByIdWithQuestions(Long id) {
        return categoryRepository.findByIdWithQuestions(id);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category create(String name, Long testId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test non trouvé"));
        
        if (categoryRepository.existsByNameAndTest(name, test)) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà dans ce test");
        }
        
        Category category = new Category(name, test);
        return categoryRepository.save(category);
    }

    public Category update(Long id, String name, Long testId) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test non trouvé"));
        
        if ((!category.getName().equals(name) || !category.getTest().getId().equals(testId)) 
            && categoryRepository.existsByNameAndTest(name, test)) {
            throw new RuntimeException("Une catégorie avec ce nom existe déjà dans ce test");
        }
        
        category.setName(name);
        category.setTest(test);
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        
        if (!category.getQuestions().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer une catégorie contenant des questions");
        }
        
        categoryRepository.delete(category);
    }

    public boolean existsByNameAndTest(String name, Test test) {
        return categoryRepository.existsByNameAndTest(name, test);
    }

    public int getQuestionCount(Long categoryId) {
        return categoryRepository.countQuestionsByCategoryId(categoryId);
    }

    public long getTotalCount() {
        return categoryRepository.count();
    }
}