package com.elie.quizapp.controller.admin;

import com.elie.quizapp.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final BranchService branchService;
    private final TestService testService;
    private final CategoryService categoryService;
    private final QuestionService questionService;
    private final UserService userService;
    private final QuizService quizService;

    public AdminDashboardController(BranchService branchService, TestService testService,
                                   CategoryService categoryService, QuestionService questionService,
                                   UserService userService, QuizService quizService) {
        this.branchService = branchService;
        this.testService = testService;
        this.categoryService = categoryService;
        this.questionService = questionService;
        this.userService = userService;
        this.quizService = quizService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Statistiques générales
        model.addAttribute("totalBranches", branchService.getTotalCount());
        model.addAttribute("totalTests", testService.getTotalCount());
        model.addAttribute("totalCategories", categoryService.getTotalCount());
        model.addAttribute("totalQuestions", questionService.getTotalQuestionCount());
        model.addAttribute("totalUsers", userService.getRegularUserCount());
        model.addAttribute("totalAdmins", userService.getAdminCount());

        // Statistiques des quiz de la semaine dernière
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        model.addAttribute("weeklyAttempts", quizService.getAttemptCountSince(weekAgo));

        // Statistiques des tests les plus populaires
        model.addAttribute("testStatistics", quizService.getTestStatistics());

        // Branches avec leurs tests
        model.addAttribute("branches", branchService.findAllWithTests());

        return "admin/dashboard";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        // Page dédiée aux statistiques détaillées
        model.addAttribute("testStatistics", quizService.getTestStatistics());
        
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        model.addAttribute("monthlyAttempts", quizService.getAttemptCountSince(monthAgo));
        
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        model.addAttribute("weeklyAttempts", quizService.getAttemptCountSince(weekAgo));

        return "admin/statistics";
    }
}