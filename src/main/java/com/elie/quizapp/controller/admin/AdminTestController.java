package com.elie.quizapp.controller.admin;

import com.elie.quizapp.entity.Test;
import com.elie.quizapp.service.TestService;
import com.elie.quizapp.service.BranchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tests")
public class AdminTestController {

    private final TestService testService;
    private final BranchService branchService;

    public AdminTestController(TestService testService, BranchService branchService) {
        this.testService = testService;
        this.branchService = branchService;
    }

    @GetMapping
    public String listTests(Model model) {
        model.addAttribute("tests", testService.findAllWithCategories());
        return "admin/tests/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("test", new Test());
        model.addAttribute("branches", branchService.findAll());
        return "admin/tests/form";
    }

    @PostMapping
    public String createTest(@RequestParam String name, @RequestParam Long branchId, 
                            RedirectAttributes redirectAttributes) {
        try {
            testService.create(name, branchId);
            redirectAttributes.addFlashAttribute("success", "Test créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/tests";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Test test = testService.findById(id).orElse(null);
        if (test == null) {
            redirectAttributes.addFlashAttribute("error", "Test non trouvé");
            return "redirect:/admin/tests";
        }
        model.addAttribute("test", test);
        model.addAttribute("branches", branchService.findAll());
        return "admin/tests/form";
    }

    @PostMapping("/{id}")
    public String updateTest(@PathVariable Long id, @RequestParam String name, 
                            @RequestParam Long branchId, RedirectAttributes redirectAttributes) {
        try {
            testService.update(id, name, branchId);
            redirectAttributes.addFlashAttribute("success", "Test modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/tests";
    }

    @PostMapping("/{id}/delete")
    public String deleteTest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            testService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Test supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/tests";
    }

    @GetMapping("/{id}")
    public String viewTest(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Test test = testService.findByIdWithCategories(id).orElse(null);
        if (test == null) {
            redirectAttributes.addFlashAttribute("error", "Test non trouvé");
            return "redirect:/admin/tests";
        }
        model.addAttribute("test", test);
        model.addAttribute("questionCount", testService.getQuestionCount(id));
        model.addAttribute("isReady", testService.isTestReady(id));
        return "admin/tests/view";
    }

    @GetMapping("/branch/{branchId}")
    public String listTestsByBranch(@PathVariable Long branchId, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("tests", testService.findByBranchId(branchId));
            model.addAttribute("branch", branchService.findById(branchId).orElse(null));
            return "admin/tests/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du chargement des tests");
            return "redirect:/admin/branches";
        }
    }
}