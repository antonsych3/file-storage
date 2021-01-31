package ua.com.clm.filestorage.sercice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.exception.BadRequestException;
import ua.com.clm.filestorage.exception.FileNotFoundException;
import ua.com.clm.filestorage.model.File;
import ua.com.clm.filestorage.repository.FileRepository;
import ua.com.clm.filestorage.type.BaseTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void assignTags(List<String> tags, String id) {
        fileRepository
                .findById(id)
                .map(v -> {
                    List<String> actualTags = v.getTags();
                    if (actualTags == null) {
                        log.debug("[x]Here is no tag on this file ({})", id);
                        actualTags = new ArrayList<>();
                    }
                    actualTags.addAll(tags);
                    v.setTags(actualTags);
                    return fileRepository.save(v);
                })
                .orElseThrow(() -> new FileNotFoundException(FILE_NOT_FOUND)
                );
    }

    @Override
    public void removeTags(List<String> tags, String id) {
        if (tags.isEmpty()) return;
        fileRepository.findById(id)
                .map(v -> {
                    List<String> actualTags = v.getTags();
                    if (actualTags == null || actualTags.isEmpty()) {
                        throw new BadRequestException(TAG_NOT_FOUND);
                    }
                    for (String tag : tags) {
                        if (actualTags.contains(tag)) {
                            actualTags.remove(tag);
                        } else {
                            throw new BadRequestException(TAG_NOT_FOUND);
                        }
                    }
                    v.setTags(actualTags);
                    return fileRepository.save(v);
                })
                .orElseThrow(() -> new FileNotFoundException(FILE_NOT_FOUND));
    }

    @Override
    public FilesResponseDto getFilesByTags(List<String> tags, int page, int size) {
        FilesResponseDto filesResponseDto = new FilesResponseDto();
        Page<File> pageableFiles;

        if (tags.isEmpty()) {
            pageableFiles = fileRepository.findAll(PageRequest.of(page, size));
        } else {
            pageableFiles = fileRepository.findAllByTags(tags, PageRequest.of(page, size));
        }
        filesResponseDto.setTotal((int) pageableFiles.getTotalElements());
        filesResponseDto.setFiles(pageableFiles.getContent());
        return filesResponseDto;
    }

    private File addAccordingTags(File file) {
        String[] nameArray = file.getName().trim().split("\\.");
        String extension = nameArray[nameArray.length - 1];
        List<String> tags = new ArrayList<>();
        BaseTag tag = (BaseTag) ALL_EXTENSIONS.get(extension);
        if (tag != null) {
            tags.add(tag.getName());
            file.setTags(tags);
        }
        return file;
    }
}
