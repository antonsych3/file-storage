package ua.com.cml.filestorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.com.cml.filestorage.dto.ErrorResponseDto;
import ua.com.cml.filestorage.dto.FileRequestDto;
import ua.com.cml.filestorage.dto.FileResponseDto;
import ua.com.cml.filestorage.dto.FilesResponseDto;
import ua.com.cml.filestorage.exception.BadRequestException;
import ua.com.cml.filestorage.exception.FileNotFoundException;
import ua.com.cml.filestorage.handler.CustomExceptionHandler;
import ua.com.cml.filestorage.model.File;
import ua.com.cml.filestorage.service.FileService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileStorageControllerTest {

    @MockBean
    private FileService fileServiceMock;

    @Autowired
    private FileStorageController fileStorageController;

    @Nested
    class GetFileByIdTest {

        @Test
        void getFileById() throws Exception {
            File expectedFile = new File();
            expectedFile.setName("some name");
            expectedFile.setId("42");
            expectedFile.setTags(new HashSet<>());
            expectedFile.setSize(23L);
            doReturn(expectedFile).when(fileServiceMock).getFileById(anyString());
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                    .get("/file/{ID}", "42")
                    .contentType(MediaType.APPLICATION_JSON);
            standaloneSetup(fileStorageController)
                    .build()
                    .perform(builder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(content()
                            .string(Matchers.containsString(new ObjectMapper().writeValueAsString(expectedFile))));
            verify(fileServiceMock, times(1)).getFileById(anyString());
        }

        @Test
        void getFileById2() throws Exception {
            ErrorResponseDto error = new ErrorResponseDto();
            error.setError("file not found");
            error.setSuccess(false);
            doThrow(new FileNotFoundException("file not found")).when(fileServiceMock).getFileById(anyString());
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                    .get("/file/{ID}", "42")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
            standaloneSetup(fileStorageController)
                    .setControllerAdvice(CustomExceptionHandler.class)
                    .build()
                    .perform(builder)
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith("application/json"))
                    .andExpect(content()
                            .string(Matchers.containsString(new ObjectMapper().writeValueAsString(error))));
            verify(fileServiceMock, times(1)).getFileById(anyString());
        }
    }

    @Nested
    class GetFilesTest {

        @Test
        void testGetFiles_shouldReturnAllFiles_ifParametersAreDefault() throws Exception {
            FilesResponseDto filesResponseDto = new FilesResponseDto();
            filesResponseDto.setTotal(3);
            filesResponseDto.setFiles(new ArrayList<>());
            doReturn(filesResponseDto).when(fileServiceMock).getFilesByTagsAndName(anySet(), anyString(), any());
            standaloneSetup(fileStorageController)
                    .build()
                    .perform(get("/file"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(content()
                            .string(Matchers.containsString(new ObjectMapper().writeValueAsString(filesResponseDto))));
            verify(fileServiceMock, times(1)).getFilesByTagsAndName(anySet(), anyString(), any());
        }

        @Test
        void testGetFiles_shouldReturnEmptyDto_ifParametersAreNotAbleForParsing() throws Exception {
            ErrorResponseDto errorResponseDto = new ErrorResponseDto();
            errorResponseDto.setError("wrong parameters");
            MockHttpServletRequestBuilder requestBuilder = get("/file")
                    .param("size", "a")
                    .param("page", "b");
            standaloneSetup(fileStorageController)
                    .setControllerAdvice(CustomExceptionHandler.class)
                    .build()
                    .perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(content()
                            .string(Matchers.containsString(new ObjectMapper().writeValueAsString(errorResponseDto))));
            verify(fileServiceMock, times(0)).getFilesByTagsAndName(anySet(), anyString(), any());
        }
    }

    @Nested
    class UploadFileTest {

        @Test
        void uploadFile_shouldSkipAllValidation_ifParametersAreCorrect() throws Exception {
            File uploadedFile = new File();
            uploadedFile.setId("some id");
            doReturn(uploadedFile).when(fileServiceMock).uploadFile(any(File.class));
            MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
                    .post("/file")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(new FileRequestDto("MP3", 0L)));
            standaloneSetup(fileStorageController)
                    .build()
                    .perform(contentTypeResult)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(content()
                            .string(Matchers
                                    .containsString(new ObjectMapper()
                                            .writeValueAsString(new FileResponseDto(uploadedFile.getId())))));
            verify(fileServiceMock, times(1)).uploadFile(any(File.class));
        }

        @Test
        void testUploadFile_shouldReturnErrorResponse_ifSizeIsNegative() throws Exception {
            File uploadedFile = new File();
            uploadedFile.setId("some id");
            doReturn(uploadedFile).when(fileServiceMock).uploadFile(any(File.class));
            MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/file")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(new FileRequestDto("  ", -42L)));
            standaloneSetup(fileStorageController)
                    .build()
                    .perform(contentTypeResult)
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                    .andExpect(result ->
                            assertEquals("incorrect field", Objects.requireNonNull(result.getResolvedException()).getMessage()));
            verify(fileServiceMock, times(0)).uploadFile(any(File.class));
        }

        @Test
        void uploadFile_shouldReturnErrorResponse_ifNameIsNull() throws Exception {
            File uploadedFile = new File();
            uploadedFile.setId("some id");
            doReturn(uploadedFile).when(fileServiceMock).uploadFile(any(File.class));
            MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/file")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(new FileRequestDto(null, 0L)));
            standaloneSetup(fileStorageController)
                    .build()
                    .perform(contentTypeResult)
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                    .andExpect(result ->
                            assertEquals("empty name field", Objects.requireNonNull(result.getResolvedException()).getMessage()));
            verify(fileServiceMock, times(0)).uploadFile(any(File.class));
        }
    }

    @Test
    void assignTags_shouldAssignTags_ifFileByIdExist() throws Exception {
        doReturn(new HashSet<>()).when(fileServiceMock)
                .assignTags(anySet(), anyString());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/file/{ID}/tags", "some_id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new HashSet<String>()));
        standaloneSetup(fileStorageController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(Matchers.containsString("{\"success\":true}")));
        verify(fileServiceMock, times(1)).assignTags(anySet(), anyString());
    }

    @Test
    void removeTags_shouldReturnOkStatus_ifTagsAndFileExist() throws Exception {
        doReturn(new HashSet<>()).when(fileServiceMock).removeTags(anySet(), anyString());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/file/{ID}/tags", "value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new HashSet<String>()));
        standaloneSetup(fileStorageController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(Matchers.containsString("{\"success\":true}")));
        verify(fileServiceMock, times(1)).removeTags(anySet(), anyString());
    }

    @Test
    void testDeleteFile_shouldReturnStatusOk_ifRepositoryContainsIt() throws Exception {
        doNothing().when(fileServiceMock).deleteFileById(anyString());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/file/{ID}", "value")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new HashSet<String>()));
        standaloneSetup(fileStorageController)
                .build()
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(Matchers.containsString("{\"success\":true}")));
    }
}

