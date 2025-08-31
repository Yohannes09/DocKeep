package com.dockeep.config;

import com.authmat.tool.exception.BaseGlobalExceptionHandler;
import com.authmat.tool.exception.ErrorResponse;
import com.dockeep.document.exception.DocumentNotFoundException;
import com.authmat.tool.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {
    @ExceptionHandler({DocumentNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> resourceNotFoundExceptions(HttpServletRequest request, Exception exception){
        return generateErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, request);
    }
}
