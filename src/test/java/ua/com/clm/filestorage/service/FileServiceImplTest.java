package ua.com.clm.filestorage.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.com.clm.filestorage.dto.FilesResponseDto;
import ua.com.clm.filestorage.exception.BadRequestException;
import ua.com.clm.filestorage.exception.FileNotFoundException;
import ua.com.clm.filestorage.model.File;
import ua.com.clm.filestorage.repository.FileRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileServiceImplTest {

    @MockBean
    private FileRepository fileRepositoryMock;

    @Autowired
    private FileService fileService;

    @Nested
    class GetFilesByTagsAndNameTest {

        Page<File> pageableFiles;
        FilesResponseDto expectedResponseDto;

        @BeforeEach
        void init() {
            expectedResponseDto = new FilesResponseDto();
            expectedResponseDto.setTotal(3);
            expectedResponseDto.setFiles(new ArrayList<>());
            pageableFiles = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 3L);
        }

        @Test
        void getFilesByTagsAndName_shouldFindByName_ifTagsDontExist() {
            doReturn(pageableFiles)
                    .when(fileRepositoryMock)
                    .findAllByNameContains(anyString(), any(PageRequest.class));
            assertEquals(expectedResponseDto, fileService.getFilesByTagsAndName(
                    new HashSet<>(),
                    "aaaa",
                    PageRequest.of(0, 10)));
            verify(fileRepositoryMock, times(1))
                    .findAllByNameContains(anyString(), any(PageRequest.class));
        }

        @Test
        void getFilesByTagsAndName_shouldFindByNameAndTags_ifTagsExist() {
            doReturn(pageableFiles)
                    .when(fileRepositoryMock)
                    .findAllByTagsAndNameContains(anySet(), anyString(), any(PageRequest.class));
            assertEquals(expectedResponseDto, fileService.getFilesByTagsAndName(
                    new HashSet<>(Arrays.asList("tag1", "tag2")),
                    "aaaa",
                    PageRequest.of(0, 10)));
            verify(fileRepositoryMock, times(1))
                    .findAllByTagsAndNameContains(anySet(), anyString(), any(PageRequest.class));
        }
    }

    @Nested
    class UploadFileTest {

        private File expectedFile;

        @BeforeEach
        void init() {
            expectedFile = new File();
            expectedFile.setId("some id");
            expectedFile.setSize(0L);
        }

        @Test
        void uploadFile_shouldReturnFile_ifParameterIsCorrect() {
            Set<String> tags = new HashSet<>();
            tags.add("audio");
            expectedFile.setTags(tags);
            File parameterFile = new File();
            parameterFile.setName("AAA.MP3");

            when(fileRepositoryMock.save(any(File.class))).thenReturn(expectedFile);
            File actualFile = fileService.uploadFile(parameterFile);
            assertSame(expectedFile, actualFile);
            assertTrue(actualFile.getTags().contains("audio") && actualFile.getTags().size() == 1);
            verify(fileRepositoryMock, times(1)).save(any(File.class));
        }

        @Test
        void uploadFile_shouldReturnFileWithoutExtension_ifParameterDoesNotContainDot() {
            Set<String> tags = new HashSet<>();
            expectedFile.setTags(tags);
            File parameterFile = new File();
            parameterFile.setName("MP3");

            when(fileRepositoryMock.save(any(File.class))).thenReturn(expectedFile);
            File actualFile = fileService.uploadFile(parameterFile);
            assertSame(expectedFile, actualFile);
            assertTrue(actualFile.getTags().isEmpty());
            verify(fileRepositoryMock, times(1)).save(any(File.class));
        }
    }

    @Nested
    class DeleteFileByIdTest {

        @BeforeEach
        void invokeMock() {
            doNothing().when(fileRepositoryMock).deleteById(anyString());
        }

        @Test
        void deleteFileById_shouldDoesNotThrowException_ifFileExist() {
            when(fileRepositoryMock.existsById(anyString())).thenReturn(true);
            assertDoesNotThrow(() -> fileService.deleteFileById("42"));
        }

        @Test
        void deleteFileById_shouldThrowException_ifFileDoesNotExist() {
            when(fileRepositoryMock.existsById(anyString())).thenReturn(false);
            assertThrows(FileNotFoundException.class, () -> fileService.deleteFileById("42"));
        }

        @AfterEach
        void verifyMocks() {
            verify(fileRepositoryMock, times(1)).existsById(anyString());
            verify(fileRepositoryMock, times(1)).existsById(anyString());
        }
    }

    @Nested
    class AssignTagsTest {

        @Test
        void testAssignTags_shouldThrowException_ifFileDoesNotExist() {
            when(fileRepositoryMock.save(any(File.class))).thenReturn(new File());
            when(fileRepositoryMock.findById(anyString())).thenReturn(Optional.<File>empty());
            assertThrows(FileNotFoundException.class, () -> fileService.assignTags(new HashSet<>(), "42"));
            verify(fileRepositoryMock, times(0)).save(any(File.class));
            verify(fileRepositoryMock, times(1)).findById(anyString());
        }

        @Test
        void testAssignTags_shouldReturnTheSameSetWithoutReplacing_ifRepeatingAddition() {
            Set<String> tagsForAddition = new HashSet<>();
            tagsForAddition.add("tag1");
            tagsForAddition.add("tag2");
            Set<String> actualTags = new HashSet<>();
            actualTags.add("tag1");
            actualTags.add("tag2");
            actualTags.add("tag3");
            File file = new File();
            file.setTags(actualTags);
            Optional<File> ofResult = Optional.<File>of(file);

            when(fileRepositoryMock.save(any(File.class))).thenReturn(file);
            when(fileRepositoryMock.findById(anyString())).thenReturn(ofResult);
            assertSame(actualTags, fileService.assignTags(tagsForAddition, "42"));
            verify(fileRepositoryMock, times(1)).save(any(File.class));
            verify(fileRepositoryMock, times(1)).findById(anyString());
        }
    }

    @Nested
    class RemoveTagsTest {

        @Test
        void removeTags_shouldRemoveTags_ifSetContainsAllOfThem() {
            Set<String> actualTags = new HashSet<>();
            actualTags.add("tag1");
            actualTags.add("tag2");
            File file = new File();
            file.setTags(actualTags);

            when(fileRepositoryMock.save(any(File.class))).thenReturn(file);
            when(fileRepositoryMock.findById(anyString())).thenReturn(Optional.<File>of(file));
            assertEquals(new HashSet<>(), fileService.removeTags(actualTags, "42"));
            verify(fileRepositoryMock, times(1)).save(any(File.class));
            verify(fileRepositoryMock, times(1)).findById(anyString());
        }

        @Test
        void removeTags_shouldThrowException_ifFileDoesNotContainSomeTag() {
            Set<String> actualTags = new HashSet<>();
            actualTags.add("tag1");
            actualTags.add("tag2");
            File file = new File();
            file.setTags(actualTags);
            Set<String> setFotRemoving = new HashSet<>(Collections.singletonList("tag3"));

            when(fileRepositoryMock.save(any(File.class))).thenReturn(file);
            when(fileRepositoryMock.findById(anyString())).thenReturn(Optional.<File>of(file));
            assertThrows(BadRequestException.class, () -> fileService.removeTags(setFotRemoving, "42"));
            verify(fileRepositoryMock, times(0)).save(any(File.class));
            verify(fileRepositoryMock, times(1)).findById(anyString());
        }

        @Test
        void removeTags_shouldThrowException_ifFileDoesNotExist() {
            Set<String> setFotRemoving = new HashSet<>(Collections.singletonList("tag3"));

            when(fileRepositoryMock.save(any(File.class))).thenReturn(new File());
            when(fileRepositoryMock.findById(anyString())).thenReturn(Optional.<File>empty());
            assertThrows(FileNotFoundException.class, () -> fileService.removeTags(setFotRemoving, "42"));
            verify(fileRepositoryMock, times(0)).save(any(File.class));
            verify(fileRepositoryMock, times(1)).findById(anyString());
        }
    }
}

