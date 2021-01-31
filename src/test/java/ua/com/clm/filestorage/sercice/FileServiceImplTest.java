package ua.com.clm.filestorage.sercice;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.com.clm.filestorage.exception.FileNotFoundException;
import ua.com.clm.filestorage.model.File;
import ua.com.clm.filestorage.repository.FileRepository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class FileServiceImplTest {

    @MockBean
    private FileRepository fileRepositoryMock;

    @Autowired
    private FileServiceImpl fileServiceImpl;

    @Nested
    class UploadFileTest {

        @Test
        void uploadFile_shouldReturnFile_ifParameterIsCorrect() {
            File expectedFile = new File();
            expectedFile.setId("some id");
            when(fileRepositoryMock.save(any())).thenReturn(expectedFile);
            File parameterFile = new File();
            parameterFile.setName("some name");
            parameterFile.setSize(345L);
            assertSame(expectedFile, fileServiceImpl.uploadFile(parameterFile));
            verify(fileRepositoryMock, times(1)).save(any());
        }
    }

    @Nested
    class DeleteFileByIdTest {

        @Test
        void deleteFileById_shouldDoesNotThrowException_ifFileExist() {
            doNothing().when(fileRepositoryMock).deleteById(anyString());
            when(fileRepositoryMock.existsById(anyString())).thenReturn(true);
            fileServiceImpl.deleteFileById("42");
            verify(fileRepositoryMock, times(1)).deleteById(anyString());
            verify(fileRepositoryMock, times(1)).existsById(anyString());
        }

        @Test
        void deleteFileById_shouldThrowException_ifFileDoesNotExist() {
            doNothing().when(fileRepositoryMock).deleteById(anyString());
            when(fileRepositoryMock.existsById(anyString())).thenReturn(false);
            assertThrows(FileNotFoundException.class, () -> fileServiceImpl.deleteFileById("42"));
            verify(fileRepositoryMock, times(1)).existsById(anyString());
            verify(fileRepositoryMock, times(1)).existsById(anyString());
        }
    }
}

