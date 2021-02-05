package ua.com.cml.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ua.com.cml.filestorage.model.File;

import java.util.ArrayList;
import java.util.List;

@Data
public class FilesResponseDto {

    private int total;
    @JsonProperty("page")
    private List<File> files = new ArrayList<>();
}
