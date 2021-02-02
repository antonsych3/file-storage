package ua.com.clm.filestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.exception.BadRequestException;
import ua.com.clm.filestorage.exception.FileNotFoundException;
import ua.com.clm.filestorage.model.File;
import ua.com.clm.filestorage.repository.FileRepository;
import ua.com.clm.filestorage.type.BaseTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String FILE_NOT_FOUND = "file not found";
    private static final String TAG_NOT_FOUND = "tag not found on file";
    private static final Map<String, Enum<BaseTag>> ALL_EXTENSIONS = new HashMap<>();

    static {
        ALL_EXTENSIONS.put("mp3", BaseTag.AUDIO);
        ALL_EXTENSIONS.put("wav", BaseTag.AUDIO);
        ALL_EXTENSIONS.put("mp4", BaseTag.VIDEO);
        ALL_EXTENSIONS.put("avi", BaseTag.VIDEO);
        ALL_EXTENSIONS.put("doc", BaseTag.DOCUMENT);
        ALL_EXTENSIONS.put("pdf", BaseTag.DOCUMENT);
        ALL_EXTENSIONS.put("txt", BaseTag.DOCUMENT);
        ALL_EXTENSIONS.put("jpg", BaseTag.IMAGE);
        ALL_EXTENSIONS.put("png", BaseTag.IMAGE);
    }

    private final FileRepository fileRepository;

    @Override
    public File uploadFile(File file) {
        return fileRepository.save(addAccordingTags(file));
    }

    @Override
    public void deleteFileById(String id) {
        if (!fileRepository.existsById(id)) {
            throw new FileNotFoundException(FILE_NOT_FOUND);
        }
        fileRepository.deleteById(id);
    }

    @Override
    public void assignTags(Set<String> tags, String id) {
        fileRepository
                .findById(id)
                .map(v -> {
                    Set<String> actualTags = (v.getTags() == null) ? new HashSet<>() : v.getTags();
                    actualTags.addAll(tags);
                    v.setTags(actualTags);
                    return fileRepository.save(v);
                })
                .orElseThrow(() -> new FileNotFoundException(FILE_NOT_FOUND)
                );
    }

    @Override
    public void removeTags(Set<String> tags, String id) {
        if (tags.isEmpty()) return;
        fileRepository.findById(id)
                .map(v -> {
                    Set<String> actualTags = v.getTags();
                    if (actualTags == null || actualTags.isEmpty() || !actualTags.containsAll(tags)) {
                        throw new BadRequestException(TAG_NOT_FOUND);
                    } else {
                        actualTags.removeAll(tags);
                    }
                    v.setTags(actualTags);
                    return fileRepository.save(v);
                })
                .orElseThrow(() -> new FileNotFoundException(FILE_NOT_FOUND));
    }

    @Override
    public FilesResponseDto getFilesByTags(Set<String> tags, String nameSubstring, Pageable pageable) {
        FilesResponseDto filesResponseDto = new FilesResponseDto();
        Page<File> pageableFiles;

        if (tags.isEmpty()) {
            pageableFiles = fileRepository.findAllByNameContains(nameSubstring,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        } else {
            pageableFiles = fileRepository.findAllByTagsAndNameContains(tags, nameSubstring,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        }
        filesResponseDto.setTotal((int) pageableFiles.getTotalElements());
        filesResponseDto.setFiles(pageableFiles.getContent());
        return filesResponseDto;
    }

    private File addAccordingTags(File file) {
        if (file.getName().contains(".")) {
            String trimmedName = file.getName().trim();
            String extension = trimmedName.substring(trimmedName.lastIndexOf(".") + 1);
            Set<String> tags = new HashSet<>();
            BaseTag tag = (BaseTag) ALL_EXTENSIONS.get(extension.toLowerCase());
            if (tag != null) {
                tags.add(tag.getName());
                file.setTags(tags);
            }
        }
        return file;
    }
}
