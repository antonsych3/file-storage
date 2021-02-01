package ua.com.clm.filestorage.service;

import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.model.File;

import java.util.LinkedHashSet;
import java.util.Set;

public interface FileService {

    File uploadFile(File file);

    void deleteFileById(String id);

    void assignTags(LinkedHashSet<String> tags, String id);

    void removeTags(Set<String> tags, String id);

    FilesResponseDto getFilesByTags(Set<String> tags, String nameSubstring, int page, int size);
}
