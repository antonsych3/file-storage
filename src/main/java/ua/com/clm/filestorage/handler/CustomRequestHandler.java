package ua.com.clm.filestorage.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.clm.filestorage.configuration.ErrorMessages;
import ua.com.clm.filestorage.dto.FileRequestDto;
import ua.com.clm.filestorage.exception.BadRequestException;

@Slf4j
@Service
public class CustomRequestHandler {

    public void checkRequest(FileRequestDto request) {
        if (request.getName() == null) {
            log.error("Some field for upload file request is empty - {}", request);
            throw new BadRequestException(ErrorMessages.EMPTY_FIELD);
        }
        String trimmedName = request.getName().trim();
        if (request.getSize() <= 0 || trimmedName.equals("")) {
            log.error("Incorrect value of request's field for upload file - {}", request);
            throw new BadRequestException(ErrorMessages.INCORRECT_FIELD);
        }
    }
}
