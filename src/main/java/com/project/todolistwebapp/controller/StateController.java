package com.project.todolistwebapp.controller;

import com.project.todolistwebapp.dto.StateDto;
import com.project.todolistwebapp.model.State;
import com.project.todolistwebapp.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/states")
@RequiredArgsConstructor
public class StateController {

    private final StateService stateService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public String listStates(Model model) {
        model.addAttribute("states", stateService.findAll());
        return "state/state-list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createState(Model model) {
        model.addAttribute("state", new State());
        return "state/create-state";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createState(@Validated @ModelAttribute("state") State state, BindingResult result) {
        if (result.hasErrors()) {
            return "state/create-state";
        }
        stateService.create(state);
        return "redirect:/states/all";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/rename")
    public String renameState(@PathVariable Long id, Model model) {
        State state = stateService.readById(id);
        model.addAttribute("state", state);
        return "state/rename-state";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/rename")
    public String renameState(@Validated @ModelAttribute("state") State renamedState, BindingResult result) {
        if (result.hasErrors()) {
            return "state/rename-state";
        }

        stateService.update(renamedState);
        return "redirect:/states/all";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/delete")
    public String deleteState(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            stateService.delete(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error","Cannot delete state: it has linked tasks.");
            return "redirect:/states/all";
        }
        return "redirect:/states/all";
    }
}
