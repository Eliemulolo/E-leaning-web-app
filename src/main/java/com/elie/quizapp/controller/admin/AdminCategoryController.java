package com.elie.quizapp.controller.admin;

import com.elie.quizapp.entity.Category;
import com.elie.quizapp.service.CategoryService;
import com.elie.quizapp.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final TestService testService;

    public AdminCategoryController(CategoryService categoryService, TestService testService) {
        this.categoryService = categoryService;
        this.testService = testService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/categories/list";
    }

    @GetMapping("/test/{testId}")
    public String listCategoriesByTest(@PathVariable Long testId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("categories", categoryService.findByTestIdWithQuestionsAndAnswers(testId));
            model.addAttribute("test", testService.findById(testId).orElse(null));
            return "admin/categories/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement des catégories");
            return "redirect:/admin/tests";
        }
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long testId, Model model) {
        model.addAttribute("category", new Category());
        if (testId != null) {
            model.addAttribute("selectedTestId", testId);
            model.addAttribute("selectedTest", testService.findById(testId).orElse(null));
        }
        model.addAttribute("tests", testService.findAll());
        return "admin/categories/form";
    }

    @PostMapping
    public String createCategory(@RequestParam String name, @RequestParam Long testId, 
                                RedirectAttributes redirectAttributes) {
        try {
            categoryService.create(name, testId);
            redirectAttributes.addFlashAttribute("success", "Catégorie créée avec succès");
            return "redirect:/admin/categories/test/" + testId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/categories/new?testId=" + testId;
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Category category = categoryService.findById(id).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", "Catégorie non trouvée");
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        model.addAttribute("tests", testService.findAll());
        return "admin/categories/form";
    }

    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id, @RequestParam String name, 
                                @RequestParam Long testId, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.update(id, name, testId);
            redirectAttributes.addFlashAttribute("success", "Catégorie modifiée avec succès");
            return "redirect:/admin/categories/test/" + category.getTest().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/categories/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(id).orElse(null);
            Long testId = category != null ? category.getTest().getId() : null;
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Catégorie supprimée avec succès");
            if (testId != null) {
                return "redirect:/admin/categories/test/" + testId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}")
    public String viewCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Category category = categoryService.findByIdWithQuestions(id).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", "Catégorie non trouvée");
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        model.addAttribute("questionCount", categoryService.getQuestionCount(id));
        return "admin/categories/view";
    }
}