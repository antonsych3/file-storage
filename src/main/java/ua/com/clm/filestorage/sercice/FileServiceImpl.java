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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    public File uploadFile(File file) {
        return fileRepository.save(file);
    }

    @Override
    public void deleteFileById(String id) {
        if (!fileRepository.existsById(id)) {
            throwFileNotFoundException(id);
        }
        fileRepository.deleteById(id);
    }

    @Override
    public void assignTags(String[] tags, String id) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            List<String> actualTags = file.getTags();
            if (actualTags == null) {
                log.debug("Here is no tag(-s) on this file ({})", id);
                actualTags = new ArrayList<>();
            }
            actualTags.addAll(Arrays.asList(tags));
            file.setTags(actualTags);
            fileRepository.save(file);
        } else {
            throwFileNotFoundException(id);
        }
    }

    @Override
    public void removeTags(String[] tags, String id) {
        if (tags.length == 0) return;
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            List<String> actualTags = file.getTags();
            if (actualTags == null || actualTags.isEmpty()) {
                throwBadRequestException(id);
            }
            for (String tag : tags) {
                if (actualTags.contains(tag)) {
                    actualTags.remove(tag);
                } else {
                    throwBadRequestException(id);
                }
            }
            file.setTags(actualTags);
            fileRepository.save(file);
        } else {
            throwFileNotFoundException(id);
        }
    }

    public FilesResponseDto getFilesByTags(List<String> tags, int page, int size) {
        FilesResponseDto resultDto = new FilesResponseDto();
        Page<File> pageableFiles;

        if (tags.size() == 0) {
            pageableFiles = fileRepository.findAll(PageRequest.of(page, size));
        } else {
            pageableFiles = fileRepository.findAllByTags(tags, PageRequest.of(page, size));
        }
        resultDto.setTotal((int) pageableFiles.getTotalElements());
        resultDto.setFiles(pageableFiles.getContent());
        return resultDto;
    }

    private void throwBadRequestException(String id) {
        log.error("Here is no those tags on this file ({})", id);
        throw new BadRequestException("tag not found on file");
    }

    private void throwFileNotFoundException(String id) {
        log.error("File with id = {} does not exist", id);
        throw new FileNotFoundException("file not found");
    }
}
