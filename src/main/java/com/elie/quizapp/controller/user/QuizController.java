package com.elie.quizapp.controller.user;

import com.elie.quizapp.entity.*;
import com.elie.quizapp.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final TestService testService;
    private final QuizService quizService;

    public QuizController(TestService testService, QuizService quizService) {
        this.testService = testService;
        this.quizService = quizService;
    }

    @GetMapping
    public String listAvailableTests(Model model) {
        model.addAttribute("tests", testService.findAllWithCategories());
        return "quiz/tests";
    }

    @GetMapping("/{testId}")
    public String viewTest(@PathVariable Long testId, Model model, RedirectAttributes redirectAttributes) {
        Test test = testService.findByIdWithFullDetails(testId).orElse(null);
        if (test == null) {
            redirectAttributes.addFlashAttribute("error", "Test non trouvé");
            return "redirect:/quiz";
        }
        
        if (!testService.isTestReady(testId)) {
            redirectAttributes.addFlashAttribute("error", "Ce test n'est pas encore prêt");
            return "redirect:/quiz";
        }
        
        model.addAttribute("test", test);
        model.addAttribute("totalQuestions", testService.getQuestionCount(testId));
        return "quiz/test-info";
    }

    @PostMapping("/{testId}/start")
    public String startQuiz(@PathVariable Long testId, Authentication authentication, 
                           @RequestParam(defaultValue = "false") boolean randomOrder,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            QuizAttempt attempt = quizService.startQuiz(currentUser, testId);
            
            return "redirect:/quiz/attempt/" + attempt.getId() + "?random=" + randomOrder;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/quiz/" + testId;
        }
    }

    @GetMapping("/attempt/{attemptId}")
    public String takeQuiz(@PathVariable Long attemptId, 
                          @RequestParam(defaultValue = "false") boolean random,
                          @RequestParam(defaultValue = "0") int currentQuestion,
                          Model model, RedirectAttributes redirectAttributes) {
        try {
            QuizAttempt attempt = quizService.getQuizAttemptWithDetails(attemptId).orElse(null);
            if (attempt == null) {
                redirectAttributes.addFlashAttribute("error", "Tentative de quiz non trouvée");
                return "redirect:/quiz";
            }
            
            List<Question> questions = quizService.getQuestionsForQuiz(attempt.getTest().getId(), random);
            
            if (currentQuestion >= questions.size()) {
                return "redirect:/quiz/attempt/" + attemptId + "/submit";
            }
            
            Question question = questions.get(currentQuestion);
            
            model.addAttribute("attempt", attempt);
            model.addAttribute("question", question);
            model.addAttribute("questions", questions);
            model.addAttribute("currentQuestion", currentQuestion);
            model.addAttribute("totalQuestions", questions.size());
            model.addAttribute("random", random);
            
            return "quiz/take-quiz";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement du quiz");
            return "redirect:/quiz";
        }
    }

    @PostMapping("/attempt/{attemptId}/answer")
    public String saveAnswer(@PathVariable Long attemptId,
                            @RequestParam Long questionId,
                            @RequestParam(required = false) Long answerId,
                            @RequestParam int currentQuestion,
                            @RequestParam(defaultValue = "false") boolean random,
                            RedirectAttributes redirectAttributes) {
        
        // Sauvegarder la réponse dans la session ou base de données temporaire
        // Pour simplifier, on passe directement à la question suivante
        
        int nextQuestion = currentQuestion + 1;
        return "redirect:/quiz/attempt/" + attemptId + "?random=" + random + "&currentQuestion=" + nextQuestion;
    }

    @GetMapping("/attempt/{attemptId}/submit")
    public String showSubmitPage(@PathVariable Long attemptId, Model model, RedirectAttributes redirectAttributes) {
        try {
            QuizAttempt attempt = quizService.getQuizAttemptWithDetails(attemptId).orElse(null);
            if (attempt == null) {
                redirectAttributes.addFlashAttribute("error", "Tentative de quiz non trouvée");
                return "redirect:/quiz";
            }
            
            model.addAttribute("attempt", attempt);
            model.addAttribute("test", attempt.getTest());
            
            return "quiz/submit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement de la page de soumission");
            return "redirect:/quiz";
        }
    }

    @PostMapping("/attempt/{attemptId}/submit")
    public String submitQuiz(@PathVariable Long attemptId, 
                            @RequestParam Map<String, String> answers,
                            RedirectAttributes redirectAttributes) {
        try {
            // Convertir les réponses du formulaire en Map<Long, Long>
            Map<Long, Long> questionAnswers = new HashMap<>();
            
            for (Map.Entry<String, String> entry : answers.entrySet()) {
                if (entry.getKey().startsWith("answer_")) {
                    String questionIdStr = entry.getKey().substring(7); // Enlever "answer_"
                    Long questionId = Long.parseLong(questionIdStr);
                    Long answerId = entry.getValue() != null && !entry.getValue().isEmpty() 
                                   ? Long.parseLong(entry.getValue()) : null;
                    questionAnswers.put(questionId, answerId);
                }
            }
            
            QuizAttempt completedAttempt = quizService.submitQuiz(attemptId, questionAnswers);
            redirectAttributes.addFlashAttribute("success", "Quiz terminé avec succès!");
            
            return "redirect:/quiz/results/" + completedAttempt.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la soumission: " + e.getMessage());
            return "redirect:/quiz/attempt/" + attemptId + "/submit";
        }
    }

    @GetMapping("/results/{attemptId}")
    public String viewResults(@PathVariable Long attemptId, Model model, RedirectAttributes redirectAttributes) {
        try {
            QuizService.QuizResultSummary summary = quizService.getQuizResultSummary(attemptId);
            
            model.addAttribute("summary", summary);
            model.addAttribute("attempt", summary.getAttempt());
            model.addAttribute("categoryPerformances", summary.getCategoryPerformances());
            model.addAttribute("strongPoints", summary.getStrongPoints());
            model.addAttribute("weakPoints", summary.getWeakPoints());
            
            return "quiz/results";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement des résultats");
            return "redirect:/quiz";
        }
    }

    @GetMapping("/results/{attemptId}/detailed")
    public String viewDetailedResults(@PathVariable Long attemptId, Model model, RedirectAttributes redirectAttributes) {
        try {
            QuizAttempt attempt = quizService.getQuizAttemptWithDetails(attemptId).orElse(null);
            if (attempt == null) {
                redirectAttributes.addFlashAttribute("error", "Tentative de quiz non trouvée");
                return "redirect:/quiz";
            }
            
            model.addAttribute("attempt", attempt);
            model.addAttribute("userAnswers", attempt.getUserAnswers());
            
            return "quiz/detailed-results";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement des résultats détaillés");
            return "redirect:/quiz/results/" + attemptId;
        }
    }
}