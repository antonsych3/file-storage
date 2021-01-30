package ua.com.clm.filestorage.sercice;

import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.model.File;

public interface FileService {

    File uploadFile(File file);

    void deleteFileById(String id);

    void assignTags(String[] tags, String id);

    void removeTags(String[] tags, String id);

    FilesResponseDto getFilesByTags(String[] tags, int page, int size);
}
