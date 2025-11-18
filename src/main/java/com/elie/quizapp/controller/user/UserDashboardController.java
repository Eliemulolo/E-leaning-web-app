package com.elie.quizapp.controller.user;

import com.elie.quizapp.entity.User;
import com.elie.quizapp.service.TestService;
import com.elie.quizapp.service.QuizService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final TestService testService;
    private final QuizService quizService;

    public UserDashboardController(TestService testService, QuizService quizService) {
        this.testService = testService;
        this.quizService = quizService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Tests disponibles
        model.addAttribute("availableTests", testService.findAllWithCategories());
        
        // Historique des tentatives récentes
        model.addAttribute("recentAttempts", quizService.getUserQuizHistory(currentUser)
            .stream().limit(5).toList());
        
        // Statistiques personnelles
        Double averageScore = quizService.getAverageScoreForUser(currentUser.getId());
        model.addAttribute("userAverageScore", averageScore != null ? averageScore : 0.0);
        
        model.addAttribute("totalAttempts", quizService.getUserQuizHistory(currentUser).size());
        
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        
        model.addAttribute("user", currentUser);
        model.addAttribute("quizHistory", quizService.getUserQuizHistory(currentUser));
        
        Double averageScore = quizService.getAverageScoreForUser(currentUser.getId());
        model.addAttribute("averageScore", averageScore != null ? averageScore : 0.0);
        
        return "user/profile";
    }
}