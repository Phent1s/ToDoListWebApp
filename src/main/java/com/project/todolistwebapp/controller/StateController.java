package com.project.todolistwebapp.controller;

import com.project.todolistwebapp.model.State;
import com.project.todolistwebapp.service.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/states")
@RequiredArgsConstructor
public class StateController {

    private final StateService stateService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String listStates(Model model) {
        model.addAttribute("states", stateService.findAll());
        log.info("State list page loaded");
        return "state/state-list";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/create")
    public String createState(Model model) {
        model.addAttribute("state", new State());
        log.info("Create state page loaded");
        return "state/create-state";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public String createState(@Validated @ModelAttribute("state") State state, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Create validation error {}", result.getAllErrors());
            return "state/create-state";
        }
        stateService.create(state);
        log.info("State created");
        return "redirect:/states/all";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/rename")
    public String renameState(@PathVariable Long id, Model model) {
        State state = stateService.readById(id);
        model.addAttribute("state", state);
        log.info("Rename state page loaded");
        return "state/rename-state";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/rename")
    public String renameState(@Validated @ModelAttribute("state") State renamedState, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Rename validation error {}", result.getAllErrors());
            return "state/rename-state";
        }

        stateService.update(renamedState);
        log.info("State renamed");
        return "redirect:/states/all";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/delete")
    public String deleteState(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            stateService.delete(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error","Cannot delete state: it has linked tasks.");
            log.error("Cannot delete state: {}", e.getMessage());
            return "redirect:/states/all";
        }
        log.info("State deleted");
        return "redirect:/states/all";
    }
}
