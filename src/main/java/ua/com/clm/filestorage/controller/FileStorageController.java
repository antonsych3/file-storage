package ua.com.clm.filestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ua.com.clm.filestorage.dto.FileRequestDto;
import ua.com.clm.filestorage.dto.FileResponseDto;
import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.dto.ResponseDto;
import ua.com.clm.filestorage.exception.BadRequestException;
import ua.com.clm.filestorage.model.File;
import ua.com.clm.filestorage.service.FileService;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileStorageController {

    private final FileService fileService;

    @GetMapping
    @ResponseBody
    public FilesResponseDto getFiles(@RequestParam(defaultValue = "", required = false) Set<String> tags,
                                     @RequestParam(name = "q", defaultValue = "", required = false) String nameSubstring,
                                     @RequestParam(defaultValue = "0", required = false) String page,
                                     @RequestParam(defaultValue = "10", required = false) String size) {
        log.info("[x]Request to get files by tags - {} and name - {}", tags, nameSubstring);
        FilesResponseDto filesResponseDto;
        try {
            int pageNumber = safeParseInt(page);
            int sizeNumber = safeParseInt(size);
            filesResponseDto = fileService.getFilesByTags(tags, nameSubstring,
                    PageRequest.of(pageNumber, sizeNumber));
        } catch (RuntimeException e) {
            log.warn("Some argument is wrong or here is no connection");
            return new FilesResponseDto();
        }
        log.info("[x]Got {} files by tags - {} and name - {}", filesResponseDto.getTotal(), tags, nameSubstring);
        return filesResponseDto;
    }

    @PostMapping
    @ResponseBody
    public FileResponseDto uploadFile(@RequestBody FileRequestDto fileRequestDto) {
        log.info("[x]Request to upload file with name - {}", fileRequestDto.getName());
        checkRequest(fileRequestDto);
        File uploadedFile = fileService.uploadFile(new File(fileRequestDto));
        log.info("[x]File with id = {} has been uploaded to the storage", uploadedFile.getId());
        return new FileResponseDto(uploadedFile.getId());
    }

    @DeleteMapping("/{ID}")
    @ResponseBody
    public ResponseDto deleteFile(@PathVariable("ID") String id) {
        log.info("[x]Request to delete file with id = {}", id);
        fileService.deleteFileById(id);
        log.info("[x]Deleted file with id = {}", id);
        return new ResponseDto(true);
    }

    @PostMapping("/{ID}/tags")
    @ResponseBody
    public ResponseDto assignTags(@RequestBody Set<String> tags, @PathVariable("ID") String id) {
        log.info("[x]Request to assign tags - {} for file with id = {}", tags, id);
        fileService.assignTags(tags, id);
        log.info("[x]Assigned tags - {} on the file with id = {}", tags, id);
        return new ResponseDto(true);
    }

    @DeleteMapping("/{ID}/tags")
    @ResponseBody
    public ResponseDto removeTags(@RequestBody Set<String> tags, @PathVariable("ID") String id) {
        log.info("[x]Request to remove tags - {} for file with id = {}", tags, id);
        fileService.removeTags(tags, id);
        log.info("[x]Removed tags - {} from file with id = {}", tags, id);
        return new ResponseDto(true);
    }

    private int safeParseInt(String text) {
        int number = Integer.parseInt(text);
        if (number < 0) throw new NumberFormatException("Negative size and page");
        return number;
    }

    private void checkRequest(FileRequestDto request) {
        if (request.getName() == null) {
            log.error("[x]Some field for upload file request is empty - {}", request);
            throw new BadRequestException("empty name field");
        }
        if (request.getSize() < 0 || request.getName().isBlank()) {
            log.error("[x]Incorrect value of request's field for upload file - {}", request);
            throw new BadRequestException("incorrect field");
        }
    }
}
