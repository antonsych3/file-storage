package ua.com.clm.filestorage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.dto.UploadFileRequestDto;
import ua.com.clm.filestorage.dto.UploadFileResponseDto;
import ua.com.clm.filestorage.dto.ResponseDto;
import ua.com.clm.filestorage.exception.BadRequestException;
import ua.com.clm.filestorage.exception.FileNotFoundException;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileStorageController {

    @GetMapping("")
    public ResponseEntity<FilesResponseDto> getFiles(@RequestParam(defaultValue = "", required = false) String[] tags,
                                                     @RequestParam(defaultValue = "0", required = false) int page,
                                                     @RequestParam(defaultValue = "10", required = false) int size) {
        System.out.println(Arrays.toString(tags) + " " + page + " " + size);
        return new ResponseEntity<>(new FilesResponseDto(), HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<UploadFileResponseDto> uploadFile(@RequestBody UploadFileRequestDto uploadFileRequestDto) {
        log.info("REQUEST TO UPLOAD FILE - {}", uploadFileRequestDto.getName());
        if (uploadFileRequestDto.getSize() == 1) {
            log.error("Bad request body {}", uploadFileRequestDto);
            throw new BadRequestException("bad request");
        }
        return new ResponseEntity<>(new UploadFileResponseDto(uploadFileRequestDto.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/{ID}")
    public ResponseEntity<ResponseDto> deleteFile(@PathVariable("ID") String id) {
        log.info("REQUEST TO DELETE FILE WITH ID = {}", id);
        if (id.equals("1")) {
            log.error("File with id = {} is not found", id);
            throw new FileNotFoundException("file not found");
        }
        return new ResponseEntity<>(new ResponseDto(true), HttpStatus.OK);
    }

    @PostMapping("/{ID}/tags")
    public ResponseEntity<ResponseDto> assignTags(@RequestBody String[] tags, @PathVariable("ID") String id) {
        log.info("REQUEST TO ASSIGN TAGS FOR FILE WITH ID = {}", id);
        if (id.equals("1")) {
            log.error("File with id = {} is not found", id);
            throw new FileNotFoundException("file not found");
        }
        return new ResponseEntity<>(new ResponseDto(true), HttpStatus.OK);
    }

    @DeleteMapping("/{ID}/tags")
    public ResponseEntity<ResponseDto> removeTags(@RequestBody String[] tags, @PathVariable("ID") String id) {
        log.info("REQUEST TO REMOVE TAGS FOR FILE WITH ID = {}", id);
        if (id.equals("1")) {
            log.error("Some tag is not found on the file with id = {}", id);
            throw new BadRequestException("tag not found on file");
        } else if (id.equals("2")) {
            log.error("File with id = {} is not found", id);
            throw new FileNotFoundException("file not found");
        }
        return new ResponseEntity<>(new ResponseDto(true), HttpStatus.OK);
    }
}
