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
import ua.com.clm.filestorage.sercice.FileService;

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
        log.info("REQUEST TO GET FILES BY TAG - {}", tags);
        FilesResponseDto filesResponseDto = fileService.getFilesByTags(tags, page, size);
        log.info("GOT {} FILES FOR REQUEST BY TAG - {}", filesResponseDto.getTotal(), tags);
        return filesResponseDto;
    }


    @PostMapping
    @ResponseBody
    public BaseFile uploadFile(@RequestBody FileRequestDto fileRequestDto) {
        log.info("REQUEST TO UPLOAD FILE - {}", fileRequestDto.getName());
        checkRequest(fileRequestDto);
        File uploadedFile = fileService.uploadFile(new File(fileRequestDto));
        log.info("FILE WITH ID = {} HAS BEEN UPLOADED TO THE STORAGE", uploadedFile.getId());
        return new FileResponseDto(uploadedFile.getId());
    }

    @DeleteMapping("/{ID}")
    @ResponseBody
    public ResponseDto deleteFile(@PathVariable("ID") String id) {
        log.info("REQUEST TO DELETE FILE WITH ID = {}", id);
        fileService.deleteFileById(id);
        log.info("DELETED FILE WITH ID = {}", id);
        return new ResponseDto(true);
    }

    @PostMapping("/{ID}/tags")
    @ResponseBody
    public ResponseDto assignTags(@RequestBody String[] tags, @PathVariable("ID") String id) {
        log.info("REQUEST TO ASSIGN TAGS - {} FOR FILE WITH ID = {}", tags, id);
        fileService.assignTags(tags, id);
        log.info("ASSIGNED TAGS - {} ON THE FILE WITH ID = {}", tags, id);
        return new ResponseDto(true);
    }

    @DeleteMapping("/{ID}/tags")
    @ResponseBody
    public ResponseDto removeTags(@RequestBody String[] tags, @PathVariable("ID") String id) {
        log.info("REQUEST TO REMOVE TAGS - {} FOR FILE WITH ID = {}", tags, id);
        fileService.removeTags(tags, id);
        log.info("REMOVED TAGS - {} FROM FILE WITH ID = {}", tags, id);
        return new ResponseDto(true);
    }

    public void checkRequest(FileRequestDto request) {
        if (request.getName() == null) {
            log.error("Some field for upload file request is empty - {}", request);
            throw new BadRequestException("empty name field");
        }
        String trimmedName = request.getName().trim();
        if (request.getSize() <= 0 || trimmedName.equals("")) {
            log.error("Incorrect value of request's field for upload file - {}", request);
            throw new BadRequestException("incorrect field");
        }
    }
}
