package ua.com.clm.filestorage.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.com.clm.filestorage.dto.ErrorResponseDto;
import ua.com.clm.filestorage.exception.BadRequestException;
import ua.com.clm.filestorage.exception.FileNotFoundException;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadRequestException.class, FileNotFoundException.class})
    public final ResponseEntity<ErrorResponseDto> handleBadRequestException(RuntimeException exception) {
        return new ResponseEntity<>(
                new ErrorResponseDto(false, exception.getLocalizedMessage()),
                exception instanceof BadRequestException ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND);
    }
}