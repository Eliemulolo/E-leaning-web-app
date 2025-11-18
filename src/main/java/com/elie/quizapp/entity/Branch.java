package com.elie.quizapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom de la branche est requis")
    @Column(nullable = false, unique = true)
    private String name;
    
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();
    
    // Constructeurs
    public Branch() {}
    
    public Branch(String name) {
        this.name = name;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Test> getTests() {
        return tests;
    }
    
    public void setTests(List<Test> tests) {
        this.tests = tests;
    }
    
    // Méthodes utilitaires
    public void addTest(Test test) {
        tests.add(test);
        test.setBranch(this);
    }
    
    public void removeTest(Test test) {
        tests.remove(test);
        test.setBranch(null);
    }
}