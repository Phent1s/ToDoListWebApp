package com.project.todolistwebapp.config.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NullEntityReferenceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ModelAndView nullEntityReferenceExceptionHandler(HttpServletRequest request, NullEntityReferenceException ex) {
        return getModelAndView(request, HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ModelAndView accessForbiddenExceptionHandler(HttpServletRequest request, AccessDeniedException ex) {
        return getModelAndView(request, HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ModelAndView entityNotFoundExceptionHandler(HttpServletRequest request, EntityNotFoundException ex) {
        return getModelAndView(request, HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView internalServerErrorHandler(HttpServletRequest request, Exception ex) {
        return getModelAndView(request, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ModelAndView getModelAndView(HttpServletRequest request, HttpStatus httpStatus, Exception exception) {
        log.error("Exception raised = {} :: URL = {}", exception.getMessage(), request.getRequestURI(), exception);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("code", httpStatus.value() + " / " + httpStatus.getReasonPhrase());
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }
}
