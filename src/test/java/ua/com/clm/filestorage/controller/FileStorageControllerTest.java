package ua.com.clm.filestorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.clm.filestorage.sercice.FileService;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@SpringBootTest
class FileStorageControllerTest {

    @MockBean
    private FileService fileServiceMock;

    @Autowired
    private FileStorageController fileStorageController;


    @BeforeEach
    void init() {
        doNothing().when(fileServiceMock).removeTags(anyList(), anyString());
    }

    @Test
    void assignTags_shouldReturnOkStatus_ifFileExist() throws Exception {
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
                .post("/file/{ID}/tags", "file_id")
                .contentType(MediaType.APPLICATION_JSON);
        sendRequest(contentTypeResult);
        verify(fileServiceMock, times(1)).assignTags(anyList(), anyString());
    }

    @Test
    void removeTags_shouldReturnOkStatus_ifFileAndTagsExist() throws Exception {
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
                .delete("/file/{ID}/tags", "file_id")
                .contentType(MediaType.APPLICATION_JSON);
        sendRequest(contentTypeResult);
        verify(fileServiceMock, times(1)).removeTags(anyList(), anyString());
    }

    private void sendRequest(MockHttpServletRequestBuilder contentTypeResult) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(new ObjectMapper().writeValueAsString(new ArrayList<String>()));
        MockMvcBuilders.standaloneSetup(fileStorageController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(Matchers.containsString("{\"success\":true}")));
    }
}

