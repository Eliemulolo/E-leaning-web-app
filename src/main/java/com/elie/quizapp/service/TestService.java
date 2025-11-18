package com.elie.quizapp.service;

import com.elie.quizapp.entity.Test;
import com.elie.quizapp.entity.Branch;
import com.elie.quizapp.repository.TestRepository;
import com.elie.quizapp.repository.BranchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestService {

    private final TestRepository testRepository;
    private final BranchRepository branchRepository;

    public TestService(TestRepository testRepository, BranchRepository branchRepository) {
        this.testRepository = testRepository;
        this.branchRepository = branchRepository;
    }

    public List<Test> findAll() {
        return testRepository.findAll();
    }

    public List<Test> findAllWithCategories() {
        return testRepository.findAllWithCategories();
    }

    public List<Test> findByBranch(Branch branch) {
        return testRepository.findByBranchOrderByName(branch);
    }

    public List<Test> findByBranchId(Long branchId) {
        return testRepository.findByBranchIdOrderByName(branchId);
    }

    public Optional<Test> findById(Long id) {
        return testRepository.findById(id);
    }

    public Optional<Test> findByIdWithCategories(Long id) {
        return testRepository.findByIdWithCategories(id);
    }

    public Optional<Test> findByIdWithFullDetails(Long id) {
        return testRepository.findByIdWithFullDetails(id);
    }

    public Test save(Test test) {
        return testRepository.save(test);
    }

    public Test create(String name, Long branchId) {
        Branch branch = branchRepository.findById(branchId)
            .orElseThrow(() -> new RuntimeException("Branche non trouvée"));
        
        if (testRepository.existsByNameAndBranch(name, branch)) {
            throw new RuntimeException("Un test avec ce nom existe déjà dans cette branche");
        }
        
        Test test = new Test(name, branch);
        return testRepository.save(test);
    }

    public Test update(Long id, String name, Long branchId) {
        Test test = testRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Test non trouvé"));
        
        Branch branch = branchRepository.findById(branchId)
            .orElseThrow(() -> new RuntimeException("Branche non trouvée"));
        
        if ((!test.getName().equals(name) || !test.getBranch().getId().equals(branchId)) 
            && testRepository.existsByNameAndBranch(name, branch)) {
            throw new RuntimeException("Un test avec ce nom existe déjà dans cette branche");
        }
        
        test.setName(name);
        test.setBranch(branch);
        return testRepository.save(test);
    }

    public void delete(Long id) {
        Test test = testRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Test non trouvé"));
        
        if (!test.getCategories().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer un test contenant des catégories");
        }
        
        testRepository.delete(test);
    }

    public boolean existsByNameAndBranch(String name, Branch branch) {
        return testRepository.existsByNameAndBranch(name, branch);
    }

    public int getQuestionCount(Long testId) {
        return testRepository.countQuestionsByTestId(testId);
    }

    public long getTotalCount() {
        return testRepository.count();
    }

    public boolean isTestReady(Long testId) {
        Test test = testRepository.findByIdWithFullDetails(testId)
            .orElseThrow(() -> new RuntimeException("Test non trouvé"));
        
        return test.getCategories().stream()
            .flatMap(category -> category.getQuestions().stream())
            .anyMatch(question -> question.getCorrectAnswer() != null && 
                     question.getAnswers().size() >= 2);
    }
}