package ua.com.clm.filestorage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ua.com.clm.filestorage.dto.FileRequestDto;
import ua.com.clm.filestorage.dto.FileResponseDto;
import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.dto.ResponseDto;
import ua.com.clm.filestorage.exception.BadRequestException;
import ua.com.clm.filestorage.model.BaseFile;
import ua.com.clm.filestorage.model.File;
import ua.com.clm.filestorage.service.FileService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileStorageController {

    private final FileService fileService;

    @GetMapping
    @ResponseBody
    public FilesResponseDto getFiles(@RequestParam(defaultValue = "", required = false) List<String> tags,
                                     @RequestParam(defaultValue = "0", required = false) int page,
                                     @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("[x]Request to get files by tags - {}", tags);
        FilesResponseDto filesResponseDto = fileService.getFilesByTags(tags, page, size);
        log.info("[x]Got {} files by tags - {}", filesResponseDto.getTotal(), tags);
        return filesResponseDto;
    }


    @PostMapping
    @ResponseBody
    public BaseFile uploadFile(@RequestBody FileRequestDto fileRequestDto) {
        log.info("[x]Request to upload file with name - {}", fileRequestDto.getName());
        checkRequest(fileRequestDto);
        File uploadedFile = fileService.uploadFile(new File(fileRequestDto));
        log.info("[x]File with id = {} has been upload to the storage", uploadedFile.getId());
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
    public ResponseDto assignTags(@RequestBody List<String> tags, @PathVariable("ID") String id) {
        log.info("[x]Request to assign tags - {} for file with id = {}", tags, id);
        fileService.assignTags(tags, id);
        log.info("[x]Assigned tags - {} on the file with id = {}", tags, id);
        return new ResponseDto(true);
    }

    @DeleteMapping("/{ID}/tags")
    @ResponseBody
    public ResponseDto removeTags(@RequestBody List<String> tags, @PathVariable("ID") String id) {
        log.info("[x]Request to remove tags - {} for file with id = {}", tags, id);
        fileService.removeTags(tags, id);
        log.info("[x]Removed tags - {} from file with id = {}", tags, id);
        return new ResponseDto(true);
    }

    private void checkRequest(FileRequestDto request) {
        if (request.getName() == null) {
            log.error("[x]Some field for upload file request is empty - {}", request);
            throw new BadRequestException("empty name field");
        }
        String trimmedName = request.getName().trim();
        if (request.getSize() < 0 || trimmedName.equals("")) {
            log.error("[x]Incorrect value of request's field for upload file - {}", request);
            throw new BadRequestException("incorrect field");
        }
    }
}
