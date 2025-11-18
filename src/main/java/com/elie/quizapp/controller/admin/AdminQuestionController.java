package com.elie.quizapp.controller.admin;

import com.elie.quizapp.entity.Question;
import com.elie.quizapp.service.QuestionService;
import com.elie.quizapp.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;
    private final CategoryService categoryService;

    public AdminQuestionController(QuestionService questionService, CategoryService categoryService) {
        this.questionService = questionService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listQuestions(Model model) {
        model.addAttribute("questions", questionService.findAll());
        return "admin/questions/list";
    }

    @GetMapping("/category/{categoryId}")
    public String listQuestionsByCategory(@PathVariable Long categoryId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("questions", questionService.findByCategoryIdWithAnswers(categoryId));
            model.addAttribute("category", categoryService.findById(categoryId).orElse(null));
            return "admin/questions/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement des questions");
            return "redirect:/admin/categories";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long categoryId, Model model) {
        model.addAttribute("question", new Question());
        if (categoryId != null) {
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("selectedCategory", categoryService.findById(categoryId).orElse(null));
        }
        model.addAttribute("categories", categoryService.findAll());
        return "admin/questions/form";
    }

    @PostMapping
    public String createQuestion(@RequestParam String text, @RequestParam String explanation,
                                @RequestParam Long categoryId, RedirectAttributes redirectAttributes) {
        try {
            Question question = questionService.create(text, explanation, categoryId);
            redirectAttributes.addFlashAttribute("success", "Question créée avec succès");
            return "redirect:/admin/questions/" + question.getId() + "/edit-answers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/questions/new?categoryId=" + categoryId;
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Question question = questionService.findById(id).orElse(null);
        if (question == null) {
            redirectAttributes.addFlashAttribute("error", "Question non trouvée");
            return "redirect:/admin/questions";
        }
        model.addAttribute("question", question);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/questions/form";
    }

    @PostMapping("/{id}")
    public String updateQuestion(@PathVariable Long id, @RequestParam String text,
                                @RequestParam String explanation, @RequestParam Long categoryId,
                                RedirectAttributes redirectAttributes) {
        try {
            Question question = questionService.update(id, text, explanation, categoryId);
            redirectAttributes.addFlashAttribute("success", "Question modifiée avec succès");
            return "redirect:/admin/questions/category/" + question.getCategory().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/questions/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Question question = questionService.findById(id).orElse(null);
            Long categoryId = question != null ? question.getCategory().getId() : null;
            questionService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Question supprimée avec succès");
            if (categoryId != null) {
                return "redirect:/admin/questions/category/" + categoryId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/questions";
    }

    @GetMapping("/{id}")
    public String viewQuestion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Question question = questionService.findByIdWithAnswers(id).orElse(null);
        if (question == null) {
            redirectAttributes.addFlashAttribute("error", "Question non trouvée");
            return "redirect:/admin/questions";
        }
        model.addAttribute("question", question);
        model.addAttribute("isComplete", questionService.isQuestionComplete(id));
        return "admin/questions/view";
    }

    // Gestion des réponses
    @GetMapping("/{id}/edit-answers")
    public String editAnswers(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Question question = questionService.findByIdWithAnswers(id).orElse(null);
        if (question == null) {
            redirectAttributes.addFlashAttribute("error", "Question non trouvée");
            return "redirect:/admin/questions";
        }
        model.addAttribute("question", question);
        model.addAttribute("answers", questionService.getAnswersForQuestion(id));
        return "admin/questions/edit-answers";
    }

    @PostMapping("/{questionId}/answers")
    public String addAnswer(@PathVariable Long questionId, @RequestParam String answerText,
                           RedirectAttributes redirectAttributes) {
        try {
            questionService.addAnswer(questionId, answerText);
            redirectAttributes.addFlashAttribute("success", "Réponse ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/questions/" + questionId + "/edit-answers";
    }

    @PostMapping("/answers/{answerId}/update")
    public String updateAnswer(@PathVariable Long answerId, @RequestParam String answerText,
                              @RequestParam Long questionId, RedirectAttributes redirectAttributes) {
        try {
            questionService.updateAnswer(answerId, answerText);
            redirectAttributes.addFlashAttribute("success", "Réponse modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/questions/" + questionId + "/edit-answers";
    }

    @PostMapping("/answers/{answerId}/delete")
    public String deleteAnswer(@PathVariable Long answerId, @RequestParam Long questionId,
                              RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteAnswer(answerId);
            redirectAttributes.addFlashAttribute("success", "Réponse supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/questions/" + questionId + "/edit-answers";
    }

    @PostMapping("/{questionId}/set-correct-answer")
    public String setCorrectAnswer(@PathVariable Long questionId, @RequestParam Long answerId,
                                  RedirectAttributes redirectAttributes) {
        try {
            questionService.setCorrectAnswer(questionId, answerId);
            redirectAttributes.addFlashAttribute("success", "Réponse correcte définie avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/questions/" + questionId + "/edit-answers";
    }
}