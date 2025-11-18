package com.elie.quizapp.service;

import com.elie.quizapp.entity.Branch;
import com.elie.quizapp.repository.BranchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> findAll() {
        return branchRepository.findAllOrderByName();
    }

    public List<Branch> findAllWithTests() {
        return branchRepository.findAllWithTests();
    }

    public Optional<Branch> findById(Long id) {
        return branchRepository.findById(id);
    }

    public Optional<Branch> findByName(String name) {
        return branchRepository.findByName(name);
    }

    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    public Branch create(String name) {
        if (branchRepository.existsByName(name)) {
            throw new RuntimeException("Une branche avec ce nom existe déjà");
        }
        Branch branch = new Branch(name);
        return branchRepository.save(branch);
    }

    public Branch update(Long id, String name) {
        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Branche non trouvée"));
        
        if (!branch.getName().equals(name) && branchRepository.existsByName(name)) {
            throw new RuntimeException("Une branche avec ce nom existe déjà");
        }
        
        branch.setName(name);
        return branchRepository.save(branch);
    }

    public void delete(Long id) {
        Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Branche non trouvée"));
        
        if (!branch.getTests().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer une branche contenant des tests");
        }
        
        branchRepository.delete(branch);
    }

    public boolean existsByName(String name) {
        return branchRepository.existsByName(name);
    }

    public long getTotalCount() {
        return branchRepository.count();
    }
}