package ua.com.cml.filestorage.service;

import org.springframework.data.domain.Pageable;
import ua.com.cml.filestorage.dto.FilesResponseDto;
import ua.com.cml.filestorage.model.File;

import java.util.Set;

public interface FileService {

    File uploadFile(File file);

    void deleteFileById(String id);

    Set<String> assignTags(Set<String> tags, String id);

    Set<String> removeTags(Set<String> tags, String id);

    FilesResponseDto getFilesByTagsAndName(Set<String> tags, String nameSubstring, Pageable pageable);

    File getFileById(String id);
}
