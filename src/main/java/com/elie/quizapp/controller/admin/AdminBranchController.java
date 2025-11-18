package com.elie.quizapp.controller.admin;

import com.elie.quizapp.entity.Branch;
import com.elie.quizapp.service.BranchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/branches")
public class AdminBranchController {

    private final BranchService branchService;

    public AdminBranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public String listBranches(Model model) {
        model.addAttribute("branches", branchService.findAllWithTests());
        return "admin/branches/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("branch", new Branch());
        return "admin/branches/form";
    }

    @PostMapping
    public String createBranch(@ModelAttribute Branch branch, RedirectAttributes redirectAttributes) {
        try {
            branchService.create(branch.getName());
            redirectAttributes.addFlashAttribute("success", "Branche créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/branches";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Branch branch = branchService.findById(id).orElse(null);
        if (branch == null) {
            redirectAttributes.addFlashAttribute("error", "Branche non trouvée");
            return "redirect:/admin/branches";
        }
        model.addAttribute("branch", branch);
        return "admin/branches/form";
    }

    @PostMapping("/{id}")
    public String updateBranch(@PathVariable Long id, @ModelAttribute Branch branch, 
                              RedirectAttributes redirectAttributes) {
        try {
            branchService.update(id, branch.getName());
            redirectAttributes.addFlashAttribute("success", "Branche modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/branches";
    }

    @PostMapping("/{id}/delete")
    public String deleteBranch(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            branchService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Branche supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/branches";
    }

    @GetMapping("/{id}")
    public String viewBranch(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Branch branch = branchService.findById(id).orElse(null);
        if (branch == null) {
            redirectAttributes.addFlashAttribute("error", "Branche non trouvée");
            return "redirect:/admin/branches";
        }
        model.addAttribute("branch", branch);
        return "admin/branches/view";
    }
}